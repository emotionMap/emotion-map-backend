package com.emotionmap.business.auth.vo;

import lombok.Getter;

import java.util.List;

@Getter
public class ApplePublicKeysResponse {
    private List<Key> keys;

    @Getter
    public static class Key {
        private String kid;
        private String alg;
        private String n;
        private String e;
    }
}
