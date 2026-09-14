package com.emotionmap.business.posts.service;

import com.emotionmap.business.posts.mapper.PostsMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnonymousNicknameServiceTest {

    @Mock
    private PostsMapper postsMapper;

    @InjectMocks
    private AnonymousNicknameService anonymousNicknameService;

    @Test
    void 기존_닉네임이_있으면_그대로_반환하고_새로_생성하지_않는다() {
        when(postsMapper.findAnonymousNickname(1L, 1L)).thenReturn("포근한 펭귄");

        String nickname = anonymousNicknameService.getOrCreateNickname(1L, 1L);

        assertThat(nickname).isEqualTo("포근한 펭귄");
        verify(postsMapper, never()).insertAnonymousNickname(any(), any(), any());
    }

    @Test
    void 기존_닉네임이_없으면_새로_생성해서_저장한다() {
        when(postsMapper.findAnonymousNickname(1L, 1L)).thenReturn(null);

        String nickname = anonymousNicknameService.getOrCreateNickname(1L, 1L);

        assertThat(nickname).isNotBlank();
        verify(postsMapper).insertAnonymousNickname(eq(1L), eq(1L), eq(nickname));
    }

    @Test
    void 닉네임이_충돌하면_재시도해서_결국_성공한다() {
        when(postsMapper.findAnonymousNickname(1L, 1L)).thenReturn(null);
        doThrow(new DataIntegrityViolationException("dup"))
                .doNothing()
                .when(postsMapper).insertAnonymousNickname(any(), any(), any());

        String nickname = anonymousNicknameService.getOrCreateNickname(1L, 1L);

        assertThat(nickname).isNotBlank();
        verify(postsMapper, times(2)).insertAnonymousNickname(any(), any(), any());
    }

    @Test
    void 재시도를_다_소진하면_예외를_던진다() {
        when(postsMapper.findAnonymousNickname(1L, 1L)).thenReturn(null);
        doThrow(new DataIntegrityViolationException("dup"))
                .when(postsMapper).insertAnonymousNickname(any(), any(), any());

        assertThatThrownBy(() -> anonymousNicknameService.getOrCreateNickname(1L, 1L))
                .isInstanceOf(IllegalStateException.class);

        verify(postsMapper, times(5)).insertAnonymousNickname(any(), any(), any());
    }
}
