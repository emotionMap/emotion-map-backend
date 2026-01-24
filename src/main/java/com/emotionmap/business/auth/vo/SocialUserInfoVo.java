package com.emotionmap.business.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialUserInfoVo {

    private String id;          // provider_user_id
    private String email;       // optional
    private String nickname;    // optional
    private String profileImageUrl; // optional
}
