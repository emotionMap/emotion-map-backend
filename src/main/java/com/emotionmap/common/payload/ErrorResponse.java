package com.emotionmap.common.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "에러 응답")
@Getter
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "에러 정보")
    private Error error;

    public ErrorResponse(String code, String message) {
        this.error = new Error(code, message);
    }

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message);
    }

    @Schema(description = "에러 상세")
    @Getter
    @AllArgsConstructor
    static class Error {
        private String code;
        private String message;
    }
}


