package com.emotionmap.business.auth.payLoad;

import com.emotionmap.business.auth.vo.UserStatusVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "소셜 로그인 응답")
@AllArgsConstructor
public class AuthLoginResponse {

    @Schema(description = "소셜 제공자")
    private String provider;
    @Schema(description = "소셜 사용자 ID")
    private String providerUserId;
    @Schema(description = "사용자 상태")
    private UserStatusVo status;
}
