package com.emotionmap.business.auth.payLoad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "익명 로그인 요청")
public class AuthLoginRequest {

    @Schema(
            description = "클라이언트가 기기별로 생성해 보관하는 익명 식별자",
            example = "5f2e9b3a-1234-4c3e-8b7a-9b3a5f2e9b3a"
    )
    private String deviceId;
}
