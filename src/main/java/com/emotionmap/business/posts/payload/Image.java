package com.emotionmap.business.posts.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Schema(description = "포스트 이미지")
public class Image {
    @Schema(description = "포스트 ID")
    private Long postId;
    @Schema(description = "이미지 URL")
    private String url;
    @Schema(description = "노출 순서")
    private Integer sortOrder;
}