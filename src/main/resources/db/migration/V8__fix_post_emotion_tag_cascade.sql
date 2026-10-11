-- post_emotion_tag.post_id FK(fk2)에 ON DELETE CASCADE가 빠져있던 문제 수정
-- 증상: 글을 작성한 유저가 회원 탈퇴(DELETE /users/me)하면
--      "Cannot delete or update a parent row: a foreign key constraint fails
--       (post_emotion_tag, CONSTRAINT fk2 ...)" 에러로 탈퇴가 500 실패함.
-- 게시글 생성 시 emotionIds가 필수라 글을 하나라도 쓴 유저는 전부 영향을 받는다.
-- 같은 ALTER 안에서 fk2를 DROP하고 같은 이름으로 다시 ADD하면 MySQL이
-- "Duplicate foreign key constraint name 'fk2'"로 거부한다. 형제 제약(fk_post_emotion_tag_emotion)과
-- 이름 스타일을 맞춰 새 이름으로 다시 생성한다.
ALTER TABLE post_emotion_tag
    DROP FOREIGN KEY fk2,
    ADD CONSTRAINT fk_post_emotion_tag_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE;
