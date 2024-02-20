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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.post.application.PostService;
import com.icebox.freshmate.domain.post.application.dto.request.PostReq;
import com.icebox.freshmate.domain.post.application.dto.response.PostRes;
import com.icebox.freshmate.domain.post.application.dto.response.PostsRes;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.post.domain.PostImage;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeType;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageType;
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
	private Recipe recipe;
	private Refrigerator refrigerator;
	private Storage storage;
	private Grocery grocery1;
	private Grocery grocery2;
	private RecipeGrocery recipeGrocery1;
	private RecipeGrocery recipeGrocery2;
	private PostImage postImage1;
	private PostImage postImage2;

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

		refrigerator = Refrigerator.builder()
			.name("우리 집 냉장고")
			.member(member)
			.build();

		storage = Storage.builder()
			.name("냉장실")
			.storageType(StorageType.FRIDGE)
			.refrigerator(refrigerator)
			.build();

		grocery1 = Grocery.builder()
			.storage(storage)
			.name("양배추")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().plusDays(7))
			.build();

		grocery2 = Grocery.builder()
			.storage(storage)
			.name("배추")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("2개")
			.description("김장용")
			.expirationDate(LocalDate.now().plusDays(7))
			.build();

		recipe = Recipe.builder()
			.writer(member)
			.owner(member)
			.recipeType(RecipeType.WRITTEN)
			.title("레시피")
			.content("내용")
			.build();

		recipeGrocery1 = RecipeGrocery.builder()
			.recipe(recipe)
			.grocery(grocery1)
			.groceryName(grocery1.getName())
			.groceryQuantity(grocery1.getQuantity())
			.build();

		recipeGrocery2 = RecipeGrocery.builder()
			.recipe(recipe)
			.grocery(grocery2)
			.groceryName(grocery2.getName())
			.groceryQuantity(grocery2.getQuantity())
			.build();

		recipe.addRecipeGrocery(recipeGrocery1);
		recipe.addRecipeGrocery(recipeGrocery2);

		post = Post.builder()
			.title("제목")
			.content("내용")
			.member(member)
			.recipe(recipe)
			.build();

		String imageFileName1 = "image1.jpg";
		String imagePath1 = "https://test-image-urls.com/image1.jpg";

		postImage1 = PostImage
			.builder()
			.post(post)
			.fileName(imageFileName1)
			.path(imagePath1)
			.build();

		String imageFileName2 = "image2.jpg";
		String imagePath2 = "https://test-image-urls.com/image2.jpg";

		postImage2 = PostImage
			.builder()
			.post(post)
			.fileName(imageFileName2)
			.path(imagePath2)
			.build();

		post.addPostImage(postImage1);
		post.addPostImage(postImage2);
	}

	@DisplayName("게시글 생성 테스트")
	@Test
	void create() throws Exception {
		//given
		Long postId = 1L;
		Long memberId = 1L;
		Long recipeId = 1L;
		Long recipeWriterId = memberId;
		Long recipeGrocery1Id = 1L;
		Long recipeGrocery2Id = 2L;
		Long grocery1Id = 1L;
		Long grocery2Id = 2L;

		PostReq postReq = new PostReq(post.getTitle(), post.getContent(), recipeId);

		MockMultipartFile file1 = new MockMultipartFile("imageFiles", "test1.jpg", "image/jpeg", "Spring Framework".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("imageFiles", "test2.jpg", "image/jpeg", "Spring Framework".getBytes());
		MockMultipartFile request = new MockMultipartFile("postReq", "postReq",
			"application/json",
			objectMapper.writeValueAsString(postReq).getBytes());

		ImageRes imageRes1 = new ImageRes(postImage1.getFileName(), postImage1.getPath());
		ImageRes imageRes2 = new ImageRes(postImage2.getFileName(), postImage2.getPath());

		RecipeGroceryRes recipeGroceryRes1 = new RecipeGroceryRes(recipeGrocery1Id, recipeId, recipe.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
		RecipeGroceryRes recipeGroceryRes2 = new RecipeGroceryRes(recipeGrocery2Id, recipeId, recipe.getTitle(), grocery2Id, grocery2.getName(), grocery2.getQuantity());

		PostRes postRes = new PostRes(postId, memberId, post.getTitle(), post.getContent(), recipeId, recipeWriterId, recipe.getWriter().getNickName(), recipe.getTitle(), recipe.getContent(), List.of(recipeGroceryRes1, recipeGroceryRes2), List.of(imageRes1, imageRes2));

		when(postService.create(any(PostReq.class), any(ImageUploadReq.class), anyString())).thenReturn(postRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/posts")
				.file(file1)
				.file(file2)
				.file(request)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(content().json(objectMapper.writeValueAsString(postRes)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.postId").value(postRes.postId()))
			.andExpect(jsonPath("$.memberId").value(postRes.memberId()))
			.andExpect(jsonPath("$.postTitle").value(postRes.postTitle()))
			.andExpect(jsonPath("$.postContent").value(postRes.postContent()))
			.andExpect(jsonPath("$.recipeId").value(postRes.postId()))
			.andExpect(jsonPath("$.recipeWriterId").value(postRes.recipeWriterId()))
			.andExpect(jsonPath("$.recipeWriterNickName").value(postRes.recipeWriterNickName()))
			.andExpect(jsonPath("$.recipeTitle").value(postRes.recipeTitle()))
			.andExpect(jsonPath("$.recipeContent").value(postRes.recipeContent()))
			.andExpect(jsonPath("$.recipeMaterials[0].recipeGroceryId").value(postRes.recipeMaterials().get(0).recipeGroceryId()))
			.andExpect(jsonPath("$.recipeMaterials[0].recipeId").value(postRes.recipeMaterials().get(0).recipeId()))
			.andExpect(jsonPath("$.recipeMaterials[0].recipeTitle").value(postRes.recipeMaterials().get(0).recipeTitle()))
			.andExpect(jsonPath("$.recipeMaterials[0].groceryId").value(postRes.recipeMaterials().get(0).groceryId()))
			.andExpect(jsonPath("$.recipeMaterials[0].groceryName").value(postRes.recipeMaterials().get(0).groceryName()))
			.andExpect(jsonPath("$.recipeMaterials[0].groceryQuantity").value(postRes.recipeMaterials().get(0).groceryQuantity()))
			.andExpect(jsonPath("$.images[0].fileName").value(post.getPostImages().get(0).getFileName()))
			.andExpect(jsonPath("$.images[0].path").value(post.getPostImages().get(0).getPath()))
			.andDo(print())
			.andDo(document("post/post-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestParts(
					partWithName("imageFiles").description("게시글 이미지들"),
					partWithName("postReq").description("게시글 등록 내용")
				),
				requestPartFields("postReq",
					fieldWithPath("title").description("게시글 제목"),
					fieldWithPath("content").description("게시글 내용"),
					fieldWithPath("recipeId").description("게시글에 공유하려는 레시피 ID")
				),
				responseFields(
					fieldWithPath("postId").type(NUMBER).description("게시글 ID"),
					fieldWithPath("memberId").type(NUMBER).description("게시글 작성자 ID"),
					fieldWithPath("postTitle").type(STRING).description("게시글 제목"),
					fieldWithPath("postContent").type(STRING).description("게시글 내용"),
					fieldWithPath("recipeId").type(NUMBER).description("게시글에 공유된 레시피 ID"),
					fieldWithPath("recipeWriterId").type(NUMBER).description("게시글에 공유된 레시피 작성자 ID"),
					fieldWithPath("recipeWriterNickName").type(STRING).description("게시글에 공유된 레시피 작성자 닉네임"),
					fieldWithPath("recipeTitle").type(STRING).description("게시글에 공유된 레시피 제목"),
					fieldWithPath("recipeContent").type(STRING).description("게시글에 공유된 레시피 내용"),
					fieldWithPath("recipeMaterials").type(ARRAY).description("게시글에 공유된 레시피 재료 배열"),
					fieldWithPath("recipeMaterials[].recipeGroceryId").type(NUMBER).description("게시글에 공유된 레시피와 식재료의 연관 관계 ID"),
					fieldWithPath("recipeMaterials[].recipeId").type(NUMBER).description("게시글에 공유된 레시피 ID"),
					fieldWithPath("recipeMaterials[].recipeTitle").type(STRING).description("게시글에 공유된 레시피 제목"),
					fieldWithPath("recipeMaterials[].groceryId").type(NUMBER).description("게시글에 공유된 레시피의 식재료 ID"),
					fieldWithPath("recipeMaterials[].groceryName").type(STRING).description("게시글에 공유된 레시피의 식재료 이름"),
					fieldWithPath("recipeMaterials[].groceryQuantity").type(STRING).description("게시글에 공유된 레시피의 식재료 수량"),
					fieldWithPath("images[].fileName").type(STRING).description("게시글 이미지 파일 이름"),
					fieldWithPath("images[].path").type(STRING).description("게시글 이미지 파일 경로")
				)
			));
	}

	@DisplayName("게시글 단건 조회 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long postId = 1L;
		Long memberId = 1L;
		Long recipeId = 1L;
		Long recipeWriterId = memberId;
		Long recipeGrocery1Id = 1L;
		Long recipeGrocery2Id = 2L;
		Long grocery1Id = 1L;
		Long grocery2Id = 2L;

		ImageRes imageRes1 = new ImageRes(postImage1.getFileName(), postImage1.getPath());
		ImageRes imageRes2 = new ImageRes(postImage2.getFileName(), postImage2.getPath());

		RecipeGroceryRes recipeGroceryRes1 = new RecipeGroceryRes(recipeGrocery1Id, recipeId, recipe.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
		RecipeGroceryRes recipeGroceryRes2 = new RecipeGroceryRes(recipeGrocery2Id, recipeId, recipe.getTitle(), grocery2Id, grocery2.getName(), grocery2.getQuantity());

		PostRes postRes = new PostRes(postId, memberId, post.getTitle(), post.getContent(), recipeId, recipeWriterId, recipe.getWriter().getNickName(), recipe.getTitle(), recipe.getContent(), List.of(recipeGroceryRes1, recipeGroceryRes2), List.of(imageRes1, imageRes2));

		when(postService.findById(anyLong())).thenReturn(postRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/posts/{id}", postId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(postRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.postId").value(postRes.postId()))
			.andExpect(jsonPath("$.memberId").value(postRes.memberId()))
			.andExpect(jsonPath("$.postTitle").value(postRes.postTitle()))
			.andExpect(jsonPath("$.postContent").value(postRes.postContent()))
			.andExpect(jsonPath("$.recipeId").value(postRes.postId()))
			.andExpect(jsonPath("$.recipeWriterId").value(postRes.recipeWriterId()))
			.andExpect(jsonPath("$.recipeWriterNickName").value(postRes.recipeWriterNickName()))
			.andExpect(jsonPath("$.recipeTitle").value(postRes.recipeTitle()))
			.andExpect(jsonPath("$.recipeContent").value(postRes.recipeContent()))
			.andExpect(jsonPath("$.recipeMaterials[0].recipeGroceryId").value(postRes.recipeMaterials().get(0).recipeGroceryId()))
			.andExpect(jsonPath("$.recipeMaterials[0].recipeId").value(postRes.recipeMaterials().get(0).recipeId()))
			.andExpect(jsonPath("$.recipeMaterials[0].recipeTitle").value(postRes.recipeMaterials().get(0).recipeTitle()))
			.andExpect(jsonPath("$.recipeMaterials[0].groceryId").value(postRes.recipeMaterials().get(0).groceryId()))
			.andExpect(jsonPath("$.recipeMaterials[0].groceryName").value(postRes.recipeMaterials().get(0).groceryName()))
			.andExpect(jsonPath("$.recipeMaterials[0].groceryQuantity").value(postRes.recipeMaterials().get(0).groceryQuantity()))
			.andExpect(jsonPath("$.images[0].fileName").value(post.getPostImages().get(0).getFileName()))
			.andExpect(jsonPath("$.images[0].path").value(post.getPostImages().get(0).getPath()))
			.andDo(print())
			.andDo(document("post/post-find-by-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("게시글 ID")),
				responseFields(
					fieldWithPath("postId").type(NUMBER).description("게시글 ID"),
					fieldWithPath("memberId").type(NUMBER).description("게시글 작성자 ID"),
					fieldWithPath("postTitle").type(STRING).description("게시글 제목"),
					fieldWithPath("postContent").type(STRING).description("게시글 내용"),
					fieldWithPath("recipeId").type(NUMBER).description("게시글에 공유된 레시피 ID"),
					fieldWithPath("recipeWriterId").type(NUMBER).description("게시글에 공유된 레시피 작성자 ID"),
					fieldWithPath("recipeWriterNickName").type(STRING).description("게시글에 공유된 레시피 작성자 닉네임"),
					fieldWithPath("recipeTitle").type(STRING).description("게시글에 공유된 레시피 제목"),
					fieldWithPath("recipeContent").type(STRING).description("게시글에 공유된 레시피 내용"),
					fieldWithPath("recipeMaterials").type(ARRAY).description("게시글에 공유된 레시피 재료 배열"),
					fieldWithPath("recipeMaterials[].recipeGroceryId").type(NUMBER).description("게시글에 공유된 레시피와 식재료의 연관 관계 ID"),
					fieldWithPath("recipeMaterials[].recipeId").type(NUMBER).description("게시글에 공유된 레시피 ID"),
					fieldWithPath("recipeMaterials[].recipeTitle").type(STRING).description("게시글에 공유된 레시피 제목"),
					fieldWithPath("recipeMaterials[].groceryId").type(NUMBER).description("게시글에 공유된 레시피의 식재료 ID"),
					fieldWithPath("recipeMaterials[].groceryName").type(STRING).description("게시글에 공유된 레시피의 식재료 이름"),
					fieldWithPath("recipeMaterials[].groceryQuantity").type(STRING).description("게시글에 공유된 레시피의 식재료 수량"),
					fieldWithPath("images[].fileName").type(STRING).description("게시글 이미지 파일 이름"),
					fieldWithPath("images[].path").type(STRING).description("게시글 이미지 파일 경로")
				)
			));
	}

//	@DisplayName("작성자별 모든 게시글 조회 테스트")
//	@Test
//	void findAllByMemberId() throws Exception {
//		//given
//		Long memberId = 1L;
//		Long recipeId = 1L;
//		Long recipeWriterId = memberId;
//		Long recipeGrocery1Id = 1L;
//		Long recipeGrocery2Id = 2L;
//		Long grocery1Id = 1L;
//		Long grocery2Id = 2L;
//
//		Post post2 = Post.builder()
//			.title("제목2")
//			.content("내용2")
//			.member(member)
//			.recipe(recipe)
//			.build();
//
//		ImageRes imageRes1 = new ImageRes(postImage1.getFileName(), postImage1.getPath());
//		ImageRes imageRes2 = new ImageRes(postImage2.getFileName(), postImage2.getPath());
//
//		RecipeGroceryRes recipeGroceryRes1 = new RecipeGroceryRes(recipeGrocery1Id, recipeId, recipe.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
//		RecipeGroceryRes recipeGroceryRes2 = new RecipeGroceryRes(recipeGrocery2Id, recipeId, recipe.getTitle(), grocery2Id, grocery2.getName(), grocery2.getQuantity());
//
//		PostRes postRes1 = new PostRes(1L, memberId, post.getTitle(), post.getContent(), recipeId, recipeWriterId, post.getRecipe().getWriter().getNickName(), post.getRecipe().getTitle(), post.getRecipe().getContent(), List.of(recipeGroceryRes1, recipeGroceryRes2), List.of(imageRes1, imageRes2));
//		PostRes postRes2 = new PostRes(2L, memberId, post2.getTitle(), post2.getContent(), recipeId, recipeWriterId, post2.getRecipe().getWriter().getNickName(), post2.getRecipe().getTitle(), post2.getRecipe().getContent(), List.of(recipeGroceryRes1, recipeGroceryRes2), List.of(imageRes1, imageRes2));
//
//		PostsRes postsRes = new PostsRes(List.of(postRes1, postRes2));
//
//		when(postService.findAll(anyLong())).thenReturn(postsRes);
//
//		//when
//		//then
//		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/posts?member-id=" + memberId)
//				.contentType(MediaType.APPLICATION_JSON)
//				.with(user(principalDetails))
//				.with(csrf().asHeader()))
//			.andExpect(content().json(objectMapper.writeValueAsString(postsRes)))
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.posts", hasSize(2)))
//			.andExpect(jsonPath("$.posts[0].postId").value(postRes1.postId()))
//			.andExpect(jsonPath("$.posts[0].memberId").value(postRes1.memberId()))
//			.andExpect(jsonPath("$.posts[0].postTitle").value(postRes1.postTitle()))
//			.andExpect(jsonPath("$.posts[0].postContent").value(postRes1.postContent()))
//			.andExpect(jsonPath("$.posts[0].recipeId").value(recipeId))
//			.andExpect(jsonPath("$.posts[0].recipeWriterId").value(recipeWriterId))
//			.andExpect(jsonPath("$.posts[0].recipeWriterNickName").value(postRes1.recipeWriterNickName()))
//			.andExpect(jsonPath("$.posts[0].recipeTitle").value(postRes1.recipeTitle()))
//			.andExpect(jsonPath("$.posts[0].recipeContent").value(postRes1.recipeContent()))
//			.andExpect(jsonPath("$.posts[0].recipeMaterials[0].recipeGroceryId").value(recipeGrocery1Id))
//			.andExpect(jsonPath("$.posts[0].recipeMaterials[0].recipeId").value(recipeId))
//			.andExpect(jsonPath("$.posts[0].recipeMaterials[0].recipeTitle").value(postRes1.recipeMaterials().get(0).recipeTitle()))
//			.andExpect(jsonPath("$.posts[0].recipeMaterials[0].groceryId").value(grocery1Id))
//			.andExpect(jsonPath("$.posts[0].recipeMaterials[0].groceryName").value(postRes1.recipeMaterials().get(0).groceryName()))
//			.andExpect(jsonPath("$.posts[0].recipeMaterials[0].groceryQuantity").value(postRes1.recipeMaterials().get(0).groceryQuantity()))
//			.andExpect(jsonPath("$.posts[0].images[0].fileName").value(postRes1.images().get(0).fileName()))
//			.andExpect(jsonPath("$.posts[0].images[0].path").value(postRes1.images().get(0).path()))
//			.andDo(print())
//			.andDo(document("post/post-find-all-by-member-id",
//				preprocessRequest(prettyPrint()),
//				preprocessResponse(prettyPrint()),
//				queryParameters(
//					parameterWithName("member-id").description("게시글 작성자 ID")
//				),
//				responseFields(
//					fieldWithPath("posts").type(ARRAY).description("게시글 배열"),
//					fieldWithPath("posts[].postId").type(NUMBER).description("게시글 ID"),
//					fieldWithPath("posts[].memberId").type(NUMBER).description("게시글 작성자 ID"),
//					fieldWithPath("posts[].postTitle").type(STRING).description("게시글 제목"),
//					fieldWithPath("posts[].postContent").type(STRING).description("게시글 내용"),
//					fieldWithPath("posts[].recipeId").type(NUMBER).description("게시글에 공유된 레시피 ID"),
//					fieldWithPath("posts[].recipeWriterId").type(NUMBER).description("게시글에 공유된 레시피 작성자 ID"),
//					fieldWithPath("posts[].recipeWriterNickName").type(STRING).description("게시글에 공유된 레시피 작성자 닉네임"),
//					fieldWithPath("posts[].recipeTitle").type(STRING).description("게시글에 공유된 레시피 제목"),
//					fieldWithPath("posts[].recipeContent").type(STRING).description("게시글에 공유된 레시피 내용"),
//					fieldWithPath("posts[].recipeMaterials").type(ARRAY).description("게시글에 공유된 레시피 재료 배열"),
//					fieldWithPath("posts[].recipeMaterials[].recipeGroceryId").type(NUMBER).description("게시글에 공유된 레시피와 식재료의 연관 관계 ID"),
//					fieldWithPath("posts[].recipeMaterials[].recipeId").type(NUMBER).description("게시글에 공유된 레시피 ID"),
//					fieldWithPath("posts[].recipeMaterials[].recipeTitle").type(STRING).description("게시글에 공유된 레시피 제목"),
//					fieldWithPath("posts[].recipeMaterials[].groceryId").type(NUMBER).description("게시글에 공유된 레시피의 식재료 ID"),
//					fieldWithPath("posts[].recipeMaterials[].groceryName").type(STRING).description("게시글에 공유된 레시피의 식재료 이름"),
//					fieldWithPath("posts[].recipeMaterials[].groceryQuantity").type(STRING).description("게시글에 공유된 레시피의 식재료 수량"),
//					fieldWithPath("posts[].images[].fileName").type(STRING).description("게시글 이미지 파일 이름"),
//					fieldWithPath("posts[].images[].path").type(STRING).description("게시글 이미지 파일 경로")
//				)
//			));
//	}

	@DisplayName("게시글 수정 테스트")
	@Test
	void update() throws Exception {
		//given
		Long postId = 1L;
		Long memberId = 1L;
		Long recipeId = 1L;
		Long recipeWriterId = memberId;
		Long recipeGrocery1Id = 1L;
		Long recipeGrocery2Id = 2L;
		Long grocery1Id = 1L;
		Long grocery2Id = 2L;

		ImageRes imageRes1 = new ImageRes(postImage1.getFileName(), postImage1.getPath());
		ImageRes imageRes2 = new ImageRes(postImage2.getFileName(), postImage2.getPath());

		RecipeGroceryRes recipeGroceryRes1 = new RecipeGroceryRes(recipeGrocery1Id, recipeId, recipe.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
		RecipeGroceryRes recipeGroceryRes2 = new RecipeGroceryRes(recipeGrocery2Id, recipeId, recipe.getTitle(), grocery2Id, grocery2.getName(), grocery2.getQuantity());

		PostReq postReq = new PostReq("제목 수정", "내용 수정", recipeId);
		PostRes postRes = new PostRes(postId, memberId, postReq.title(), postReq.content(), recipeId, recipeWriterId, recipe.getWriter().getNickName(), recipe.getTitle(), recipe.getContent(), List.of(recipeGroceryRes1, recipeGroceryRes2), List.of(imageRes1, imageRes2));

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
			.andExpect(jsonPath("$.postTitle").value(postReq.title()))
			.andExpect(jsonPath("$.postContent").value(postReq.content()))
			.andExpect(jsonPath("$.recipeId").value(recipeId))
			.andExpect(jsonPath("$.recipeWriterId").value(recipeWriterId))
			.andExpect(jsonPath("$.recipeWriterNickName").value(post.getRecipe().getWriter().getNickName()))
			.andExpect(jsonPath("$.recipeTitle").value(post.getRecipe().getTitle()))
			.andExpect(jsonPath("$.recipeContent").value(post.getRecipe().getContent()))
			.andExpect(jsonPath("$.recipeMaterials[0].recipeGroceryId").value(recipeGrocery1Id))
			.andExpect(jsonPath("$.recipeMaterials[0].recipeId").value(recipeId))
			.andExpect(jsonPath("$.recipeMaterials[0].recipeTitle").value(post.getRecipe().getRecipeGroceries().get(0).getRecipe().getTitle()))
			.andExpect(jsonPath("$.recipeMaterials[0].groceryId").value(grocery1Id))
			.andExpect(jsonPath("$.recipeMaterials[0].groceryName").value(post.getRecipe().getRecipeGroceries().get(0).getGroceryName()))
			.andExpect(jsonPath("$.recipeMaterials[0].groceryQuantity").value(post.getRecipe().getRecipeGroceries().get(0).getGroceryQuantity()))
			.andExpect(jsonPath("$.images[0].fileName").value(post.getPostImages().get(0).getFileName()))
			.andExpect(jsonPath("$.images[0].path").value(post.getPostImages().get(0).getPath()))
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
					fieldWithPath("content").description("수정할 게시글 내용"),
					fieldWithPath("recipeId").description("수정할 레시피 ID")
				),
				responseFields(
					fieldWithPath("postId").type(NUMBER).description("게시글 ID"),
					fieldWithPath("memberId").type(NUMBER).description("게시글 작성자 ID"),
					fieldWithPath("postTitle").type(STRING).description("수정된 게시글 제목"),
					fieldWithPath("postContent").type(STRING).description("수정된 게시글 내용"),
					fieldWithPath("recipeId").type(NUMBER).description("게시글에 공유된 레시피 ID (레시피 내용 수정 가능)"),
					fieldWithPath("recipeWriterId").type(NUMBER).description("게시글에 공유된 레시피 작성자 ID"),
					fieldWithPath("recipeWriterNickName").type(STRING).description("게시글에 공유된 레시피 작성자 닉네임"),
					fieldWithPath("recipeTitle").type(STRING).description("게시글에 공유된 레시피 제목"),
					fieldWithPath("recipeContent").type(STRING).description("게시글에 공유된 레시피 내용"),
					fieldWithPath("recipeMaterials").type(ARRAY).description("게시글에 공유된 레시피 재료 배열"),
					fieldWithPath("recipeMaterials[].recipeGroceryId").type(NUMBER).description("게시글에 공유된 레시피와 식재료의 연관 관계 ID"),
					fieldWithPath("recipeMaterials[].recipeId").type(NUMBER).description("게시글에 공유된 레시피 ID"),
					fieldWithPath("recipeMaterials[].recipeTitle").type(STRING).description("게시글에 공유된 레시피 제목"),
					fieldWithPath("recipeMaterials[].groceryId").type(NUMBER).description("게시글에 공유된 레시피의 식재료 ID"),
					fieldWithPath("recipeMaterials[].groceryName").type(STRING).description("게시글에 공유된 레시피의 식재료 이름"),
					fieldWithPath("recipeMaterials[].groceryQuantity").type(STRING).description("게시글에 공유된 레시피의 식재료 수량"),
					fieldWithPath("images[].fileName").type(STRING).description("게시글 이미지 파일 이름"),
					fieldWithPath("images[].path").type(STRING).description("게시글 이미지 파일 경로")
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
