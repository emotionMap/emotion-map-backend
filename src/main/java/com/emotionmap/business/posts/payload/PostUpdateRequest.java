package com.emotionmap.business.posts.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "포스트 수정")
public class PostUpdateRequest {

    @Schema(description = "위치 ID (선택)")
    private Long locationId;

    @Schema(description = "감정 태그 ID 목록 (선택, 전달 시 전체 교체)")
    private List<Long> emotionIds;

    @Schema(description = "본문 (선택)")
    private String content;

    // 서비스에서 주입
    private Long postId;
}
