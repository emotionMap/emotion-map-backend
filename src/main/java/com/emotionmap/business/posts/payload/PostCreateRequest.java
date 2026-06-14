package com.emotionmap.business.posts.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "포스트 등록")
public class PostCreateRequest {

    @Schema(description = "위치 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long locationId;

    @Schema(description = "감정 태그 ID 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> emotionIds;

    @Schema(description = "본문 (선택)")
    private String content;

    @Schema(description = "부모 게시글 ID (댓글 작성 시)")
    private Long parentId;

    // 서비스에서 주입 / MyBatis 생성 키 수신
    private Long userId;
    private Long postId;
}
