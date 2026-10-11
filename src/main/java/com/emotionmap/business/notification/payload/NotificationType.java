package com.emotionmap.business.notification.payload;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 종류")
public enum NotificationType {
    @Schema(description = "좋아요")
    LIKE,
    @Schema(description = "댓글 (게시글에 바로 달린 최상위 댓글)")
    COMMENT,
    @Schema(description = "답글 (댓글에 달린 대댓글)")
    REPLY,
}
