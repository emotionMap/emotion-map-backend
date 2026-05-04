package com.emotionmap.business.posts.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Schema(description = "감정 태그")
public class Emotion {
    @Schema(description = "포스트 ID")
    private Long postId;
    @Schema(description = "감정 아이디")
    private Long id;
    @Schema(description = "감정 이모지")
    private String emoji;
    @Schema(description = "감정 이름")
    private String name;
}