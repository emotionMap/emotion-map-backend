package com.emotionmap.business.auth.service;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.auth.payLoad.AuthLoginResponse;
import com.emotionmap.business.auth.vo.SocialUserInfoVo;
import com.emotionmap.business.auth.vo.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final SocialAuthService socialAuthService;

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
            }

            return new AuthLoginResponse(
                    user.getProvider(),
                    user.getProviderUserId(),
                    user.getStatus()
            );
    }
}
