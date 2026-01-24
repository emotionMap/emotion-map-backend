package com.emotionmap.business.profile.payload;

import com.emotionmap.business.auth.vo.JWTToken;
import com.emotionmap.business.auth.vo.UserStatusVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "프로필 등록 응답")
@AllArgsConstructor
public class ProfileResponse {
    @Schema(description = "사용자 상태")
    private UserStatusVo status;
    @Schema(description = "JWT 토큰")
    JWTToken token;
}
