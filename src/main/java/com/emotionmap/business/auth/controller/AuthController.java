package com.emotionmap.business.auth.controller;

import com.emotionmap.business.auth.payLoad.AuthLoginResponse;
import com.emotionmap.business.auth.payLoad.AuthLoginRequest;
import com.emotionmap.business.auth.service.AuthService;
import com.emotionmap.common.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "auth", description = "인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "소셜 로그인",
            description = "카카오 / 네이버 소셜 로그인 API"
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthLoginResponse>> login(
            @RequestBody AuthLoginRequest request
    ) {
        AuthLoginResponse response =
                authService.login(request.getProvider(), request.getAccessToken());

        return ResponseEntity.ok(ApiResponse.of(response));

    }
}
