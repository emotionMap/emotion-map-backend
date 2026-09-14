# EmotionMap

감정 태그 기반 소셜 지도 앱의 백엔드 REST API 서버입니다.

본인인증(휴대폰 인증, 소셜 로그인 등) 없이, 기기 식별자(deviceId) 하나로 바로 이용할 수 있는 완전한 MVP입니다. 사용자는 로그인 후 게시글에 감정 태그와 위치를 첨부하여 지도 위에 감정을 공유할 수 있습니다.

프로필(닉네임/자기소개/사진) 없이 작동하는 완전 익명 게시판입니다. 게시글을 쓸 때마다 그 게시글 안에서만 통하는 무작위 닉네임이 자동으로 부여되며, 다른 게시글에서는 같은 사람이라도 다른 닉네임으로 보입니다. 타 사용자의 프로필을 조회하는 기능은 없고, 본인 글 목록만 마이페이지(`GET /posts/me`)에서 확인할 수 있습니다.

게시글과 댓글은 완전히 분리된 개념입니다. 게시글은 항상 최상위 글이고, 댓글은 **댓글 → 대댓글 → 대대댓글 → ...로 깊이 제한 없이** 중첩될 수 있습니다. 게시글 상세 조회 1번으로 그 글의 전체 댓글 트리를 화면 전환 없이 한 번에 볼 수 있습니다.

지도 메인에서는 지역(시/군/구)마다 최근 부착된 감정 태그를 최대 5개까지 보여줍니다. 지역을 선택하면 그 지역의 게시글 피드를 확인할 수 있습니다 (기존 피드 API를 `locationId`로 재사용).

## 기술 스택

| 분류 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.7 |
| ORM | MyBatis 3.0.3 |
| Database | MySQL 8.0 |
| Security | Spring Security, JWT (jjwt 0.11.5) |
| Infra | AWS EC2 |
| Storage | AWS S3 (SDK v2) |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Build | Gradle |

## API 명세

> 로컬 실행 후 Swagger UI에서 전체 명세를 확인할 수 있습니다.
> `http://localhost:8080/swagger-ui/index.html`

### Auth
| Method | URI | 설명 | 인증 |
|---|---|---|---|
| POST | /auth/login | 익명 로그인 (deviceId 기반, 본인인증 없음) | 불필요 |
| POST | /auth/refresh | Access Token 갱신 | 불필요 |
| POST | /auth/logout | 로그아웃 | 필요 |

### Users
| Method | URI | 설명 | 인증 |
|---|---|---|---|
| PATCH | /users/me/location | 가입 시 위치 설정 (필수, 최초 1회 / 이후 변경 가능) | 필요 |
| DELETE | /users/me | 회원 탈퇴 | 필요 |

### Map
| Method | URI | 설명 | 인증 |
|---|---|---|---|
| GET | /map | 지역별 지도 요약 (지역마다 최근 부착된 감정 태그 최대 5개, 게시글 없는 지역은 제외) | 필요 |

### Posts
| Method | URI | 설명 | 인증 |
|---|---|---|---|
| GET | /posts | 게시글 목록 조회 (`locationId` 생략 시 내 계정 위치, 지정 시 그 지역 - 지도에서 지역 선택 시 사용) | 필요 |
| GET | /posts/{postId} | 게시글 상세 조회 (댓글을 대댓글까지 중첩 트리로 한 번에 포함) | 필요 |
| POST | /posts | 게시글 생성 | 필요 |
| PATCH | /posts/{postId} | 게시글 수정 | 필요 |
| DELETE | /posts/{postId} | 게시글 삭제 | 필요 |
| GET | /posts/me | 내 게시글 목록 | 필요 |
| GET | /posts/me/emotion-stats?days= | 개인 감정 통계 (최근 N일간 감정 태그별 사용 횟수, 많이 쓴 순, 기본 7일) | 필요 |

### Comments
| Method | URI | 설명 | 인증 |
|---|---|---|---|
| POST | /posts/{postId}/comments | 댓글/대댓글 작성 (`parentCommentId`로 무제한 중첩) | 필요 |
| PATCH | /comments/{commentId} | 댓글 수정 | 필요 |
| DELETE | /comments/{commentId} | 댓글 삭제 (soft delete, 대댓글은 유지) | 필요 |

### Emotion
| Method | URI | 설명 | 인증 |
|---|---|---|---|
| GET | /emotion | 감정 태그 목록 조회 | 필요 |

### Location
| Method | URI | 설명 | 인증 |
|---|---|---|---|
| GET | /location/sido | 시/도 목록 조회 | 필요 |
| GET | /location/sigungu?siDo= | 시/군/구 목록 조회 | 필요 |

## HTTP Client (API 테스트)
IntelliJ 내장 HTTP Client를 사용합니다. Swagger 없이 IDE에서 바로 API 요청을 실행할 수 있습니다.

1. 서버 실행 후 로그인 요청으로 토큰 발급
<br/>Swagger(http://localhost:8080/swagger-ui/index.html)에서 로그인 1번만 실행
2. `http/http-client.env.json`에 토큰 입력
```json
{
  "local": {
    "baseUrl": "http://localhost:8080",
    "accessToken": "발급받은_액세스_토큰",
    "refreshToken": "발급받은_리프레시_토큰"
  }
}
```
**3. `http/` 폴더의 `.http` 파일에서 `▶` 버튼으로 실행**

| 파일 | 내용 |
|---|---|
| `auth.http` | 로그인, 토큰 갱신, 로그아웃 |
| `users.http` | 위치 설정, 회원 탈퇴 |
| `posts.http` | 게시글 CRUD |
| `comments.http` | 댓글/대댓글 작성, 수정, 삭제 |
| `map.http` | 지도 요약, 지역별 피드 |
| `emotion.http` | 감정 태그 목록 |

## 인증 플로우
1. 클라이언트가 기기별로 생성해 보관하는 `deviceId`를 `/auth/login`으로 전달 (본인인증 없음)
2. 서버가 `deviceId`로 기존 계정을 찾거나, 없으면 새로 생성 (location_id: `NULL`)
3. Access Token (30분, `locationSet` 클레임 포함) + Refresh Token (90일) 발급
4. `locationSet=false`인 유저는 `/users/me/location` 호출로 위치 설정 후 `locationSet=true`가 반영된 새 토큰을 재발급받아야 다른 API 사용 가능 (닉네임·사진 등 프로필 개념 자체가 없음)
5. 이후 모든 요청은 `Authorization: Bearer {accessToken}` 헤더로 인증
6. Access Token 만료 시 `/auth/refresh`로 토큰 갱신 (Refresh Token Rotation)
7. 앱을 지우고 새 deviceId로 다시 로그인하면 이전 계정과 연결이 끊어짐 (본인인증이 없어 계정 복구 수단 없음)

## 패키지 구조

```
src/main/java/com/emotionmap
├── business/
│   ├── auth/        # 익명 로그인(deviceId), JWT 발급
│   ├── jwt/         # JWT 필터, 토큰 파싱
│   ├── users/       # 가입 시 위치 설정, 회원 탈퇴
│   ├── posts/       # 게시글 CRUD + 게시글 단위 익명 닉네임 (게시글은 항상 최상위)
│   ├── comments/    # 댓글 (대댓글 무제한 중첩)
│   ├── emotion/     # 감정 태그 목록
│   ├── location/    # 시/도·시/군/구 위치 조회
│   └── map/         # 지역별 지도 요약 (스키마 변경 없는 순수 조회/집계)
└── common/
    ├── config/      # Security, S3, Swagger 설정
    ├── code/        # ErrorCode 열거형
    ├── exception/   # 전역 예외 처리
    └── payload/     # 공통 응답 형식 (ApiResponse)
```

## ERD 요약

```
users ──── locations (location_id)
 └─< posts (user_id)
        └─< post_images            (post_id)
        └─< post_emotion_tag       (post_id) >─ emotion_tags
        └─< post_likes             (post_id, user_id)
        └─< post_anonymous_nickname (post_id, user_id)
        └─< comments               (post_id)
               └─< comments (self)  (parent_comment_id)  -- 대댓글, 무제한 중첩
        └── locations               (location_id)
```

- 게시글과 댓글은 완전히 분리된 테이블이다. 게시글은 항상 최상위, 댓글은 `parent_comment_id`로 자기참조해 깊이 제한 없이 중첩된다.
- `post_anonymous_nickname`: 완전 익명 게시판 — 게시글 + 유저 조합별로 무작위 닉네임을 배정 (게시글 본문이든 그 아래 댓글이든 동일). 프로필/타 사용자 조회 기능은 없음
