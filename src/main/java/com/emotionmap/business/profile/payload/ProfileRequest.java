package com.emotionmap.business.profile.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    @Schema(description = "감정 태그 ID 목록 (최소 1개, 최대 5개)")
    private List<Long> emotionTagIds;
    @Schema(description = "위치 ID")
    private Long locationId;
}
