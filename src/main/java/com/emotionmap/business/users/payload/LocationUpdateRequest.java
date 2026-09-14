package com.emotionmap.business.users.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "위치 설정 요청")
public class LocationUpdateRequest {

    @Schema(description = "위치 ID (locations.id)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long locationId;
}
