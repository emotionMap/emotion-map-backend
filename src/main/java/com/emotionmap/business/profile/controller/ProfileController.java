package com.emotionmap.business.profile.controller;

import com.emotionmap.business.jwt.vo.JwtUser;
import com.emotionmap.business.profile.payload.ProfileRequest;
import com.emotionmap.business.profile.payload.ProfileResponse;
import com.emotionmap.business.profile.service.ProfileService;
import com.emotionmap.common.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // 프로필 조회

    // @AuthenticationPrincipal
//    @Operation(summary = "프로필 수정")
//    @PutMapping("/update/{userId}")
//    public ResponseEntity<ApiResponse<ProfileResponse>> login(@RequestBody ProfileRequest request) {
//        ProfileResponse response =
//                profileService.reg(request);
//
//        return ResponseEntity.ok(ApiResponse.of(response));
//    }

    // 나의 작성글
}
