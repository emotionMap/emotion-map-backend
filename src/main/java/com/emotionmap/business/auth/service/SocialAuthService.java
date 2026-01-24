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

    public SocialUserInfoVo getUserInfo(String provider, String accessToken) {

        switch (provider.toLowerCase()) {
            case "kakao":
                return kakaoAuthClient.getUserInfo(accessToken);
            case "naver":
                return naverAuthClient.getUserInfo(accessToken);
            default:
                throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }
}
