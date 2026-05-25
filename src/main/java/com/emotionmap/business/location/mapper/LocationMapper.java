package com.emotionmap.business.location.mapper;

import com.emotionmap.business.location.payload.SigunguResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LocationMapper {

    List<String> getSido();

    List<SigunguResponse> getSigungu(@Param("siDo") String siDo);
}
