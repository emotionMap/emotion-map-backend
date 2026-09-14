package com.emotionmap.business.posts.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "감정 통계 (마이페이지)")
public class EmotionStatResponse {

    @Schema(description = "감정 아이디")
    private Long id;
    @Schema(description = "감정 이름")
    private String name;
    @Schema(description = "감정 이모지")
    private String emoji;
    @Schema(description = "기간 내 사용 횟수")
    private int count;
}
