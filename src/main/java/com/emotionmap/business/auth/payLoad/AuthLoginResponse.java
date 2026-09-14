package com.emotionmap.business.auth.payLoad;

import com.emotionmap.business.auth.vo.JWTToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "익명 로그인 응답")
@AllArgsConstructor
public class AuthLoginResponse {

    @Schema(description = "위치 설정 완료 여부 - false면 /users/me/location 호출 전까지 다른 API 사용 불가")
    private boolean locationSet;
    @Schema(description = "JWT 토큰")
    private JWTToken token;
}
