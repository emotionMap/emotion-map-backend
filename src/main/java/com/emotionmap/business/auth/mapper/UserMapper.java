package com.emotionmap.business.auth.mapper;

import com.emotionmap.business.auth.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    UserVo findByProviderAndProviderUserId(
            @Param("provider") String provider,
            @Param("providerUserId") String providerUserId
    );

    void insertUserInfo(UserVo user);

    void dataClean();
}
