package com.emotionmap.business.profile.controller;

import com.emotionmap.business.jwt.vo.JwtUser;
import com.emotionmap.business.profile.payload.ProfileMeResponse;
import com.emotionmap.business.profile.payload.ProfileRequest;
import com.emotionmap.business.profile.payload.ProfileResponse;
import com.emotionmap.business.profile.payload.ProfileUpdateRequest;
import com.emotionmap.business.profile.service.ProfileService;
import com.emotionmap.common.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "profile", description = "프로필 API")
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "회원가입(프로필 등록)")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProfileResponse>> create(
            @AuthenticationPrincipal JwtUser jwtUser,
            @RequestBody ProfileRequest request) {
        ProfileResponse response = profileService.reg(jwtUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "내 프로필 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileMeResponse>> getMyProfile(
            @AuthenticationPrincipal JwtUser jwtUser) {
        ProfileMeResponse response = profileService.getMyProfile(jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "내 프로필 수정")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<ProfileMeResponse>> updateMyProfile(
            @AuthenticationPrincipal JwtUser jwtUser,
            @RequestBody ProfileUpdateRequest request) {
        ProfileMeResponse response = profileService.updateMyProfile(jwtUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
