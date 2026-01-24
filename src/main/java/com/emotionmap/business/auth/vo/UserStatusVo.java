package com.emotionmap.business.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 상태")
public enum UserStatusVo {
    @Schema(description = "가입완료")
    REGISTERED,
    @Schema(description = "신규")
    UNREGISTERED,
}