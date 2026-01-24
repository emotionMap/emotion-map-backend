package com.emotionmap.business.auth.vo;

import lombok.Getter;

@Getter
public class NaverUserResponse {

    private String resultcode;
    private String message;
    private Response response;

    @Getter
    public static class Response {
        private String id;
        private String email;
        private String nickname;
        private String profile_image;
        private String profileImage;
    }
}
