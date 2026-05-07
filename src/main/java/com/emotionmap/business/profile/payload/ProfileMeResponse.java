package com.emotionmap.business.profile.payload;

import com.emotionmap.business.emotion.payload.EmotionResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "내 프로필 조회 응답")
public class ProfileMeResponse {
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "소개글")
    private String bio;
    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;
    @Schema(description = "가입일")
    private LocalDateTime createdAt;
    @Schema(description = "감정 태그 목록 (최대 5개)")
    private List<EmotionResponse> emotionTags;
}
