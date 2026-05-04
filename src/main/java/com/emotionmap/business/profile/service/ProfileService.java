package com.emotionmap.business.profile.service;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.jwt.provider.JwtProvider;
import com.emotionmap.business.auth.vo.JWTToken;
import com.emotionmap.business.auth.vo.UserVo;
import com.emotionmap.business.profile.mapper.ProfilerMapper;
import com.emotionmap.business.profile.payload.ProfileRequest;
import com.emotionmap.business.profile.payload.ProfileResponse;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserMapper userMapper;
    private final JwtProvider jwtProvider;
    private final ProfilerMapper profilerMapper;

    public ProfileResponse reg(ProfileRequest request) {
        // 사용자 식별
        UserVo user = userMapper.findByProviderAndProviderUserId(
                request.getProvider(),
                request.getProviderUserId()
        );

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FIND_USER_INFO);
        }
        if (user.isActive()) {
            throw new BusinessException(ErrorCode.EXAMPLE0);
        }

        // 프로필 정보 업데이트
        user.activate();
        profilerMapper.updateProfile(request);

        // 토큰 발급
        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = jwtProvider.createRefreshToken(user);

        JWTToken token = new JWTToken(accessToken, refreshToken);

        return new ProfileResponse(
                user.getStatus(),
                token
        );
    }
}
