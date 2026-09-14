package com.emotionmap.business.posts.service;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.comments.service.CommentsService;
import com.emotionmap.business.posts.mapper.PostsMapper;
import com.emotionmap.business.posts.payload.PostCreateRequest;
import com.emotionmap.business.posts.payload.PostUpdateRequest;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostsServiceTest {

    @Mock
    private PostsMapper postsMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AnonymousNicknameService anonymousNicknameService;
    @Mock
    private CommentsService commentsService;

    @InjectMocks
    private PostsService postsService;

    @Test
    void create_위치가_없으면_INVALID_POST_REQUEST() {
        PostCreateRequest request = new PostCreateRequest();
        request.setEmotionIds(List.of(1L));

        assertThatThrownBy(() -> postsService.create(request, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_POST_REQUEST);

        verifyNoInteractions(postsMapper);
    }

    @Test
    void create_감정태그가_없으면_INVALID_POST_REQUEST() {
        PostCreateRequest request = new PostCreateRequest();
        request.setLocationId(1L);
        request.setEmotionIds(List.of());

        assertThatThrownBy(() -> postsService.create(request, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_POST_REQUEST);
    }

    @Test
    void create_정상이면_저장하고_닉네임을_배정한다() {
        PostCreateRequest request = new PostCreateRequest();
        request.setLocationId(1L);
        request.setEmotionIds(List.of(10L, 20L));
        doAnswer(inv -> {
            request.setPostId(99L);
            return null;
        }).when(postsMapper).insertPost(request);

        Long postId = postsService.create(request, 1L);

        assertThat(postId).isEqualTo(99L);
        verify(postsMapper).insertPostEmotionTags(99L, List.of(10L, 20L));
        verify(anonymousNicknameService).getOrCreateNickname(99L, 1L);
    }

    @Test
    void update_게시글이_없으면_POST_NOT_FOUND() {
        when(postsMapper.selectPostUserId(1L)).thenReturn(null);

        assertThatThrownBy(() -> postsService.update(1L, new PostUpdateRequest(), 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    @Test
    void update_소유자가_아니면_FORBIDDEN() {
        when(postsMapper.selectPostUserId(1L)).thenReturn(2L);

        assertThatThrownBy(() -> postsService.update(1L, new PostUpdateRequest(), 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);

        verify(postsMapper, never()).updatePost(any());
    }

    @Test
    void delete_소유자가_아니면_FORBIDDEN() {
        when(postsMapper.selectPostUserId(1L)).thenReturn(2L);

        assertThatThrownBy(() -> postsService.delete(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);

        verify(postsMapper, never()).softDeletePost(any());
    }

    @Test
    void toggleLike_없으면_추가하고_Y를_반환한다() {
        when(postsMapper.selectPostUserId(1L)).thenReturn(2L);
        when(postsMapper.existsLike(1L, 1L)).thenReturn(false);

        String result = postsService.toggleLike(1L, 1L);

        assertThat(result).isEqualTo("Y");
        verify(postsMapper).insertLike(1L, 1L);
    }

    @Test
    void toggleLike_있으면_취소하고_N을_반환한다() {
        when(postsMapper.selectPostUserId(1L)).thenReturn(2L);
        when(postsMapper.existsLike(1L, 1L)).thenReturn(true);

        String result = postsService.toggleLike(1L, 1L);

        assertThat(result).isEqualTo("N");
        verify(postsMapper).deleteLike(1L, 1L);
    }
}
