package com.emotionmap.business.auth.service;

import com.emotionmap.business.auth.vo.SocialUserInfoVo;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialAuthService {

    private final KakaoAuthClient kakaoAuthClient;
    private final NaverAuthClient naverAuthClient;
    private final AppleAuthClient appleAuthClient;

    public SocialUserInfoVo getUserInfo(String provider, String accessToken) {

        switch (provider.toLowerCase()) {
            case "KAKAO":
                return kakaoAuthClient.getUserInfo(accessToken);
            case "NAVER":
                return naverAuthClient.getUserInfo(accessToken);
            case "APPLE":
                return appleAuthClient.getUserInfo(accessToken);
            default:
                throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }
}
