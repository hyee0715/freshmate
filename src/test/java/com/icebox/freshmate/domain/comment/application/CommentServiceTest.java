package com.icebox.freshmate.domain.comment.application;

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
import org.springframework.mock.web.MockMultipartFile;

import com.icebox.freshmate.domain.comment.application.dto.request.CommentCreateReq;
import com.icebox.freshmate.domain.comment.application.dto.request.CommentUpdateReq;
import com.icebox.freshmate.domain.comment.application.dto.response.CommentRes;
import com.icebox.freshmate.domain.comment.application.dto.response.CommentsRes;
import com.icebox.freshmate.domain.comment.domain.Comment;
import com.icebox.freshmate.domain.comment.domain.CommentImage;
import com.icebox.freshmate.domain.comment.domain.CommentImageRepository;
import com.icebox.freshmate.domain.comment.domain.CommentRepository;
import com.icebox.freshmate.domain.image.application.ImageService;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.image.application.dto.response.ImagesRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.post.domain.PostRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@InjectMocks
	private CommentService commentService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PostRepository postRepository;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private CommentImageRepository commentImageRepository;

	@Mock
	private ImageService imageService;

	private Member member;
	private Post post;
	private Comment comment;
	private CommentImage commentImage;

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

		comment = Comment.builder()
			.member(member)
			.post(post)
			.content("댓글 내용")
			.build();

		String imageFileName = "image.jpg";
		String imagePath = "https://fake-image-url.com/image.jpg";

		commentImage = CommentImage
			.builder()
			.comment(comment)
			.fileName(imageFileName)
			.path(imagePath)
			.build();

		comment.addCommentImage(commentImage);
	}

	@DisplayName("댓글 생성 테스트")
	@Test
	void create() {
		//given
		Long postId = 1L;

		CommentCreateReq commentCreateReq = new CommentCreateReq(postId, comment.getContent());

		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "Spring Framework".getBytes());
		ImageUploadReq imageUploadReq = new ImageUploadReq(List.of(file));

		ImageRes imageRes = new ImageRes(commentImage.getFileName(), commentImage.getPath());
		ImagesRes imagesRes = new ImagesRes(List.of(imageRes));

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
		when(commentRepository.save(any(Comment.class))).thenReturn(comment);
		when(commentImageRepository.save(any(CommentImage.class))).thenReturn(commentImage);
		when(imageService.store(any(ImageUploadReq.class))).thenReturn(imagesRes);

		//when
		CommentRes commentRes = commentService.create(commentCreateReq, imageUploadReq, member.getUsername());

		//then
		assertThat(commentRes.memberNickName()).isEqualTo(member.getNickName());
		assertThat(commentRes.content()).isEqualTo(comment.getContent());
		assertThat(commentRes.images().get(0).fileName()).isEqualTo(commentImage.getFileName());
		assertThat(commentRes.images().get(0).path()).isEqualTo(commentImage.getPath());
	}

	@DisplayName("게시글 별 모든 댓글 조회 테스트")
	@Test
	void findAllByPostId() {
		//given
		Long postId = 1L;

		Comment comment2 = Comment.builder()
			.member(member)
			.post(post)
			.content("댓글 내용2")
			.build();

		when(commentRepository.findAllByPostId(anyLong())).thenReturn(List.of(comment, comment2));

		//when
		CommentsRes commentsRes = commentService.findAllByPostId(postId);

		//then
		assertThat(commentsRes.comments().get(0).memberNickName()).isEqualTo(comment.getMember().getNickName());
		assertThat(commentsRes.comments().get(0).content()).isEqualTo(comment.getContent());
	}

	@DisplayName("댓글 수정 테스트")
	@Test
	void update() {
		//given
		Long commentId = 1L;
		CommentUpdateReq commentUpdateReq = new CommentUpdateReq("댓글 수정");

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(commentRepository.findByIdAndMemberId(any(), any())).thenReturn(Optional.of(comment));
		when(postRepository.findById(any())).thenReturn(Optional.of(post));

		//when
		CommentRes commentRes = commentService.update(commentId, commentUpdateReq, member.getUsername());

		//then
		assertThat(commentRes.memberNickName()).isEqualTo(member.getNickName());
		assertThat(commentRes.content()).isEqualTo(comment.getContent());
	}
}
