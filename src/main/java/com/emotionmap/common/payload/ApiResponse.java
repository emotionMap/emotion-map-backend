 package com.emotionmap.common.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "공통 API 응답")
@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    @Schema(description = "응답 데이터")
    private T data;

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data);
    }
}