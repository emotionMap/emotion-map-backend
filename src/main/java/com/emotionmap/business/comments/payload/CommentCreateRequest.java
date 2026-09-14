package com.emotionmap.business.comments.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "댓글 작성")
public class CommentCreateRequest {

    @Schema(description = "내용", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "부모 댓글 ID (대댓글 작성 시, 없으면 게시글에 바로 다는 최상위 댓글)")
    private Long parentCommentId;

    // 서비스에서 주입 / MyBatis 생성 키 수신
    private Long postId;
    private Long userId;
    private Long commentId;
}
