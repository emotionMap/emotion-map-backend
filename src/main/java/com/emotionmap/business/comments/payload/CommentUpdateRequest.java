package com.emotionmap.business.comments.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "댓글 수정")
public class CommentUpdateRequest {

    @Schema(description = "내용", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    // 서비스에서 주입
    private Long commentId;
}
