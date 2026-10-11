package com.emotionmap.business.notification.controller;

import com.emotionmap.business.jwt.vo.JwtUser;
import com.emotionmap.business.notification.payload.NotificationResponse;
import com.emotionmap.business.notification.service.NotificationService;
import com.emotionmap.common.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "notifications", description = "알림 API (좋아요/댓글/답글)")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "안읽은 알림 개수", description = "피드 상단 알림 뱃지 활성/비활성 판단용 (0이면 비활성)")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Integer>> getUnreadCount(@AuthenticationPrincipal JwtUser jwtUser) {
        return ResponseEntity.ok(ApiResponse.of(notificationService.getUnreadCount(jwtUser.getUserId())));
    }

    @Operation(summary = "알림 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(@AuthenticationPrincipal JwtUser jwtUser,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.of(notificationService.getNotifications(page, size, jwtUser.getUserId())));
    }

    @Operation(summary = "전체 읽음 처리", description = "알림 목록 화면 진입 시 호출 - 알림 뱃지 비활성화")
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> readAll(@AuthenticationPrincipal JwtUser jwtUser) {
        notificationService.readAll(jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(null));
    }
}
