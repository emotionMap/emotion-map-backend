-- 게시글/댓글 완전 분리 + 무한 대댓글 구조
-- 적용 대상: 로컬 개발 DB (emotionMap)

-- 1. 댓글 전용 테이블 생성 (자기참조로 무제한 중첩)
CREATE TABLE comments (
    id                BIGINT NOT NULL AUTO_INCREMENT,
    post_id           BIGINT NOT NULL,
    parent_comment_id BIGINT NULL,
    user_id           BIGINT NOT NULL,
    content           TEXT NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_comments_post_id (post_id),
    KEY idx_comments_parent_comment_id (parent_comment_id),
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_comment_id) REFERENCES comments (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. 기존 "하위 게시글로 만든 댓글" 데이터를 comments로 이관
--    (지금까지 데이터는 전부 depth=1이라 parent_comment_id는 NULL로 단순 이관 가능)
INSERT INTO comments (post_id, parent_comment_id, user_id, content, status, created_at)
SELECT COALESCE(root_post_id, parent_id), NULL, user_id, COALESCE(content, ''), status, created_at
FROM posts
WHERE parent_id IS NOT NULL;

-- 3. 이관 완료한 원본 행 삭제
DELETE FROM posts WHERE parent_id IS NOT NULL;

-- 4. posts에서 댓글-겸용 컬럼 제거 (게시글은 이제 항상 최상위)
ALTER TABLE posts
    DROP FOREIGN KEY fk_posts_parent,
    DROP FOREIGN KEY fk_posts_root,
    DROP COLUMN parent_id,
    DROP COLUMN root_post_id,
    DROP COLUMN depth;

-- 5. post_anonymous_nickname: root_post_id -> post_id로 의미 명확화 (더 이상 "루트 계산"이 아니라 그냥 게시글 id)
ALTER TABLE post_anonymous_nickname
    DROP FOREIGN KEY fk_pan_root,
    CHANGE COLUMN root_post_id post_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_pan_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE;

ALTER TABLE post_anonymous_nickname RENAME INDEX uq_root_nickname TO uq_post_nickname;
