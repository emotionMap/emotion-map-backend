package com.emotionmap.business.posts.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "포스트 리스트 조회")
public class PostListResponse {
    @Schema(description = "포스트 아이디")
    private Long postId;
    @Schema(description = "유저 아이디")
    private Long userId;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "작성자 프로필 이미지 URL")
    private String profileImageUrl;
    @Schema(description = "위치 아이디")
    private Long locationId;
    @Schema(description = "위치")
    private String locationName;
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
