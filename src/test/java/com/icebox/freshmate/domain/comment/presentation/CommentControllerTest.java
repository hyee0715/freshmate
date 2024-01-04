package com.icebox.freshmate.domain.comment.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.comment.application.CommentService;
import com.icebox.freshmate.domain.comment.application.dto.request.CommentCreateReq;
import com.icebox.freshmate.domain.comment.application.dto.request.CommentUpdateReq;
import com.icebox.freshmate.domain.comment.application.dto.response.CommentRes;
import com.icebox.freshmate.domain.comment.application.dto.response.CommentsRes;
import com.icebox.freshmate.domain.comment.domain.Comment;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.post.presentation.PostController;
import com.icebox.freshmate.global.TestPrincipalDetailsService;

@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(value = {CommentController.class})
class CommentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private CommentService commentService;

	private final TestPrincipalDetailsService testUserDetailsService = new TestPrincipalDetailsService();
	private PrincipalDetails principalDetails;

	private Member member;
	private Post post;
	private Comment comment;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(documentationConfiguration(restDocumentationContextProvider))
			.apply(springSecurity())
			.alwaysDo(print()).build();

		principalDetails = (PrincipalDetails) testUserDetailsService.loadUserByUsername(TestPrincipalDetailsService.USERNAME);

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
	}

	@DisplayName("댓글 생성 테스트")
	@Test
	void create() throws Exception {
		//given
		Long commentId = 1L;
		Long postId = 1L;
		Long memberId = 1L;

		CommentCreateReq commentCreateReq = new CommentCreateReq(postId, comment.getContent());
		CommentRes commentRes = new CommentRes(commentId, postId, memberId, member.getNickName(), post.getContent());

		when(commentService.create(any(CommentCreateReq.class), anyString())).thenReturn(commentRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/comments")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(commentCreateReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(commentRes)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.commentId").value(commentId))
			.andExpect(jsonPath("$.postId").value(postId))
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.memberNickName").value(member.getNickName()))
			.andExpect(jsonPath("$.content").value(post.getContent()))
			.andDo(print())
			.andDo(document("comment/comment-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("postId").description("게시글 ID"),
					fieldWithPath("content").description("댓글 내용")
				),
				responseFields(
					fieldWithPath("commentId").type(NUMBER).description("댓글 ID"),
					fieldWithPath("postId").type(NUMBER).description("게시글 ID"),
					fieldWithPath("memberId").type(NUMBER).description("댓글 작성자 ID"),
					fieldWithPath("memberNickName").type(STRING).description("댓글 작성자 닉네임"),
					fieldWithPath("content").type(STRING).description("댓글 내용")
				)
			));
	}

	@DisplayName("게시글 별 모든 댓글 조회 테스트")
	@Test
	void findAllByPostId() throws Exception {
		//given
		Long postId = 1L;
		Long memberId = 1L;

		Comment comment2 = Comment.builder()
			.member(member)
			.post(post)
			.content("댓글 내용2")
			.build();

		CommentRes commentRes1 = new CommentRes(1L, postId, memberId, member.getNickName(), comment.getContent());
		CommentRes commentRes2 = new CommentRes(2L, postId, memberId, member.getNickName(), comment2.getContent());

		CommentsRes commentsRes = new CommentsRes(List.of(commentRes1, commentRes2));

		when(commentService.findAllByPostId(anyLong())).thenReturn(commentsRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/comments?post-id=" + postId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(commentsRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.comments", hasSize(2)))
			.andExpect(jsonPath("$.comments[0].commentId").value(1L))
			.andExpect(jsonPath("$.comments[0].postId").value(postId))
			.andExpect(jsonPath("$.comments[0].memberId").value(memberId))
			.andExpect(jsonPath("$.comments[0].memberNickName").value(member.getNickName()))
			.andExpect(jsonPath("$.comments[0].content").value(comment.getContent()))
			.andDo(print())
			.andDo(print())
			.andDo(document("comment/comment-find-all-by-post-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("post-id").description("게시글 ID")
				),
				responseFields(
					fieldWithPath("comments").type(ARRAY).description("댓글 배열"),
					fieldWithPath("comments[].commentId").type(NUMBER).description("댓글 ID"),
					fieldWithPath("comments[].postId").type(NUMBER).description("게시글 ID"),
					fieldWithPath("comments[].memberId").type(NUMBER).description("댓글 작성자 ID"),
					fieldWithPath("comments[].memberNickName").type(STRING).description("댓글 작성자 닉네임"),
					fieldWithPath("comments[].content").type(STRING).description("댓글 내용")
				)
			));
	}

	@DisplayName("댓글 수정 테스트")
	@Test
	void update() throws Exception {
		//given
		Long commentId = 1L;
		Long postId = 1L;
		Long memberId = 1L;

		CommentUpdateReq commentUpdateReq = new CommentUpdateReq("댓글 내용 수정");
		CommentRes commentRes = new CommentRes(commentId, postId, memberId, member.getNickName(), commentUpdateReq.content());

		when(commentService.update(anyLong(), any(CommentUpdateReq.class), anyString())).thenReturn(commentRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/comments/{id}", commentId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(commentUpdateReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(commentRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.commentId").value(commentId))
			.andExpect(jsonPath("$.postId").value(postId))
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.memberNickName").value(member.getNickName()))
			.andExpect(jsonPath("$.content").value(commentUpdateReq.content()))
			.andDo(print())
			.andDo(document("comment/comment-update",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(parameterWithName("id").description("댓글 ID")),
				requestFields(
					fieldWithPath("content").description("수정할 댓글 내용")
				),
				responseFields(
					fieldWithPath("commentId").type(NUMBER).description("댓글 ID"),
					fieldWithPath("postId").type(NUMBER).description("게시글 ID"),
					fieldWithPath("memberId").type(NUMBER).description("댓글 작성자 ID"),
					fieldWithPath("memberNickName").type(STRING).description("댓글 작성자 닉네임"),
					fieldWithPath("content").type(STRING).description("수정된 댓글 내용")
				)
			));
	}

	@DisplayName("댓글 삭제 테스트")
	@Test
	void delete() throws Exception {
		//given
		Long commentId = 1L;

		doNothing().when(commentService).delete(anyLong(), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/comments/{id}", commentId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andDo(document("comment/comment-delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(
					parameterWithName("id").description("댓글 ID")
				)
			));
	}
}
