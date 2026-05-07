# EmotionMap

감정 태그 기반 소셜 지도 앱의 백엔드 REST API 서버입니다.

사용자는 소셜 로그인 후 게시글에 감정 태그와 위치를 첨부하여 지도 위에 감정을 공유할 수 있습니다.

## 기술 스택

| 분류 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.7 |
| ORM | MyBatis 3.0.3 |
| Database | MySQL 8.0 |
| Security | Spring Security, JWT (jjwt 0.11.5) |
| Social Login | Kakao, Naver, Apple |
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
| POST | /auth/login | 소셜 로그인 (Kakao/Naver/Apple) | 불필요 |
| POST | /auth/refresh | Access Token 갱신 | 불필요 |
| POST | /auth/logout | 로그아웃 | 필요 |

### Profile
| Method | URI | 설명 | 인증 |
|---|---|---|---|
| POST | /profile/create | 프로필 등록 (최초 1회) | 필요 |
| GET | /profile/me | 내 프로필 조회 | 필요 |
| PATCH | /profile/me | 프로필 수정 | 필요 |

### Users
| Method | URI | 설명 | 인증 |
|---|---|---|---|
| DELETE | /users/me | 회원 탈퇴 | 필요 |

### Posts
| Method | URI | 설명 | 인증 |
|---|---|---|---|
| GET | /posts | 게시글 목록 조회 | 필요 |
| GET | /posts/{postId} | 게시글 상세 조회 | 필요 |
| POST | /posts | 게시글 생성 | 필요 |
| PATCH | /posts/{postId} | 게시글 수정 | 필요 |
| DELETE | /posts/{postId} | 게시글 삭제 | 필요 |
| GET | /posts/me | 내 게시글 목록 | 필요 |

### Emotion
| Method | URI | 설명 | 인증 |
|---|---|---|---|
| GET | /emotion | 감정 태그 목록 조회 | 필요 |

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
| `profile.http` | 프로필 등록/조회/수정 |
| `users.http` | 회원 탈퇴 |
| `posts.http` | 게시글 CRUD |
| `emotion.http` | 감정 태그 목록 |

## 인증 플로우
1. 클라이언트가 소셜 Access Token을 `/auth/login`으로 전달
2. 서버가 소셜 제공자 API로 사용자 정보 검증
3. 신규 유저면 DB에 생성 (status: `UNREGISTERED`)
4. Access Token (30분) + Refresh Token (90일) 발급
5. `UNREGISTERED` 유저는 `/profile/create` 호출로 프로필 등록 후 `REGISTERED` 전환
6. 이후 모든 요청은 `Authorization: Bearer {accessToken}` 헤더로 인증
7. Access Token 만료 시 `/auth/refresh`로 토큰 갱신 (Refresh Token Rotation)

## 패키지 구조

```
src/main/java/com/emotionmap
├── business/
│   ├── auth/        # 소셜 로그인, JWT 발급
│   ├── jwt/         # JWT 필터, 토큰 파싱
│   ├── profile/     # 프로필 등록
│   ├── posts/       # 게시글 CRUD
│   ├── emotion/     # 감정 태그 목록
│   └── map/         # 지도 위치 기능
└── common/
    ├── config/      # Security, S3, Swagger 설정
    ├── code/        # ErrorCode 열거형
    ├── exception/   # 전역 예외 처리
    └── payload/     # 공통 응답 형식 (ApiResponse)
```

## ERD 요약

```
users
 ├─< posts (user_id)
 │      └─< post_images      (post_id)
 │      └─< post_emotion_tag (post_id) >─ emotion_tags
 │      └─< post_likes       (post_id, user_id)
 │      └── locations        (location_id)
 └─< user_emotion_tag (user_id) >─ emotion_tags
```

- `posts.parent_id` + `posts.depth` 로 댓글 계층 표현 (별도 댓글 테이블 없음)
