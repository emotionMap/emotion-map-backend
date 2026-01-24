package com.emotionmap.business.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JWTToken {
    String accessToken;
    String refreshToken;
}
