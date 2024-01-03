package com.icebox.freshmate.domain.post.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.post.application.dto.request.PostReq;
import com.icebox.freshmate.domain.post.application.dto.response.PostRes;
import com.icebox.freshmate.domain.post.application.dto.response.PostsRes;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.post.domain.PostRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@InjectMocks
	private PostService postService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PostRepository postRepository;

	private Member member;
	private Post post;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.realName("성이름")
			.username("aaaa1111")
			.password("aaaa1111!")
			.nickName("닉네임닉네임")
			.role(Role.USER)
			.build();

		post = Post.builder()
			.title("제목")
			.content("내용")
			.member(member)
			.build();
	}

	@DisplayName("게시글 생성 테스트")
	@Test
	void create() {
		//given
		PostReq postReq = new PostReq(post.getTitle(), post.getContent());

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(postRepository.save(any(Post.class))).thenReturn(post);

		//when
		PostRes postRes = postService.create(postReq, member.getUsername());

		//then
		assertThat(postRes.title()).isEqualTo(post.getTitle());
		assertThat(postRes.content()).isEqualTo(post.getContent());
	}

	@DisplayName("게시글 단건 조회 테스트")
	@Test
	void findById() {
		//given
		Long postId = 1L;

		when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

		//when
		PostRes postRes = postService.findById(postId);

		//then
		assertThat(postRes.title()).isEqualTo(post.getTitle());
		assertThat(postRes.content()).isEqualTo(post.getContent());
	}

	@DisplayName("작성자별 모든 게시글 조회 테스트")
	@Test
	void findAllByMemberId() {
		//given
		Long memberId = 1L;

		Post post2 = Post.builder()
			.title("제목2")
			.content("내용2")
			.member(member)
			.build();

		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(postRepository.findAllByMemberId(any())).thenReturn(List.of(post, post2));

		//when
		PostsRes postsRes = postService.findAllByMemberId(memberId);

		//then
		assertThat(postsRes.posts()).hasSize(2);
		assertThat(postsRes.posts().get(0).title()).isEqualTo(post.getTitle());
		assertThat(postsRes.posts().get(0).content()).isEqualTo(post.getContent());
	}

	@DisplayName("게시글 수정 테스트")
	@Test
	void update() {
		//given
		Long postId = 1L;

		PostReq postReq = new PostReq("제목 수정", "내용 수정");

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(postRepository.findByIdAndMemberId(any(), any())).thenReturn(Optional.of(post));

		//when
		PostRes postRes = postService.update(postId, postReq, member.getUsername());

		//then
		assertThat(postRes.title()).isEqualTo(post.getTitle());
		assertThat(postRes.content()).isEqualTo(post.getContent());
	}
}
