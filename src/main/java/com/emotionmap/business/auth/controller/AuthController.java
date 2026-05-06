package com.emotionmap.business.auth.controller;

import com.emotionmap.business.auth.payLoad.AuthLoginRequest;
import com.emotionmap.business.auth.payLoad.AuthLoginResponse;
import com.emotionmap.business.auth.payLoad.AuthRefreshRequest;
import com.emotionmap.business.auth.service.AuthService;
import com.emotionmap.business.auth.vo.JWTToken;
import com.emotionmap.business.jwt.vo.JwtUser;
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

@Tag(name = "auth", description = "인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "카카오/네이버/애플 로그인 API")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthLoginResponse>> login(@RequestBody AuthLoginRequest request) {
        AuthLoginResponse response = authService.login(request.getProvider(), request.getAccessToken());
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새 Access Token 발급")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JWTToken>> refresh(@RequestBody AuthRefreshRequest request) {
        JWTToken token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.of(token));
    }

    @Operation(summary = "로그아웃", description = "Refresh Token 무효화")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal JwtUser jwtUser) {
        authService.logout(jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(null));
    }
}
