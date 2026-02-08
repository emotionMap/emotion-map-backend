package com.emotionmap.business.posts.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "포스트 등록")
public class PostCreateRequest {
    @Schema(description = "소셜 로그인 제공자")
    private String provider;
}
