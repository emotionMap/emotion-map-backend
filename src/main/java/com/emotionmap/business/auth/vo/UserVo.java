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
    private String deviceId;
    private LocalDateTime createdAt;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiresAt;
    private Long locationId;

    public static UserVo newAnonymousUser(String deviceId) {
        UserVo user = new UserVo();
        user.setDeviceId(deviceId);
        return user;
    }

    public boolean hasLocation() {
        return this.locationId != null;
    }

}
