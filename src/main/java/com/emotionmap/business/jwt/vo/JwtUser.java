package com.emotionmap.business.jwt.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtUser {

    private final Long userId;
    private final String status;
//  iat/exp는 parse 메서드에서 검증됨
}
