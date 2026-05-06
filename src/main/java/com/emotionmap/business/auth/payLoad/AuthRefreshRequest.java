package com.emotionmap.business.auth.payLoad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "토큰 갱신 요청")
public class AuthRefreshRequest {

    @Schema(description = "Refresh Token")
    private String refreshToken;
}
