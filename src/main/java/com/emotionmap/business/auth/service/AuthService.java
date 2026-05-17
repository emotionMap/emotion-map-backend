package com.emotionmap.business.auth.service;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.auth.payLoad.AuthLoginResponse;
import com.emotionmap.business.auth.vo.JWTToken;
import com.emotionmap.business.auth.vo.SocialUserInfoVo;
import com.emotionmap.business.auth.vo.UserVo;
import com.emotionmap.business.jwt.provider.JwtProvider;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final SocialAuthService socialAuthService;
    private final JwtProvider jwtProvider;

    public AuthLoginResponse login(String provider, String socialAccessToken) {

        // 1. 소셜 토큰 검증 + 사용자 정보 조회
        SocialUserInfoVo socialUser =
                socialAuthService.getUserInfo(provider, socialAccessToken);

        // 2. 기존 유저 조회
        UserVo user = userMapper
                .findByProviderAndProviderUserId(provider, socialUser.getId());

        // 3. 없으면 신규 생성
        if (user == null) {
            user = UserVo.newSocialUser(provider, socialUser.getId());
            userMapper.insertUserInfo(user);
            log.info("[Auth] 신규 유저 생성 - provider: {}, providerUserId: {}", provider, socialUser.getId());
        }

        // 4. JWT 발급 + RT DB 저장
        JWTToken token = issueAndSaveTokens(user);
        log.info("[Auth] 로그인 성공 - userId: {}, status: {}", user.getId(), user.getStatus());

        return new AuthLoginResponse(user.getStatus(), token);
    }

    public JWTToken refresh(String refreshToken) {

        // 1. RT 서명/만료 검증
        Long userId;
        try {
            userId = jwtProvider.parseSubject(refreshToken);
        } catch (Exception e) {
            log.warn("[Auth] Refresh Token 서명/만료 검증 실패");
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. DB의 RT와 일치 여부 확인 (로그아웃된 토큰 차단)
        UserVo user = userMapper.findById(userId);
        if (user == null || !refreshToken.equals(user.getRefreshToken())) {
            log.warn("[Auth] Refresh Token 불일치 - userId: {}", userId);
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3. 새 AT + RT 발급 (Rotation)
        return issueAndSaveTokens(user);
    }

    public void logout(Long userId) {
        userMapper.clearRefreshToken(userId);
    }

    public JWTToken issueAndSaveTokens(UserVo user) {
        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = jwtProvider.createRefreshToken(user);
        userMapper.updateRefreshToken(user.getId(), refreshToken, LocalDateTime.now().plusDays(90));
        return new JWTToken(accessToken, refreshToken);
    }
}
