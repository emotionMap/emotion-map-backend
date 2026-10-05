-- posts.status(soft delete) 컬럼이 EC2 운영 DB에 누락되어 있어 보강
-- split-posts-and-comments.sql 실행 전에 적용 필요 (해당 마이그레이션이 posts.status를 참조함)
ALTER TABLE posts ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
