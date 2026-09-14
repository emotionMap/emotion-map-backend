package com.emotionmap.business.map.controller;

import com.emotionmap.business.map.payload.MapRegionResponse;
import com.emotionmap.business.map.service.MapService;
import com.emotionmap.common.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "map", description = "지도 API")
@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @Operation(summary = "지역별 지도 요약", description = "지역(시/군/구)별로 최근 부착된 감정 태그 최대 5개. 게시글이 없는 지역은 제외됨")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MapRegionResponse>>> getRegionSummaries() {
        List<MapRegionResponse> response = mapService.getRegionSummaries();
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
