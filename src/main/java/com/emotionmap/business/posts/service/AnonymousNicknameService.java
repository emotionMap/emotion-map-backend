package com.emotionmap.business.posts.service;

import com.emotionmap.business.posts.mapper.PostsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * 같은 게시글(스레드)에서는 같은 유저에게 항상 같은 익명 닉네임을 부여하고,
 * 다른 스레드에서는 서로 다른 닉네임을 부여한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnonymousNicknameService {

    private static final String[] ADJECTIVES = {
            "몽글몽글", "잔잔한", "포근한", "말랑한", "촉촉한", "느긋한", "산뜻한", "조용한",
            "따뜻한", "몰랑한", "설레는", "아련한", "고요한", "말갛은", "포슬포슬한",
            "즐거운", "신나는", "뛰어노는", "그네타는", "춤추는", "노래하는", "웃음짓는", "통통튀는", "팔랑이는", "콩닥이는"
    };

    private static final String[] NOUNS = {
            "고양이", "구름", "파도", "감자", "여우", "토끼", "달빛", "우산",
            "고슴도치", "민들레", "은행잎", "강아지", "펭귄", "수달", "코알라", "다람쥐",
            "사슴", "참새", "오리", "햇살", "별빛", "바람", "딸기", "나비", "풍선", "도토리"
    };

    private static final int MAX_RETRY = 5;

    private final PostsMapper postsMapper;
    private final SecureRandom random = new SecureRandom();

    public String getOrCreateNickname(Long postId, Long userId) {
        String existing = postsMapper.findAnonymousNickname(postId, userId);
        if (existing != null) {
            return existing;
        }

        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            String candidate = generate(attempt);
            try {
                postsMapper.insertAnonymousNickname(postId, userId, candidate);
                return candidate;
            } catch (DataIntegrityViolationException e) {
                // 같은 스레드 안에서 닉네임이 겹쳤을 때: 재시도
                log.info("[AnonymousNickname] 닉네임 충돌, 재시도 - postId: {}, attempt: {}", postId, attempt);
            }
        }

        throw new IllegalStateException("익명 닉네임 생성에 반복적으로 실패했습니다. postId=" + postId);
    }

    private String generate(int attempt) {
        String adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[random.nextInt(NOUNS.length)];

        // 첫 시도는 깔끔하게, 계속 충돌하면 숫자를 붙여 충돌 가능성을 낮춘다.
        if (attempt == 0) {
            return adjective + " " + noun;
        }
        int suffix = random.nextInt(900) + 100;
        return adjective + " " + noun + suffix;
    }
}
