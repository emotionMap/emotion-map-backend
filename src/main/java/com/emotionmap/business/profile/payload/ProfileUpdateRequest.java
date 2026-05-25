package com.emotionmap.business.profile.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "프로필 수정 요청")
public class ProfileUpdateRequest {
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "소개글")
    private String bio;
    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;
    @Schema(description = "감정 태그 ID 목록 (최대 5개, null이면 기존 유지, 빈 배열이면 전체 삭제)")
    private List<Long> emotionTagIds;
    @Schema(description = "위치 ID (null이면 기존 유지)")
    private Long locationId;
}
