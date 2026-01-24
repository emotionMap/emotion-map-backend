package com.emotionmap.business.profile.controller;

import com.emotionmap.business.profile.payload.ProfileRequest;
import com.emotionmap.business.profile.payload.ProfileResponse;
import com.emotionmap.business.profile.service.ProfileService;
import com.emotionmap.common.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "profile", description = "프로필 설정 API")
@RestController
@RequiredArgsConstructor

public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "회원가입(프로필 등록)")
    @PostMapping("/profile/reg")
    public ResponseEntity<ApiResponse<ProfileResponse>> login(@RequestBody ProfileRequest request) {
        ProfileResponse response =
                profileService.reg(request);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
