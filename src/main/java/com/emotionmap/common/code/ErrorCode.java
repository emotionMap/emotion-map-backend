package com.emotionmap.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /*4XX*/
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED", "유효하지 않은 토큰입니다."),
    KAKAO_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "KAKAO_AUTH_FAILED", "카카오 인증 실패입니다."),
    NAVER_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "NAVER_AUTH_FAILED", "네이버 인증 실패입니다."),
    APPLE_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "APPLE_AUTH_FAILED", "애플 인증 실패입니다."),
    NOT_FIND_USER_INFO(HttpStatus.NOT_ACCEPTABLE, "NOT_FIND_USER_INFO", "사용자 정보를 찾을 수 없습니다."),
    EXAMPLE0(HttpStatus.NOT_ACCEPTABLE, "NOT_FIND_USER_INFO", "이미 가입된 사용자입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_NOT_FOUND", "존재하지 않는 게시글입니다."),
    INVALID_POST_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_POST_REQUEST", "위치와 감정 태그는 필수입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "해당 게시글에 대한 권한이 없습니다.")

    /*5XX*/,
    DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB_ERROR", "데이터 처리 중 오류가 발생했습니다."),

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 오류");

    private final HttpStatus status;
    private final String code;
    private final String message;
}