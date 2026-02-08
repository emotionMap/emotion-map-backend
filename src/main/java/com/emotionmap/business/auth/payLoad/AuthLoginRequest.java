package com.emotionmap.business.auth.payLoad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "소셜 로그인 요청")
public class AuthLoginRequest {
    @Schema(
            description = "소셜 로그인 제공자",
            example = "KAKAO",
            allowableValues = {"KAKAO", "NAVER", "APPLE"}
    )
    private String provider;

    @Schema(
            description = "소셜 Access Token",
            example = "xxxx.yyyy.zzzz"
    )
    private String accessToken;
}
