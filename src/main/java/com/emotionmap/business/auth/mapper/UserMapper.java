package com.emotionmap.business.auth.mapper;

import com.emotionmap.business.auth.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    UserVo findByProviderAndProviderUserId(
            @Param("provider") String provider,
            @Param("providerUserId") String providerUserId
    );

    UserVo findById(@Param("id") Long id);

    void insertUserInfo(UserVo user);

    void updateRefreshToken(
            @Param("id") Long id,
            @Param("refreshToken") String refreshToken,
            @Param("refreshTokenExpiresAt") LocalDateTime refreshTokenExpiresAt
    );

    void clearRefreshToken(@Param("id") Long id);

    void dataClean();
}
