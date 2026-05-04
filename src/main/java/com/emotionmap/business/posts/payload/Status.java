package com.emotionmap.business.posts.payload;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상태")
public enum Status {
    @Schema(description = "활성")
    ACTIVE,
    @Schema(description = "비활성")
    DELETED,
}
