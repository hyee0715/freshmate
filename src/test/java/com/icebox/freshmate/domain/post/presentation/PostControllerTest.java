package com.icebox.freshmate.domain.post.presentation;

import static org.hamcrest.Matchers.hasSize;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.post.application.PostService;
import com.icebox.freshmate.domain.post.application.dto.request.PostReq;
import com.icebox.freshmate.domain.post.application.dto.response.PostRes;
import com.icebox.freshmate.domain.post.application.dto.response.PostsRes;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.global.TestPrincipalDetailsService;

@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(value = {PostController.class})
class PostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private PostService postService;

	private final TestPrincipalDetailsService testUserDetailsService = new TestPrincipalDetailsService();
	private PrincipalDetails principalDetails;

	private Member member;
	private Post post;

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
	}

	@DisplayName("게시글 생성 테스트")
	@Test
	void create() throws Exception {
		//given
		Long postId = 1L;
		Long memberId = 1L;

		PostReq postReq = new PostReq(post.getTitle(), post.getContent());
		PostRes postRes = new PostRes(postId, memberId, post.getTitle(), post.getContent());

		when(postService.create(any(PostReq.class), anyString())).thenReturn(postRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(postReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(postRes)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.postId").value(postId))
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.title").value(post.getTitle()))
			.andExpect(jsonPath("$.content").value(post.getContent()))
			.andDo(print())
			.andDo(document("post/post-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("title").description("게시글 제목"),
					fieldWithPath("content").description("게시글 내용")
				),
				responseFields(
					fieldWithPath("postId").type(NUMBER).description("게시글 ID"),
					fieldWithPath("memberId").type(NUMBER).description("게시글 작성자 ID"),
					fieldWithPath("title").type(STRING).description("게시글 제목"),
					fieldWithPath("content").type(STRING).description("게시글 내용")
				)
			));
	}

	@DisplayName("게시글 단건 조회 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long postId = 1L;
		Long memberId = 1L;

		PostRes postRes = new PostRes(postId, memberId, post.getTitle(), post.getContent());

		when(postService.findById(anyLong())).thenReturn(postRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/posts/{id}", postId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(postRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.postId").value(postId))
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.title").value(post.getTitle()))
			.andExpect(jsonPath("$.content").value(post.getContent()))
			.andDo(print())
			.andDo(document("post/post-find-by-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("게시글 ID")),
				responseFields(
					fieldWithPath("postId").type(NUMBER).description("게시글 ID"),
					fieldWithPath("memberId").type(NUMBER).description("게시글 작성자 ID"),
					fieldWithPath("title").type(STRING).description("게시글 제목"),
					fieldWithPath("content").type(STRING).description("게시글 내용")
				)
			));
	}

	@DisplayName("작성자별 모든 게시글 조회 테스트")
	@Test
	void findAllByMemberId() throws Exception {
		//given
		Long memberId = 1L;

		Post post2 = Post.builder()
			.title("제목2")
			.content("내용2")
			.member(member)
			.build();

		PostRes postRes1 = new PostRes(1L, memberId, post.getTitle(), post.getContent());
		PostRes postRes2 = new PostRes(2L, memberId, post2.getTitle(), post2.getContent());

		PostsRes postsRes = new PostsRes(List.of(postRes1, postRes2));

		when(postService.findAllByMemberId(anyLong())).thenReturn(postsRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/posts?member-id=" + memberId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(postsRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.posts", hasSize(2)))
			.andExpect(jsonPath("$.posts[0].postId").value(postRes1.postId()))
			.andExpect(jsonPath("$.posts[0].memberId").value(postRes1.memberId()))
			.andExpect(jsonPath("$.posts[0].title").value(postRes1.title()))
			.andExpect(jsonPath("$.posts[0].content").value(postRes1.content()))
			.andDo(print())
			.andDo(document("post/post-find-all-by-member-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("member-id").description("게시글 작성자 ID")
				),
				responseFields(
					fieldWithPath("posts").type(ARRAY).description("게시글 배열"),
					fieldWithPath("posts[].postId").type(NUMBER).description("게시글 ID"),
					fieldWithPath("posts[].memberId").type(NUMBER).description("게시글 작성자 ID"),
					fieldWithPath("posts[].title").type(STRING).description("게시글 제목"),
					fieldWithPath("posts[].content").type(STRING).description("게시글 내용")
				)
			));
	}

	@DisplayName("게시글 수정 테스트")
	@Test
	void update() throws Exception {
		//given
		Long postId = 1L;
		Long memberId = 1L;

		PostReq postReq = new PostReq("제목 수정", "내용 수정");
		PostRes postRes = new PostRes(postId, memberId, post.getTitle(), post.getContent());

		when(postService.update(anyLong(), any(PostReq.class), anyString())).thenReturn(postRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/posts/{id}", postId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(postReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(postRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.postId").value(postId))
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.title").value(post.getTitle()))
			.andExpect(jsonPath("$.content").value(post.getContent()))
			.andDo(print())
			.andDo(document("post/post-update",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(parameterWithName("id").description("게시글 ID")),
				requestFields(
					fieldWithPath("title").description("수정할 게시글 제목"),
					fieldWithPath("content").description("수정할 게시글 내용")
				),
				responseFields(
					fieldWithPath("postId").type(NUMBER).description("게시글 ID"),
					fieldWithPath("memberId").type(NUMBER).description("게시글 작성자 ID"),
					fieldWithPath("title").type(STRING).description("수정된 게시글 제목"),
					fieldWithPath("content").type(STRING).description("수정된 게시글 내용")
				)
			));
	}

	@DisplayName("게시글 삭제 테스트")
	@Test
	void delete() throws Exception {
		//given
		Long postId = 1L;

		doNothing().when(postService).delete(anyLong(), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/posts/{id}", postId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andDo(document("post/post-delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(
					parameterWithName("id").description("게시글 ID")
				)
			));
	}
}
