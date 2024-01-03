package com.icebox.freshmate.domain.recipe.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
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
import com.icebox.freshmate.domain.recipe.application.RecipeService;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeReq;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipeRes;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipesRes;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeType;
import com.icebox.freshmate.global.TestPrincipalDetailsService;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;

@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(value = {RecipeController.class})
@AutoConfigureRestDocs
class RecipeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private RecipeService recipeService;

	private final TestPrincipalDetailsService testUserDetailsService = new TestPrincipalDetailsService();
	private PrincipalDetails principalDetails;

	private Member member1;
	private Member member2;
	private Recipe recipe1;
	private Recipe recipe2;
	private Recipe recipe3;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(documentationConfiguration(restDocumentationContextProvider))
			.apply(springSecurity())
			.alwaysDo(print()).build();

		principalDetails = (PrincipalDetails) testUserDetailsService.loadUserByUsername(TestPrincipalDetailsService.USERNAME);

		member1 = Member.builder()
			.realName("성이름")
			.username("aaaa1111")
			.password("aaaa1111!")
			.nickName("닉네임닉네임")
			.role(Role.USER)
			.build();

		member2 = Member.builder()
			.realName("김이름")
			.username("aaaa2222")
			.password("aaaa2222!")
			.nickName("닉네임닉네임2")
			.role(Role.USER)
			.build();

		recipe1 = Recipe.builder()
			.writer(member1)
			.owner(member1)
			.recipeType(RecipeType.WRITTEN)
			.title("레시피1")
			.material("재료")
			.content("내용")
			.build();

		recipe2 = Recipe.builder()
			.writer(member2)
			.owner(member1)
			.recipeType(RecipeType.SCRAPED)
			.title("레시피2")
			.material("재료")
			.content("내용")
			.build();

		recipe3 = Recipe.builder()
			.writer(member1)
			.owner(member2)
			.recipeType(RecipeType.SCRAPED)
			.title("레시피3")
			.material("재료")
			.content("내용")
			.build();
	}

	@DisplayName("레시피 생성 테스트")
	@Test
	void create() throws Exception {
		//given
		Long recipe1Id = 1L;
		Long member1Id = 1L;

		RecipeReq recipeReq = new RecipeReq(recipe1.getTitle(), recipe1.getMaterial(), recipe1.getContent());
		RecipeRes recipeRes = new RecipeRes(recipe1Id, member1Id, member1.getNickName(), member1Id, member1.getNickName(), RecipeType.WRITTEN.name(), recipe1Id, recipe1.getTitle(), recipe1.getMaterial(), recipe1.getContent());

		when(recipeService.create(any(RecipeReq.class), any(String.class))).thenReturn(recipeRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/recipes")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(recipeReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(recipeRes)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.recipeId").value(recipeRes.recipeId()))
			.andExpect(jsonPath("$.writerId").value(recipeRes.writerId()))
			.andExpect(jsonPath("$.writerNickName").value(recipeRes.writerNickName()))
			.andExpect(jsonPath("$.ownerId").value(recipeRes.ownerId()))
			.andExpect(jsonPath("$.ownerNickName").value(recipeRes.ownerNickName()))
			.andExpect(jsonPath("$.recipeType").value(recipeRes.recipeType()))
			.andExpect(jsonPath("$.originalRecipeId").value(recipeRes.originalRecipeId()))
			.andExpect(jsonPath("$.title").value(recipeRes.title()))
			.andExpect(jsonPath("$.material").value(recipeRes.material()))
			.andExpect(jsonPath("$.content").value(recipeRes.content()))
			.andDo(print())
			.andDo(document("recipe/recipe-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("title").description("레시피 제목"),
					fieldWithPath("material").description("레시피 재료"),
					fieldWithPath("content").description("레시피 내용")
				),
				responseFields(
					fieldWithPath("recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("writerId").type(NUMBER).description("레시피 작성자 ID"),
					fieldWithPath("writerNickName").type(STRING).description("레시피 작성자 닉네임"),
					fieldWithPath("ownerId").type(NUMBER).description("레시피 소유자 ID"),
					fieldWithPath("ownerNickName").type(STRING).description("레시피 소유자 닉네임"),
					fieldWithPath("recipeType").type(STRING).description("레시피 타입"),
					fieldWithPath("originalRecipeId").type(NUMBER).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("title").type(STRING).description("레시피 제목"),
					fieldWithPath("material").type(STRING).description("레시피 재료"),
					fieldWithPath("content").type(STRING).description("레시피 내용")
				)
			));
	}

	@DisplayName("레시피 스크랩 성공 테스트")
	@Test
	void scrap() throws Exception {
		//given
		Long scrapedRecipeId = 3L;
		Long originalRecipeId = 2L;
		Long writerId = 2L;
		Long ownerId = 1L;

		RecipeRes recipeRes = new RecipeRes(scrapedRecipeId, writerId, member2.getNickName(), ownerId, member1.getNickName(), RecipeType.SCRAPED.name(), originalRecipeId, recipe1.getTitle(), recipe1.getMaterial(), recipe1.getContent());

		when(recipeService.scrap(anyLong(), any(String.class))).thenReturn(recipeRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/recipes/scrap?recipe-id=" + originalRecipeId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(recipeRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.recipeId").value(recipeRes.recipeId()))
			.andExpect(jsonPath("$.writerId").value(recipeRes.writerId()))
			.andExpect(jsonPath("$.writerNickName").value(recipeRes.writerNickName()))
			.andExpect(jsonPath("$.ownerId").value(recipeRes.ownerId()))
			.andExpect(jsonPath("$.ownerNickName").value(recipeRes.ownerNickName()))
			.andExpect(jsonPath("$.recipeType").value(recipeRes.recipeType()))
			.andExpect(jsonPath("$.originalRecipeId").value(recipeRes.originalRecipeId()))
			.andExpect(jsonPath("$.title").value(recipeRes.title()))
			.andExpect(jsonPath("$.material").value(recipeRes.material()))
			.andExpect(jsonPath("$.content").value(recipeRes.content()))
			.andDo(print())
			.andDo(document("recipe/recipe-scrap",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				queryParameters(
					parameterWithName("recipe-id").description("스크랩 할 레시피 ID")
				),
				responseFields(
					fieldWithPath("recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("writerId").type(NUMBER).description("레시피 작성자 ID"),
					fieldWithPath("writerNickName").type(STRING).description("레시피 작성자 닉네임"),
					fieldWithPath("ownerId").type(NUMBER).description("레시피 소유자 ID"),
					fieldWithPath("ownerNickName").type(STRING).description("레시피 소유자 닉네임"),
					fieldWithPath("recipeType").type(STRING).description("레시피 타입"),
					fieldWithPath("originalRecipeId").type(NUMBER).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("title").type(STRING).description("레시피 제목"),
					fieldWithPath("material").type(STRING).description("레시피 재료"),
					fieldWithPath("content").type(STRING).description("레시피 내용")
				)
			));
	}

	@DisplayName("레시피 스크랩 실패 테스트 - 본인이 작성한 레시피는 스크랩 불가")
	@Test
	void scrapFailure_invalidScrapAttemptToOwnRecipe() throws Exception {
		//given
		Long originalRecipeId = 2L;

		doThrow(new BusinessException(ErrorCode.INVALID_SCRAP_ATTEMPT_TO_OWN_RECIPE)).when(
			recipeService).scrap(anyLong(), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/recipes/scrap?recipe-id=" + originalRecipeId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("RC002"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("본인이 작성한 레시피는 스크랩할 수 없습니다."))
			.andDo(print())
			.andDo(document("recipe/recipe-failure-invalid-scrap-attempt-to-own-recipe",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				queryParameters(
					parameterWithName("recipe-id").description("스크랩 할 레시피 ID")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("레시피 단건 조회 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long recipe1Id = 1L;
		Long member1Id = 1L;

		RecipeRes recipeRes = new RecipeRes(recipe1Id, member1Id, member1.getNickName(), member1Id, member1.getNickName(), RecipeType.WRITTEN.name(), recipe1Id, recipe1.getTitle(), recipe1.getMaterial(), recipe1.getContent());

		when(recipeService.findById(anyLong())).thenReturn(recipeRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/recipes/{id}", recipe1Id)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(recipeRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.recipeId").value(recipeRes.recipeId()))
			.andExpect(jsonPath("$.writerId").value(recipeRes.writerId()))
			.andExpect(jsonPath("$.writerNickName").value(recipeRes.writerNickName()))
			.andExpect(jsonPath("$.ownerId").value(recipeRes.ownerId()))
			.andExpect(jsonPath("$.ownerNickName").value(recipeRes.ownerNickName()))
			.andExpect(jsonPath("$.recipeType").value(recipeRes.recipeType()))
			.andExpect(jsonPath("$.originalRecipeId").value(recipeRes.originalRecipeId()))
			.andExpect(jsonPath("$.title").value(recipeRes.title()))
			.andExpect(jsonPath("$.material").value(recipeRes.material()))
			.andExpect(jsonPath("$.content").value(recipeRes.content()))
			.andDo(print())
			.andDo(document("recipe/recipe-find-by-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(parameterWithName("id").description("레시피 ID")),
				responseFields(
					fieldWithPath("recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("writerId").type(NUMBER).description("레시피 작성자 ID"),
					fieldWithPath("writerNickName").type(STRING).description("레시피 작성자 닉네임"),
					fieldWithPath("ownerId").type(NUMBER).description("레시피 소유자 ID"),
					fieldWithPath("ownerNickName").type(STRING).description("레시피 소유자 닉네임"),
					fieldWithPath("recipeType").type(STRING).description("레시피 타입"),
					fieldWithPath("originalRecipeId").type(NUMBER).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("title").type(STRING).description("레시피 제목"),
					fieldWithPath("material").type(STRING).description("레시피 재료"),
					fieldWithPath("content").type(STRING).description("레시피 내용")
				)
			));
	}

	@DisplayName("사용자가 작성한 모든 레시피 조회 테스트")
	@Test
	void findAllByWriterId() throws Exception {
		//given
		Long member1Id = 1L;
		Long member2Id = 2L;
		Long recipe1Id = 1L;
		Long recipe3Id = 3L;

		RecipeRes recipeRes1 = new RecipeRes(recipe1Id, member1Id, member1.getNickName(), member1Id, member1.getNickName(), RecipeType.WRITTEN.name(), recipe1Id, recipe1.getTitle(), recipe1.getMaterial(), recipe1.getContent());
		RecipeRes recipeRes2 = new RecipeRes(recipe3Id, member1Id, member1.getNickName(), member2Id, member2.getNickName(), RecipeType.SCRAPED.name(), recipe3Id, recipe3.getTitle(), recipe3.getMaterial(), recipe3.getContent());

		RecipesRes recipesRes = new RecipesRes(List.of(recipeRes1, recipeRes2));

		when(recipeService.findAllByWriterId(member1.getUsername())).thenReturn(recipesRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/recipes/writers")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(recipesRes)))
			.andExpect(jsonPath("$.recipes", hasSize(2)))
			.andExpect(jsonPath("$.recipes[0].recipeId").value(recipeRes1.recipeId()))
			.andExpect(jsonPath("$.recipes[0].writerId").value(recipeRes1.writerId()))
			.andExpect(jsonPath("$.recipes[0].writerNickName").value(recipeRes1.writerNickName()))
			.andExpect(jsonPath("$.recipes[0].ownerId").value(recipeRes1.ownerId()))
			.andExpect(jsonPath("$.recipes[0].ownerNickName").value(recipeRes1.ownerNickName()))
			.andExpect(jsonPath("$.recipes[0].recipeType").value(recipeRes1.recipeType()))
			.andExpect(jsonPath("$.recipes[0].originalRecipeId").value(recipeRes1.originalRecipeId()))
			.andExpect(jsonPath("$.recipes[0].title").value(recipeRes1.title()))
			.andExpect(jsonPath("$.recipes[0].material").value(recipeRes1.material()))
			.andExpect(jsonPath("$.recipes[0].content").value(recipeRes1.content()))
			.andDo(print())
			.andDo(document("recipe/recipe-find-all-by-writer-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				responseFields(
					fieldWithPath("recipes").type(ARRAY).description("레시피 배열"),
					fieldWithPath("recipes[].recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("recipes[].writerId").type(NUMBER).description("레시피 작성자 ID"),
					fieldWithPath("recipes[].writerNickName").type(STRING).description("레시피 작성자 닉네임"),
					fieldWithPath("recipes[].ownerId").type(NUMBER).description("레시피 소유자 ID"),
					fieldWithPath("recipes[].ownerNickName").type(STRING).description("레시피 소유자 닉네임"),
					fieldWithPath("recipes[].recipeType").type(STRING).description("레시피 타입"),
					fieldWithPath("recipes[].originalRecipeId").type(NUMBER).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("recipes[].title").type(STRING).description("레시피 제목"),
					fieldWithPath("recipes[].material").type(STRING).description("레시피 재료"),
					fieldWithPath("recipes[].content").type(STRING).description("레시피 내용")
				)
			));
	}

	@DisplayName("사용자가 스크랩한(소유한) 모든 레시피 조회 테스트")
	@Test
	void findAllByOwnerId() throws Exception {
		//given
		Long member1Id = 1L;
		Long member2Id = 2L;
		Long recipe1Id = 1L;
		Long recipe2Id = 2L;

		RecipeRes recipeRes1 = new RecipeRes(recipe1Id, member1Id, member1.getNickName(), member1Id, member1.getNickName(), RecipeType.WRITTEN.name(), recipe1Id, recipe1.getTitle(), recipe1.getMaterial(), recipe1.getContent());
		RecipeRes recipeRes2 = new RecipeRes(recipe2Id, member2Id, member2.getNickName(), member1Id, member1.getNickName(), RecipeType.WRITTEN.name(), recipe2Id, recipe2.getTitle(), recipe2.getMaterial(), recipe2.getContent());

		RecipesRes recipesRes = new RecipesRes(List.of(recipeRes1, recipeRes2));

		when(recipeService.findAllByOwnerId(member1.getUsername())).thenReturn(recipesRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/recipes/owners")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(recipesRes)))
			.andExpect(jsonPath("$.recipes", hasSize(2)))
			.andExpect(jsonPath("$.recipes[0].recipeId").value(recipeRes1.recipeId()))
			.andExpect(jsonPath("$.recipes[0].writerId").value(recipeRes1.writerId()))
			.andExpect(jsonPath("$.recipes[0].writerNickName").value(recipeRes1.writerNickName()))
			.andExpect(jsonPath("$.recipes[0].ownerId").value(recipeRes1.ownerId()))
			.andExpect(jsonPath("$.recipes[0].ownerNickName").value(recipeRes1.ownerNickName()))
			.andExpect(jsonPath("$.recipes[0].recipeType").value(recipeRes1.recipeType()))
			.andExpect(jsonPath("$.recipes[0].originalRecipeId").value(recipeRes1.originalRecipeId()))
			.andExpect(jsonPath("$.recipes[0].title").value(recipeRes1.title()))
			.andExpect(jsonPath("$.recipes[0].material").value(recipeRes1.material()))
			.andExpect(jsonPath("$.recipes[0].content").value(recipeRes1.content()))
			.andDo(print())
			.andDo(document("recipe/recipe-find-all-by-owner-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				responseFields(
					fieldWithPath("recipes").type(ARRAY).description("레시피 배열"),
					fieldWithPath("recipes[].recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("recipes[].writerId").type(NUMBER).description("레시피 작성자 ID"),
					fieldWithPath("recipes[].writerNickName").type(STRING).description("레시피 작성자 닉네임"),
					fieldWithPath("recipes[].ownerId").type(NUMBER).description("레시피 소유자 ID"),
					fieldWithPath("recipes[].ownerNickName").type(STRING).description("레시피 소유자 닉네임"),
					fieldWithPath("recipes[].recipeType").type(STRING).description("레시피 타입"),
					fieldWithPath("recipes[].originalRecipeId").type(NUMBER).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("recipes[].title").type(STRING).description("레시피 제목"),
					fieldWithPath("recipes[].material").type(STRING).description("레시피 재료"),
					fieldWithPath("recipes[].content").type(STRING).description("레시피 내용")
				)
			));
	}

	@DisplayName("사용자가 작성하고 스크랩한(소유한) 모든 레시피 조회 테스트")
	@Test
	void findAllByMemberId() throws Exception {
		//given
		Long member1Id = 1L;
		Long member2Id = 2L;
		Long recipe1Id = 1L;
		Long recipe2Id = 2L;

		RecipeRes recipeRes1 = new RecipeRes(recipe1Id, member1Id, member1.getNickName(), member1Id, member1.getNickName(), RecipeType.WRITTEN.name(), recipe1Id, recipe1.getTitle(), recipe1.getMaterial(), recipe1.getContent());
		RecipeRes recipeRes2 = new RecipeRes(recipe2Id, member2Id, member2.getNickName(), member1Id, member1.getNickName(), RecipeType.WRITTEN.name(), recipe2Id, recipe2.getTitle(), recipe2.getMaterial(), recipe2.getContent());

		RecipesRes recipesRes = new RecipesRes(List.of(recipeRes1, recipeRes2));

		when(recipeService.findAllByMemberId(member1.getUsername())).thenReturn(recipesRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/recipes/members")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(recipesRes)))
			.andExpect(jsonPath("$.recipes", hasSize(2)))
			.andExpect(jsonPath("$.recipes[0].recipeId").value(recipeRes1.recipeId()))
			.andExpect(jsonPath("$.recipes[0].writerId").value(recipeRes1.writerId()))
			.andExpect(jsonPath("$.recipes[0].writerNickName").value(recipeRes1.writerNickName()))
			.andExpect(jsonPath("$.recipes[0].ownerId").value(recipeRes1.ownerId()))
			.andExpect(jsonPath("$.recipes[0].ownerNickName").value(recipeRes1.ownerNickName()))
			.andExpect(jsonPath("$.recipes[0].recipeType").value(recipeRes1.recipeType()))
			.andExpect(jsonPath("$.recipes[0].originalRecipeId").value(recipeRes1.originalRecipeId()))
			.andExpect(jsonPath("$.recipes[0].title").value(recipeRes1.title()))
			.andExpect(jsonPath("$.recipes[0].material").value(recipeRes1.material()))
			.andExpect(jsonPath("$.recipes[0].content").value(recipeRes1.content()))
			.andDo(print())
			.andDo(document("recipe/recipe-find-all-by-owner-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				responseFields(
					fieldWithPath("recipes").type(ARRAY).description("레시피 배열"),
					fieldWithPath("recipes[].recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("recipes[].writerId").type(NUMBER).description("레시피 작성자 ID"),
					fieldWithPath("recipes[].writerNickName").type(STRING).description("레시피 작성자 닉네임"),
					fieldWithPath("recipes[].ownerId").type(NUMBER).description("레시피 소유자 ID"),
					fieldWithPath("recipes[].ownerNickName").type(STRING).description("레시피 소유자 닉네임"),
					fieldWithPath("recipes[].recipeType").type(STRING).description("레시피 타입"),
					fieldWithPath("recipes[].originalRecipeId").type(NUMBER).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("recipes[].title").type(STRING).description("레시피 제목"),
					fieldWithPath("recipes[].material").type(STRING).description("레시피 재료"),
					fieldWithPath("recipes[].content").type(STRING).description("레시피 내용")
				)
			));
	}

	@DisplayName("사용자가 작성한 레시피 수정 성공 테스트")
	@Test
	void update() throws Exception {
		//given
		Long recipe1Id = 1L;
		Long member1Id = 1L;

		RecipeReq recipeReq = new RecipeReq("제목수정", "재료수정", "내용수정");
		RecipeRes recipeRes = new RecipeRes(recipe1Id, member1Id, member1.getNickName(), member1Id, member1.getNickName(), RecipeType.WRITTEN.name(), recipe1Id, recipeReq.title(), recipeReq.material(), recipeReq.content());

		when(recipeService.update(anyLong(), any(RecipeReq.class), any(String.class))).thenReturn(recipeRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/recipes/{id}", recipe1Id)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(recipeReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(recipeRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.recipeId").value(recipeRes.recipeId()))
			.andExpect(jsonPath("$.writerId").value(recipeRes.writerId()))
			.andExpect(jsonPath("$.writerNickName").value(recipeRes.writerNickName()))
			.andExpect(jsonPath("$.ownerId").value(recipeRes.ownerId()))
			.andExpect(jsonPath("$.ownerNickName").value(recipeRes.ownerNickName()))
			.andExpect(jsonPath("$.recipeType").value(recipeRes.recipeType()))
			.andExpect(jsonPath("$.originalRecipeId").value(recipeRes.originalRecipeId()))
			.andExpect(jsonPath("$.title").value(recipeRes.title()))
			.andExpect(jsonPath("$.material").value(recipeRes.material()))
			.andExpect(jsonPath("$.content").value(recipeRes.content()))
			.andDo(print())
			.andDo(document("recipe/recipe-update",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(parameterWithName("id").description("레시피 ID")),
				requestFields(
					fieldWithPath("title").description("수정할 레시피 제목"),
					fieldWithPath("material").description("수정할 레시피 재료"),
					fieldWithPath("content").description("수정할 레시피 내용")
				),
				responseFields(
					fieldWithPath("recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("writerId").type(NUMBER).description("레시피 작성자 ID"),
					fieldWithPath("writerNickName").type(STRING).description("레시피 작성자 닉네임"),
					fieldWithPath("ownerId").type(NUMBER).description("레시피 소유자 ID"),
					fieldWithPath("ownerNickName").type(STRING).description("레시피 소유자 닉네임"),
					fieldWithPath("recipeType").type(STRING).description("레시피 타입"),
					fieldWithPath("originalRecipeId").type(NUMBER).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("title").type(STRING).description("수정된 레시피 제목"),
					fieldWithPath("material").type(STRING).description("수정된 레시피 재료"),
					fieldWithPath("content").type(STRING).description("수정된 레시피 내용")
				)
			));
	}

	@DisplayName("사용자가 작성한 레시피 수정 실패 테스트 - 스크랩한 레시피는 수정 불가")
	@Test
	void updateFailure_invalidUpdateAttemptToScrapedRecipe() throws Exception {
		//given
		Long recipe1Id = 1L;

		RecipeReq recipeReq = new RecipeReq("제목수정", "재료수정", "내용수정");

		doThrow(new BusinessException(ErrorCode.INVALID_UPDATE_ATTEMPT_TO_SCRAPED_RECIPE)).when(
			recipeService).update(anyLong(), any(RecipeReq.class), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/recipes/{id}", recipe1Id)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(recipeReq)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("RC003"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("스크랩한 레시피는 수정할 수 없습니다."))
			.andDo(print())
			.andDo(document("recipe/recipe-update-failure-invalid-update-attempt-to-scraped-recipe",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(parameterWithName("id").description("레시피 ID")),
				requestFields(
					fieldWithPath("title").description("수정할 레시피 제목"),
					fieldWithPath("material").description("수정할 레시피 재료"),
					fieldWithPath("content").description("수정할 레시피 내용")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("레시피 삭제 테스트")
	@Test
	void delete() throws Exception {
		//given
		Long recipe1Id = 1L;

		doNothing().when(recipeService).delete(anyLong(), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/recipes/{id}", recipe1Id)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andDo(document("recipe/recipe-delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(
					parameterWithName("id").description("레시피 ID")
				)
			));
	}
}
