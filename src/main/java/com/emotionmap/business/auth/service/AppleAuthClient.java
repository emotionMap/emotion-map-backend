package com.emotionmap.business.auth.service;

import com.emotionmap.business.auth.payLoad.AppleTokenPayload;
import com.emotionmap.business.auth.vo.SocialUserInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppleAuthClient {

    private final AppleJwtValidator appleJwtValidator;

    public SocialUserInfoVo getUserInfo(String identityToken) {

        AppleTokenPayload payload =
                appleJwtValidator.validate(identityToken);

        return new SocialUserInfoVo(payload.getSub());
    }
}
