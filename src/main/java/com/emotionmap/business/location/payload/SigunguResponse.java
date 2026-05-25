package com.emotionmap.business.location.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "시/군/구 목록 응답")
public class SigunguResponse {
    @Schema(description = "위치 ID")
    private Long locationId;
    @Schema(description = "시/군/구 명")
    private String siGunGu;
}
