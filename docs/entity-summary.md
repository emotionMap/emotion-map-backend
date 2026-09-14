# Entity Summary

## 테이블 관계 (ERD 요약)

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

- 게시글과 댓글은 완전히 분리된 테이블이다. `posts`는 항상 최상위 글이고, 댓글은 `comments`가 전담하며 `parent_comment_id`로 자기참조해 **댓글 → 대댓글 → 대대댓글 → ...로 깊이 제한 없이** 중첩된다.
- 프로필 개념이 없는 완전 익명 게시판이다 — `post_anonymous_nickname`이 (게시글, 유저) 조합별로 무작위 닉네임을 저장한다. 게시글 작성자든 그 아래 어느 깊이의 댓글 작성자든 같은 `post_id` 기준으로 조회/배정된다. `user_emotion_tag`(프로필용 감정 태그) 테이블은 프로필 제거와 함께 삭제됨.

---

## 테이블 상세

### users

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | id | bigint AUTO_INCREMENT | PK |
| device_id | deviceId | varchar(64) NOT NULL | 클라이언트가 기기별로 생성해 보관하는 익명 식별자 (본인인증 없음) |
| location_id | locationId | int NULL | FK → locations.id (가입 시 설정 필수, 이후 변경 가능). NULL이면 위치 설정/탈퇴 외 API 사용 불가 |
| refresh_token | refreshToken | varchar NULL | 리프레시 토큰 값 |
| refresh_token_expires_at | refreshTokenExpiresAt | datetime NULL | 리프레시 토큰 만료 일시 |
| last_login_at | lastLoginAt | datetime NULL | 마지막 로그인 일시 |
| created_at | createdAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성 일시 |
| updated_at | updatedAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 수정 일시 |

> device_id UNIQUE — 같은 기기는 항상 같은 계정으로 로그인

---

### posts

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | postId | bigint AUTO_INCREMENT | PK |
| user_id | userId | bigint NOT NULL | FK → users.id (ON DELETE CASCADE) |
| location_id | locationId | int NULL | FK → locations.id |
| content | content | text NULL | 본문 |
| created_at | createdAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성 일시 |
| updated_at | updatedAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 수정 일시 |
| status | status | varchar(20) NOT NULL DEFAULT 'ACTIVE' | ACTIVE / DELETED |

---

### locations

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | locationId | int AUTO_INCREMENT | PK |
| si_do | siDo | varchar(100) NOT NULL | 시/도 (서울특별시, 경기도 등) |
| si_gun_gu | siGunGu | varchar(100) NOT NULL | 시/군/구 (강남구, 수원시 등) |

---

### post_images

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | id | bigint AUTO_INCREMENT | PK |
| post_id | postId | bigint NOT NULL | FK → posts.id (ON DELETE CASCADE) |
| image_url | url | varchar(300) NOT NULL | S3 이미지 URL |
| sort_order | sortOrder | int DEFAULT 0 | 정렬 순서 |
| created_at | createdAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성 일시 |

---

### emotion_tags

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | id | int AUTO_INCREMENT | PK |
| name | name | varchar(50) NOT NULL | 감정 이름 (UNIQUE) |
| emoji | emoji | varchar(50) NULL | 이모지 문자 |

---

### comments

댓글 전용 테이블. `parent_comment_id`로 자기참조해 댓글/대댓글/대대댓글/... 깊이 제한 없이 중첩된다. 위치/감정 태그는 없다.

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | commentId | bigint AUTO_INCREMENT | PK |
| post_id | - | bigint NOT NULL | FK → posts.id (ON DELETE CASCADE) |
| parent_comment_id | parentCommentId | bigint NULL | FK → comments.id (self, ON DELETE CASCADE), null이면 게시글에 바로 단 최상위 댓글 |
| user_id | - | bigint NOT NULL | FK → users.id (ON DELETE CASCADE) |
| content | content | text NOT NULL | 내용 |
| status | status | varchar(20) NOT NULL DEFAULT 'ACTIVE' | ACTIVE / DELETED (soft delete, 삭제돼도 대댓글은 유지) |
| created_at | createdAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성 일시 |
| updated_at | updatedAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 수정 일시 |

---

### post_emotion_tag

| 컬럼 | DB 타입 | 설명 |
|---|---|---|
| post_id | bigint NOT NULL | PK (복합), FK → posts.id |
| emotion_id | int NOT NULL | PK (복합), FK → emotion_tags.id |

---

### post_likes

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | id | bigint AUTO_INCREMENT | PK |
| post_id | postId | bigint NOT NULL | FK → posts.id (ON DELETE CASCADE) |
| user_id | userId | bigint NOT NULL | FK → users.id (ON DELETE CASCADE) |
| created_at | createdAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성 일시 |

> (post_id, user_id) UNIQUE — 동일 사용자의 중복 좋아요 방지

---

### post_anonymous_nickname

게시글별로 유저에게 배정된 익명 닉네임. 게시글 본문이든 그 아래 어느 깊이의 댓글이든, 같은 post_id 안에서는 같은 user_id가 항상 같은 nickname을 받는다.

| 컬럼 | DB 타입 | 설명 |
|---|---|---|
| post_id | bigint NOT NULL | PK (복합), FK → posts.id (ON DELETE CASCADE) |
| user_id | bigint NOT NULL | PK (복합), FK → users.id (ON DELETE CASCADE) |
| nickname | varchar(50) NOT NULL | 형용사+명사 조합으로 무작위 생성 (예: "포근한 펭귄") |
| created_at | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성 일시 |

> (post_id, nickname) UNIQUE — 같은 게시글 안에서 닉네임 중복 방지

---

## Java 클래스 ↔ 테이블 대응

| 클래스 | 위치 | 대응 테이블 |
|---|---|---|
| `UserVo` | `auth/vo/` | users |
| `JwtUser` | `jwt/vo/` | users (JWT 클레임) |
| `PostListResponse` | `posts/payload/` | posts + post_anonymous_nickname + join |
| `PostDetailResponse` | `posts/payload/` | posts + post_anonymous_nickname + join |
| `Image` | `posts/payload/` | post_images |
| `Emotion` | `posts/payload/` | post_emotion_tag + emotion_tags |
| `EmotionResponse` | `emotion/payload/` | emotion_tags |
| `SigunguResponse` | `location/payload/` | locations |
| `LocationUpdateRequest` | `users/payload/` | users.location_id |
| `AnonymousNicknameService` | `posts/service/` | post_anonymous_nickname |
| `CommentResponse` / `CommentRow` | `comments/payload,vo/` | comments + post_anonymous_nickname (중첩 트리로 조립) |
| `MapRegionResponse` / `MapEmotionRow` | `map/payload,vo/` | post_emotion_tag + posts + locations (지역별 최근 5개, 스키마 변경 없음) |
| `EmotionStatResponse` | `posts/payload/` | post_emotion_tag + posts + emotion_tags (마이페이지 개인 감정 통계, 기간 집계) |
