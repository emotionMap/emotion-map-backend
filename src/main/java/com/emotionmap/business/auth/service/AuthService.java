package com.emotionmap.business.auth.service;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.auth.payLoad.AuthLoginResponse;
import com.emotionmap.business.auth.vo.JWTToken;
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
    private final JwtProvider jwtProvider;

    public AuthLoginResponse login(String deviceId) {

        if (deviceId == null || deviceId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_LOGIN_REQUEST);
        }

        // 1. 기존 유저 조회
        UserVo user = userMapper.findByDeviceId(deviceId);

        // 2. 없으면 신규(익명) 생성
        if (user == null) {
            user = UserVo.newAnonymousUser(deviceId);
            userMapper.insertUserInfo(user);
            log.info("[Auth] 신규 익명 유저 생성 - deviceId: {}", deviceId);
        }

        // 3. JWT 발급 + RT DB 저장
        JWTToken token = issueAndSaveTokens(user);
        log.info("[Auth] 로그인 성공 - userId: {}", user.getId());

        return new AuthLoginResponse(user.hasLocation(), token);
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
