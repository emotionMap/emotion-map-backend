package com.emotionmap.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {


    /*4XX*/
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED", "유효하지 않은 토큰입니다."),
    NOT_FIND_USER_INFO(HttpStatus.NOT_ACCEPTABLE, "NOT_FIND_USER_INFO", "사용자 정보를 찾을 수 없습니다."),
    EXAMPLE0(HttpStatus.NOT_ACCEPTABLE, "NOT_FIND_USER_INFO", "이미 가입된 사용자입니다.")

    /*5XX*/,
    DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB_ERROR", "데이터 처리 중 오류가 발생했습니다."),

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 오류");

    private final HttpStatus status;
    private final String code;
    private final String message;
}