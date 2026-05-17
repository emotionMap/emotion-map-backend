package com.emotionmap.business.auth.service;

import com.emotionmap.business.auth.vo.SocialUserInfoVo;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialAuthService {

    private final KakaoAuthClient kakaoAuthClient;
    private final NaverAuthClient naverAuthClient;
    private final AppleAuthClient appleAuthClient;

    public SocialUserInfoVo getUserInfo(String provider, String accessToken) {

        switch (provider.toLowerCase()) {
            case "kakao":
                return kakaoAuthClient.getUserInfo(accessToken);
            case "naver":
                return naverAuthClient.getUserInfo(accessToken);
            case "apple":
                return appleAuthClient.getUserInfo(accessToken);
            default:
                log.warn("[Social] 지원하지 않는 provider: {}", provider);
                throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }
}
