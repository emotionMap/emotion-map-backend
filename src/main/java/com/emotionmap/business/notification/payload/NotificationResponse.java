package com.emotionmap.business.notification.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "알림")
public class NotificationResponse {

    @Schema(description = "알림 아이디")
    private Long notificationId;
    @Schema(description = "알림 종류")
    private NotificationType type;
    @Schema(description = "대상 게시글 아이디")
    private Long postId;
    @Schema(description = "대상 댓글 아이디 (LIKE는 없음)")
    private Long commentId;
    @Schema(description = "알림 문구")
    private String message;
    @Schema(description = "읽음 여부")
    private Boolean isRead;
    @Schema(description = "생성시간")
    private String createdAt;
}
