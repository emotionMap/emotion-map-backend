package com.emotionmap.business.posts.payload;

import com.emotionmap.business.comments.payload.CommentResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "포스트 상세 조회")
public class PostDetailResponse {

    @Schema(description = "현재 포스트 정보")
    private Post post;
    @Schema(description = "댓글 목록 (대댓글까지 중첩 트리로 포함)")
    private List<CommentResponse> comments;

    @Getter
    @Setter
    @Schema(description = "포스트 정보")
    public static class Post {
        @Schema(description = "포스트 아이디")
        private Long postId;
        @Schema(description = "이 스레드 내에서 부여된 익명 닉네임")
        private String nickname;
        @Schema(description = "위치 아이디")
        private Long locationId;
        @Schema(description = "시/도")
        private String siDo;
        @Schema(description = "시/군/구")
        private String siGunGu;
        @Schema(description = "내용")
        private String content;
        @Schema(description = "작성시간")
        private String createdAt;
        @Schema(description = "좋아요 표시 여부")
        private String likeYN;
        @Schema(description = "좋아요 개수")
        private int likeCount;
        @Schema(description = "댓글 개수")
        private int commentCount;
        @Schema(description = "내가 작성한 글인지 여부")
        private Boolean isMine;
        @Schema(description = "상태")
        private Status status;
        @Schema(description = "포스트 사진")
        private List<Image> imageList;
        @Schema(description = "포스트 감정")
        private List<Emotion> emotionList;
    }
}