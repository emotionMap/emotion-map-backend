package com.emotionmap.business.auth.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserVo {

    private Long id;
    private String provider;
    private String providerUserId;
    private String nickname;
    private String profileImageUrl;
    private UserStatusVo status;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiresAt;

    public static UserVo newSocialUser(String provider, String providerUserId) {
        UserVo user = new UserVo();
        user.setProvider(provider);
        user.setProviderUserId(providerUserId);
        user.setStatus(UserStatusVo.UNREGISTERED);
        return user;
    }

    // 상태 체크
    public boolean isActive() {
        return this.status == UserStatusVo.REGISTERED;
    }
    // 활성상태로 변경
    public void activate() {
        this.status = UserStatusVo.REGISTERED;
    }

}
