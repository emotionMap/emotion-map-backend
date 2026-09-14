package com.emotionmap.test;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.auth.payLoad.AuthLoginRequest;
import com.emotionmap.business.auth.vo.UserVo;
import com.emotionmap.business.jwt.provider.JwtProvider;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Tag(name = "테스트 API", description = "테스트 API")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final UserMapper userMapper;
    private final JwtProvider jwtProvider;

    private final S3Service s3Service;

    @Hidden // MultipartFile 파라미터가 OpenAPI 스키마로 잘 안 잡혀서 Dart 클라이언트 생성기가 깨짐 - 문서/코드생성 대상에서 제외
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        return s3Service.upload(file);
    }

    @Operation(summary = "유저테이블 데이터 삭제 API")
    @PostMapping("/userInfoClean")
    public void userInfoClean(
            @RequestBody AuthLoginRequest request
    ) {
        userMapper.dataClean();

    }

    @Operation(summary = "토큰발급 API",
            description = "1 넣어서 사용하시면 됩니다. 실제 users 테이블의 location_id 여부가 토큰의 locationSet 값에 반영됩니다.")
    @PostMapping("/test-login")
    public ResponseEntity<?> testLogin(@RequestParam Long userId) {

        UserVo userVo = userMapper.findById(userId);
        if (userVo == null) {
            userVo = new UserVo();
            userVo.setId(userId);
            userVo.setDeviceId("test-device-" + userId);
        }

        String token = jwtProvider.createAccessToken(userVo);

        return ResponseEntity.ok(Map.of(
                "accessToken", token
        ));
    }


}
