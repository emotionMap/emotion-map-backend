package com.emotionmap.business.users.controller;

import com.emotionmap.business.jwt.vo.JwtUser;
import com.emotionmap.business.profile.service.ProfileService;
import com.emotionmap.common.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "users", description = "사용자 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final ProfileService profileService;

    // 프앤 전달
    // 응답 성공(200) 시 로컬에 저장된 액세스 토큰과 리프레시 토큰을 즉시 삭제하고 로그인 화면으로 이동할 것
    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal JwtUser jwtUser) {
        profileService.withdraw(jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(null));
    }
}
