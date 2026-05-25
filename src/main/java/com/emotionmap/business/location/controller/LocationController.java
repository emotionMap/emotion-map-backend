package com.emotionmap.business.location.controller;

import com.emotionmap.business.location.mapper.LocationMapper;
import com.emotionmap.business.location.payload.SigunguResponse;
import com.emotionmap.common.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "location", description = "위치 API")
@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationMapper locationMapper;

    @Operation(summary = "시/도 목록 조회")
    @GetMapping("/sido")
    public ResponseEntity<ApiResponse<List<String>>> getSido() {
        List<String> response = locationMapper.getSido();
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "시/군/구 목록 조회")
    @GetMapping("/sigungu")
    public ResponseEntity<ApiResponse<List<SigunguResponse>>> getSigungu(
            @RequestParam String siDo
    ) {
        List<SigunguResponse> response = locationMapper.getSigungu(siDo);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
