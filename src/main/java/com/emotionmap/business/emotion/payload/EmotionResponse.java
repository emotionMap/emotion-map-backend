package com.emotionmap.business.emotion.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "이모지 리스트 응답")
@AllArgsConstructor
public class EmotionResponse {
    @Schema(description = "ID")
    private String id;
    @Schema(description = "이모지 명")
    private String name;
    @Schema(description = "이모지")
    private String emoji;
}
