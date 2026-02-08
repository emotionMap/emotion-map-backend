package com.emotionmap.business.auth.payLoad;

import lombok.Getter;

@Getter
public class AppleTokenPayload {

    private String iss;
    private String aud;
    private String sub;
    private Long exp;
}
