package com.emotionmap.business.emotion.controller;

import com.emotionmap.business.emotion.mapper.EmotionMapper;
import com.emotionmap.business.emotion.payload.EmotionResponse;
import com.emotionmap.common.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "emotion", description = "감정 API")
@RestController
@RequestMapping("/emotion")
@RequiredArgsConstructor

public class EmotionController {

    private final EmotionMapper emotionMapper;

    @Operation(summary = "감정리스트 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmotionResponse>>> getEmotion() {
        List<EmotionResponse> response = emotionMapper.getEmotion();
        return ResponseEntity.ok(ApiResponse.of(response));
    }

}
