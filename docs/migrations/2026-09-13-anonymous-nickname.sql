-- 프로필 제거 + 스레드 단위 익명 닉네임 도입
-- 적용 대상: 로컬 개발 DB (emotionMap)
-- 주의: users.nickname/bio/profile_image_url/status 와 user_emotion_tag 데이터는 영구 삭제됩니다.
-- users.location_id는 유지합니다 (가입 시 위치 설정은 계속 필수).

-- 1. users: 프로필 관련 컬럼 제거 (location_id는 유지)
ALTER TABLE users
    DROP COLUMN nickname,
    DROP COLUMN bio,
    DROP COLUMN profile_image_url,
    DROP COLUMN status;

-- 2. 프로필용 감정 태그 테이블 제거 (게시글의 post_emotion_tag와는 별개)
DROP TABLE IF EXISTS user_emotion_tag;

-- 3. posts: 스레드의 최상위(루트) 게시글을 즉시 알 수 있도록 컬럼 추가
--    NULL이면 이 게시글 자체가 루트라는 뜻.
ALTER TABLE posts
    ADD COLUMN root_post_id BIGINT NULL AFTER parent_id,
    ADD CONSTRAINT fk_posts_root FOREIGN KEY (root_post_id) REFERENCES posts (id) ON DELETE CASCADE,
    ADD INDEX idx_posts_root_post_id (root_post_id);

-- 4. 스레드(루트 게시글)별 유저 -> 익명 닉네임 배정 테이블
CREATE TABLE post_anonymous_nickname (
    root_post_id BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    nickname     VARCHAR(50) NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (root_post_id, user_id),
    UNIQUE KEY uq_root_nickname (root_post_id, nickname),
    CONSTRAINT fk_pan_root FOREIGN KEY (root_post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_pan_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. 기존 하위 게시글(댓글) 데이터 보정: root_post_id가 비어있으면 parent_id로 채운다.
--    (마이그레이션 이전에 만들어진 depth>=1 게시글 대상. 1단계 중첩까지만 존재한다는 전제.)
UPDATE posts SET root_post_id = parent_id WHERE parent_id IS NOT NULL AND root_post_id IS NULL;
