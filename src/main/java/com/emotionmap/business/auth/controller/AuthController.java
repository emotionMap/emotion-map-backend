package com.emotionmap.business.auth.controller;

import com.emotionmap.business.auth.mapper.UserMapper;
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
    private final UserMapper userMapper;

    @Operation(
            summary = "로그인",
            description = "카카오/네이버/애플 로그인 API"
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthLoginResponse>> login(
            @RequestBody AuthLoginRequest request
    ) {
        AuthLoginResponse response =
                authService.login(request.getProvider(), request.getAccessToken());

        return ResponseEntity.ok(ApiResponse.of(response));

    }

    @Operation(
            summary = "유저테이블 데이터 삭제",
            description = "테스트용 API"
    )
    @PostMapping("/userInfoClean")
    public void userInfoClean(
            @RequestBody AuthLoginRequest request
    ) {
        userMapper.dataClean();

    }
}
