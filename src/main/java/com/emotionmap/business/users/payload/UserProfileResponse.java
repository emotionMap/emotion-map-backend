package com.emotionmap.business.users.payload;

import com.emotionmap.business.emotion.payload.EmotionResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "타 사용자 프로필 조회 응답")
public class UserProfileResponse {
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "소개글")
    private String bio;
    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;
    @Schema(description = "감정 태그 목록 (최대 5개)")
    private List<EmotionResponse> emotionTags;
    @Schema(description = "시/도")
    private String siDo;
    @Schema(description = "시/군/구")
    private String siGunGu;
}
