package com.emotionmap.business.comments.vo;

import com.emotionmap.business.posts.payload.Status;
import lombok.Getter;
import lombok.Setter;

/**
 * DB에서 평면으로 조회한 댓글 1행. {@link com.emotionmap.business.comments.service.CommentsService}가
 * parentCommentId를 기준으로 이 행들을 묶어 중첩 트리(CommentResponse)로 조립한다.
 */
@Getter
@Setter
public class CommentRow {
    private Long commentId;
    private Long parentCommentId;
    private String nickname;
    private String content;
    private String createdAt;
    private Boolean isMine;
    private Status status;
}
