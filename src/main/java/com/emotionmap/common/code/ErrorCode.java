package com.emotionmap.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /*4XX*/
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED", "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다."),
    INVALID_LOGIN_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_LOGIN_REQUEST", "deviceId는 필수입니다."),
    NOT_FIND_USER_INFO(HttpStatus.NOT_ACCEPTABLE, "NOT_FIND_USER_INFO", "사용자 정보를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_NOT_FOUND", "존재하지 않는 게시글입니다."),
    INVALID_POST_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_POST_REQUEST", "위치와 감정 태그는 필수입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "해당 리소스에 대한 권한이 없습니다."),
    LOCATION_REQUIRED(HttpStatus.BAD_REQUEST, "LOCATION_REQUIRED", "위치는 필수입니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "존재하지 않는 댓글입니다."),
    INVALID_COMMENT_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_COMMENT_REQUEST", "댓글 내용은 필수입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 요청입니다.")

    /*5XX*/,
    DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB_ERROR", "데이터 처리 중 오류가 발생했습니다."),

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 오류");

    private final HttpStatus status;
    private final String code;
    private final String message;
}