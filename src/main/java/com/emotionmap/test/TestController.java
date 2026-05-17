package com.emotionmap.test;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.auth.payLoad.AuthLoginRequest;
import com.emotionmap.business.auth.vo.UserVo;
import com.emotionmap.business.jwt.provider.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.emotionmap.business.auth.vo.UserStatusVo.REGISTERED;


@Tag(name = "테스트 API", description = "테스트 API")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final UserMapper userMapper;
    private final JwtProvider jwtProvider;

    private final S3Service s3Service;

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
            description = "1 넣어서 사용하시면 됩니다.")
    @PostMapping("/test-login")
    public ResponseEntity<?> testLogin(@RequestParam Long userId) {

        UserVo userVo = new UserVo();
        userVo.setId(userId);
        userVo.setProvider("KAKAO");
        userVo.setProvider("1");
        userVo.setNickname("보현이");
        userVo.setStatus(REGISTERED);

        String token = jwtProvider.createAccessToken(userVo);

        return ResponseEntity.ok(Map.of(
                "accessToken", token
        ));
    }


}
