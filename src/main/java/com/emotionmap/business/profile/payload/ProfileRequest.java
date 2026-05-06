package com.emotionmap.business.profile.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "프로필 등록 요청")
public class ProfileRequest {
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "소개글")
    private String bio;
    @Schema(description = "이미지 URL")
    private String profileImageUrl;
}
