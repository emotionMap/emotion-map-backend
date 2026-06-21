# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run (uses application-local.yml by default)
./gradlew bootRun

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.emotionmap.EmotionMapApplicationTests"

# Clean build
./gradlew clean build
```

The active Spring profile defaults to `local`. To switch: `./gradlew bootRun --args='--spring.profiles.active=prod'`

## Architecture

**Spring Boot 3.5.7 / Java 17** REST API for an emotion-tagged social map app. Uses MyBatis for SQL (no JPA), MySQL as primary database.

### Package layout

```
com.emotionmap
├── business/
│   ├── auth/          # 소셜 로그인 (Kakao, Naver, Apple) + JWT 발급
│   │   ├── controller/    # AuthController → /auth
│   │   ├── mapper/        # UserMapper
│   │   ├── payload/       # AuthLoginRequest/Response, AuthRefreshRequest
│   │   ├── service/       # AuthService, SocialAuthService, KakaoAuthClient, NaverAuthClient, AppleAuthClient
│   │   └── vo/            # UserVo, UserStatusVo, JWTToken, SocialUserInfoVo
│   ├── jwt/               # JwtProvider, JwtAuthenticationFilter, JwtUser
│   ├── posts/             # 게시글 CRUD + 좋아요 + 댓글
│   │   ├── controller/    # PostsController → /posts
│   │   ├── mapper/        # PostsMapper
│   │   ├── payload/       # PostListResponse, PostDetailResponse, PostCreateRequest, PostUpdateRequest, Image, Emotion, Status
│   │   └── service/       # PostsService
│   ├── profile/           # 프로필 등록 및 관리 (소셜 로그인 후속 단계)
│   │   ├── controller/    # ProfileController → /profile
│   │   ├── mapper/        # ProfilerMapper
│   │   ├── payload/       # ProfileRequest, ProfileUpdateRequest, ProfileResponse, ProfileMeResponse
│   │   └── service/       # ProfileService
│   ├── emotion/           # 감정 태그 목록 조회
│   │   ├── controller/    # EmotionController → /emotion  ← 서비스 없이 mapper 직접 호출
│   │   ├── mapper/        # EmotionMapper
│   │   └── payload/       # EmotionResponse
│   ├── location/          # 위치(시/도, 시/군/구) 조회
│   │   ├── controller/    # LocationController → /location  ← 서비스 없이 mapper 직접 호출
│   │   ├── mapper/        # LocationMapper
│   │   └── payload/       # SigunguResponse
│   └── users/             # 사용자 관리 (탈퇴만)
│       └── controller/    # UserController → /users  ← ProfileService 위임
├── common/
│   ├── config/        # SecurityConfig, S3Config, SwaggerConfig, RestTemplateConfig
│   ├── code/          # ErrorCode enum
│   ├── exception/     # GlobalExceptionHandler, BusinessException
│   └── payload/       # ApiResponse<T>, ErrorResponse
└── test/              # S3Service test stub, TestController
```

### API Endpoints

| 메서드 | URL | 설명 | 인증 |
|---|---|---|---|
| POST | /auth/login | 소셜 로그인 (provider: kakao/naver/apple) | 불필요 |
| POST | /auth/refresh | 액세스 토큰 갱신 (리프레시 토큰 사용) | 불필요 |
| POST | /auth/logout | 로그아웃 (리프레시 토큰 무효화) | 필요 |
| POST | /profile/create | 프로필 등록 (UNREGISTERED → REGISTERED) | 필요 |
| GET | /profile/me | 내 프로필 조회 | 필요 |
| PATCH | /profile/me | 내 프로필 수정 | 필요 |
| GET | /users/{userId} | 타 사용자 프로필 조회 (REGISTERED 유저만) | 필요 |
| DELETE | /users/me | 회원 탈퇴 | 필요 |
| GET | /posts | 게시글 목록 (사용자 위치 기반 자동 필터) | 필요 |
| GET | /posts/me | 내 게시글 목록 | 필요 |
| GET | /posts/{postId} | 게시글 상세 (댓글 포함) | 필요 |
| POST | /posts | 게시글 생성 | 필요 |
| PATCH | /posts/{postId} | 게시글 수정 | 필요 |
| DELETE | /posts/{postId} | 게시글 삭제 (soft delete) | 필요 |
| POST | /posts/{postId}/like | 좋아요 토글 | 필요 |
| POST | /posts/{postId}/comments | 댓글 작성 | 필요 |
| GET | /emotion | 감정 태그 목록 | 필요 |
| GET | /location/sido | 시/도 목록 | 불필요 |
| GET | /location/sigungu?siDo= | 시/군/구 목록 | 불필요 |

### Request lifecycle

1. `JwtAuthenticationFilter`가 `Authorization: Bearer <token>`을 검증하고, `JwtUser` (userId, status)를 Spring Security principal로 설정한다.
   - 필터 제외 경로: `/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/test/**`
   - `SecurityConfig`는 `.anyRequest().permitAll()`로 설정되어 있으나, 실질적 인증은 JwtAuthenticationFilter가 담당한다. 필터를 통과하지 못하면 컨트롤러에서 `@AuthenticationPrincipal`이 null이 되어 NPE가 발생한다.
2. Controllers extract the principal via `@AuthenticationPrincipal JwtUser jwtUser`.
3. Services call MyBatis mapper interfaces; mapper XML lives under `src/main/resources/mapper/`.
4. All responses are wrapped in `ApiResponse<T>` (`{ "data": ... }`); errors go through `GlobalExceptionHandler` → `ErrorResponse`.

### Auth flow

- Social login hits `AuthController` → `SocialAuthService` delegates to provider-specific client (Kakao/Naver/Apple).
- On success, `JwtProvider` issues an access token (30 min, claims: `userId`, `status`) and a refresh token (14 days, subject = userId).
- 리프레시 토큰은 DB (`users.refresh_token`, `users.refresh_token_expires_at`)에 저장된다. 갱신 요청 시 DB 값과 대조 검증.
- New users have `status = UNREGISTERED` and must complete profile registration via `ProfileController` before accessing other features.
- 로그아웃 시 DB의 `refresh_token`, `refresh_token_expires_at`을 NULL로 초기화한다.

### MyBatis conventions

- Mapper interfaces live alongside domain classes (`business/<domain>/mapper/`); XML files are at `src/main/resources/mapper/<domain>/`.
- `application.yml` enables `map-underscore-to-camel-case: true`, so DB column `created_at` maps to `createdAt` automatically.
- MyBatis debug logging is on (`logging.level.com.emotionmap: DEBUG`).
- **Post 조회 패턴 (N+1 회피)**: 게시글 목록/상세 조회 시 images와 emotions를 별도 쿼리로 일괄 조회한 뒤 Java 코드에서 postId 기준으로 조립한다.
  ```
  getPosts() → postIdList 추출 → getPostsImage(postIdList), getPostsEmotion(postIdList) → 각 post에 set
  ```

### Business rules

- **게시글 생성 필수값**: `locationId`, `emotionIds` (비어있으면 `INVALID_POST_REQUEST`)
- **게시글/댓글 구분 없음**: posts 테이블 하나로 모두 표현한다. `parent_id = null`이면 루트 게시글, `parent_id`가 있으면 하위 게시글이다. "댓글" 개념이 아니라 **하위 게시글 진입** 구조 — 하위 게시글을 클릭하면 그 게시글을 루트로 하는 새 화면으로 진입하고, 거기서 또 하위 게시글을 작성할 수 있다. 중첩 렌더링(대댓글)은 없다. 항상 "현재 게시글 + 직계 하위 게시글" 구조이므로 재귀 조회가 필요 없다.
- **하위 게시글 생성**: `POST /posts/{postId}/comments` — 내부적으로 `insertPost`를 재사용하며 `parentId`를 설정. `depth`는 DB에서 부모의 `depth + 1`로 자동 계산됨.
- **게시글 삭제**: soft delete — `status = 'DELETED'` 업데이트, DB에서 실제 삭제하지 않음.
- **좋아요 토글**: 반환값 `"Y"` (좋아요 추가) / `"N"` (취소).
- **피드 조회 위치 필터**: 사용자의 `users.location_id`로 자동 필터링. location이 없으면 전체 조회.
- **감정 태그 (프로필)**: 최소 1개, 최대 5개 제한 (애플리케이션 레이어 검증).
- **권한 검사**: 수정/삭제 시 `posts.user_id`와 요청자 userId 비교. 불일치 시 `FORBIDDEN`.
- **회원 탈퇴**: `users` 레코드 완전 삭제 (ON DELETE CASCADE로 관련 데이터 자동 삭제).

### Common coding patterns

**에러 발생**
```java
throw new BusinessException(ErrorCode.POST_NOT_FOUND);
```

**응답 래핑**
```java
return ResponseEntity.ok(ApiResponse.of(data));  // 데이터 있는 경우
return ResponseEntity.ok(ApiResponse.of(null));  // void
```

**페이지네이션** (게시글 목록)
- 파라미터: `page` (1-based, default=1), `size` (default=20)
- offset 계산: `offset = (page - 1) * size`

**소유권 검증 패턴**
```java
Long ownerId = postsMapper.selectPostUserId(postId);
if (ownerId == null) throw new BusinessException(ErrorCode.POST_NOT_FOUND);
if (!ownerId.equals(userId)) throw new BusinessException(ErrorCode.FORBIDDEN);
```

### Key domain relationships

- `posts` → `post_images` (one-to-many), `post_emotion_tag` → `emotion_tags` (many-to-many), `post_likes`
- Post list queries join images and emotions into `PostListResponse.imageList` and `PostListResponse.emotionList`.

### Environment config

| File | Purpose |
|---|---|
| `application.yml` | Base (MyBatis paths, active profile selector) |
| `application-local.yml` | Local MySQL, JWT secret, AWS credentials |
| `application-prod.yml` | Production overrides |

AWS S3 is used for image storage; credentials and bucket name are set per-profile.

### Swagger UI

Available at `http://localhost:8080/swagger-ui/index.html` when running locally. All public endpoints are documented with `@Tag` / `@Operation` annotations.

## Security Rules

- Do not print DB passwords, access tokens, API keys, or secret values.
- If environment files are needed, summarize only key names and never expose values.
- Never modify production-like configuration without explicit approval.

## 작업 응답 규칙

- 사용자의 질문과 작업 지시는 한국어로 해석하고, 답변도 한국어로 작성한다.
- 파일 수정 전에는 먼저 수정 계획을 설명한다.
- 사용자가 명시적으로 승인하기 전까지 파일을 수정하지 않는다.
- 한 번에 여러 영역을 수정하지 않는다.
- 백엔드, 프론트엔드, DB 변경은 가능한 한 분리해서 진행한다.
- 기존 코드 스타일을 우선 따른다.
- 대규모 리팩토링보다 최소 수정 방식을 우선한다.
- 새 라이브러리 추가가 필요하면 먼저 이유를 설명하고 확인을 받는다.
- 수정 후에는 변경된 파일 목록, 변경 이유, 확인해야 할 테스트를 요약한다.

## 작업 제한 규칙

- 요청 범위 밖의 파일은 수정하지 않는다.
- 보안 정보, 환경 변수, DB 접속 정보, 토큰 값은 출력하지 않는다.
- 불확실한 부분은 추측으로 수정하지 말고 먼저 질문하거나 가정 사항을 명시한다.
- 코드 삭제가 필요한 경우 삭제 이유를 먼저 설명한다.

## Domain Entity Summary

> 상세 테이블 컬럼 및 관계 다이어그램: [docs/entity-summary.md](docs/entity-summary.md)

### User
- 소셜 로그인(Kakao / Naver / Apple)으로 생성되며, provider + provider_user_id로 식별한다.
- status: UNREGISTERED → REGISTERED (프로필 등록 시 전환)

### Post
- 본문(content), 감정 태그(N개), 이미지(N개), 위치(1개)를 포함한다.
- 댓글은 별도 테이블 없이 posts.parent_id + depth 로 계층 표현한다.

### Emotion
- 게시글에 선택되는 감정 태그 (emotion_tags 테이블)
- 감정 목록 조회 API로 제공한다.

### Image
- 게시글 첨부 이미지; S3 URL, 정렬 순서(sort_order) 보유
- 추후 개발 예정

### Location
- 게시글 1개에 위치 1개 연결 (locations 테이블)
- si_do(시/도) + si_gun_gu(시/군/구) 계층 구조로 전국 단위 확장 가능
- 사용자가 시/도 선택 후 시/군/구를 선택하는 2단계 UI 흐름에 대응한다.
- posts.location_id는 구 단위 locations.id를 참조한다.

### Map
- 위치 데이터 기반으로 게시글을 지도 위에 표시한다.
