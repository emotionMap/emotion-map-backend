package com.emotionmap.business.users.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "FCM 디바이스 토큰 등록/갱신 요청")
public class FcmTokenUpdateRequest {

    @Schema(description = "FCM 디바이스 토큰", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fcmToken;
}
