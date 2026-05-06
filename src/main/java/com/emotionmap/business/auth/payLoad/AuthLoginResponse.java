package com.emotionmap.business.auth.payLoad;

import com.emotionmap.business.auth.vo.JWTToken;
import com.emotionmap.business.auth.vo.UserStatusVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "소셜 로그인 응답")
@AllArgsConstructor
public class AuthLoginResponse {

    @Schema(description = "사용자 상태")
    private UserStatusVo status;
    @Schema(description = "JWT 토큰")
    private JWTToken token;
}
