package com.emotionmap.business.comments.payload;

import com.emotionmap.business.posts.payload.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "댓글 (대댓글 포함 트리 구조)")
public class CommentResponse {

    @Schema(description = "댓글 아이디")
    private Long commentId;
    @Schema(description = "이 게시글 내에서 부여된 익명 닉네임")
    private String nickname;
    @Schema(description = "내용")
    private String content;
    @Schema(description = "작성시간")
    private String createdAt;
    @Schema(description = "내가 작성한 댓글인지 여부")
    private Boolean isMine;
    @Schema(description = "상태")
    private Status status;
    @Schema(description = "대댓글 목록")
    private List<CommentResponse> children;
}
