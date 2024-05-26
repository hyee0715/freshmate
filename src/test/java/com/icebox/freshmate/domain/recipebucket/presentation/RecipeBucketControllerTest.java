package com.icebox.freshmate.domain.recipebucket.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeType;
import com.icebox.freshmate.domain.recipebucket.application.RecipeBucketService;
import com.icebox.freshmate.domain.recipebucket.application.dto.request.RecipeBucketReq;
import com.icebox.freshmate.domain.recipebucket.application.dto.response.RecipeBucketRes;
import com.icebox.freshmate.domain.recipebucket.application.dto.response.RecipeBucketsRes;
import com.icebox.freshmate.domain.recipebucket.domain.RecipeBucket;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageType;
import com.icebox.freshmate.global.TestPrincipalDetailsService;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(value = {RecipeBucketController.class})
@AutoConfigureRestDocs
class RecipeBucketControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private RecipeBucketService recipeBucketService;

	private final TestPrincipalDetailsService testUserDetailsService = new TestPrincipalDetailsService();
	private PrincipalDetails principalDetails;

	private Member member;
	private Recipe recipe;
	private Refrigerator refrigerator;
	private Storage storage;
	private Grocery grocery1;
	private Grocery grocery2;
	private RecipeGrocery recipeGrocery1;
	private RecipeGrocery recipeGrocery2;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(documentationConfiguration(restDocumentationContextProvider))
			.apply(springSecurity())
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print()).build();

		principalDetails = (PrincipalDetails) testUserDetailsService.loadUserByUsername(TestPrincipalDetailsService.USERNAME);

		member = Member.builder()
			.realName("성이름")
			.username("aaaa1111")
			.password("aaaa1111!")
			.nickName("닉네임")
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
			.quantity("1개")
			.description("필수 식재료")
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
	}

	@DisplayName("즐겨 찾는 레시피 생성 성공 테스트")
	@Test
	void create() throws Exception {
		//given
		Long recipeId = 1L;
		Long recipeBucketId = 1L;
		Long writerId = 1L;
		Long originalRecipeId = 1L;
		Long memberId = 1L;
		Long grocery1Id = 1L;
		Long grocery2Id = 2L;
		Long recipeGrocery1Id = 1L;
		Long recipeGrocery2Id = 2L;

		LocalDateTime createdAt = LocalDateTime.now();

		RecipeBucketReq recipeBucketReq = new RecipeBucketReq(recipeId);

		RecipeGroceryRes recipeGroceryRes1 = new RecipeGroceryRes(recipeGrocery1Id, recipeId, recipe.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
		RecipeGroceryRes recipeGroceryRes2 = new RecipeGroceryRes(recipeGrocery2Id, recipeId, recipe.getTitle(), grocery2Id, grocery2.getName(), grocery2.getQuantity());

		RecipeBucketRes recipeBucketRes = new RecipeBucketRes(recipeBucketId, recipeId, writerId, member.getNickName(), recipe.getRecipeType().name(), originalRecipeId, recipe.getTitle(), recipe.getContent(), List.of(recipeGroceryRes1, recipeGroceryRes2), memberId, member.getNickName(), createdAt);

		when(recipeBucketService.create(any(RecipeBucketReq.class), anyString())).thenReturn(recipeBucketRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/recipe-buckets")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(recipeBucketReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(recipeBucketRes)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.recipeBucketId").value(recipeBucketRes.recipeBucketId()))
			.andExpect(jsonPath("$.recipeId").value(recipeBucketRes.recipeId()))
			.andExpect(jsonPath("$.writerId").value(recipeBucketRes.writerId()))
			.andExpect(jsonPath("$.writerNickName").value(recipeBucketRes.writerNickName()))
			.andExpect(jsonPath("$.recipeType").value(recipeBucketRes.recipeType()))
			.andExpect(jsonPath("$.originalRecipeId").value(recipeBucketRes.originalRecipeId()))
			.andExpect(jsonPath("$.recipeTitle").value(recipeBucketRes.recipeTitle()))
			.andExpect(jsonPath("$.recipeContent").value(recipeBucketRes.recipeContent()))
			.andExpect(jsonPath("$.materials[0].recipeGroceryId").value(recipeBucketRes.materials().get(0).recipeGroceryId()))
			.andExpect(jsonPath("$.materials[0].recipeId").value(recipeBucketRes.materials().get(0).recipeId()))
			.andExpect(jsonPath("$.materials[0].recipeTitle").value(recipeBucketRes.materials().get(0).recipeTitle()))
			.andExpect(jsonPath("$.materials[0].groceryId").value(recipeBucketRes.materials().get(0).groceryId()))
			.andExpect(jsonPath("$.materials[0].groceryName").value(recipeBucketRes.materials().get(0).groceryName()))
			.andExpect(jsonPath("$.materials[0].groceryQuantity").value(recipeBucketRes.materials().get(0).groceryQuantity()))
			.andExpect(jsonPath("$.memberId").value(recipeBucketRes.memberId()))
			.andExpect(jsonPath("$.memberNickName").value(recipeBucketRes.memberNickName()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(recipeBucketRes.createdAt())))
			.andDo(print())
			.andDo(document("recipe-bucket/recipe-bucket-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("recipeId").description("레시피 ID")
				),
				responseFields(
					fieldWithPath("recipeBucketId").type(NUMBER).description("즐겨 찾는 레시피 ID"),
					fieldWithPath("recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("writerId").type(NUMBER).description("레시피 작성자 ID"),
					fieldWithPath("writerNickName").type(STRING).description("레시피 작성자 닉네임"),
					fieldWithPath("recipeType").type(STRING).description("레시피 타입"),
					fieldWithPath("originalRecipeId").type(NUMBER).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("recipeTitle").type(STRING).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("recipeContent").type(STRING).description("레시피 내용"),
					fieldWithPath("materials[].recipeGroceryId").type(NUMBER).description("레시피 식재료 연관 관계 ID"),
					fieldWithPath("materials[].recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("materials[].recipeTitle").type(STRING).description("레시피 제목"),
					fieldWithPath("materials[].groceryId").type(NUMBER).description("식재료 ID"),
					fieldWithPath("materials[].groceryName").type(STRING).description("식재료 이름"),
					fieldWithPath("materials[].groceryQuantity").type(STRING).description("식재료 수량"),
					fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("memberNickName").type(STRING).description("회원 닉네임"),
					fieldWithPath("createdAt").type(STRING).description("즐겨 찾는 레시피 등록 시점")
				)
			));
	}

	@DisplayName("즐겨 찾는 레시피 생성 실패 테스트 - 이미 즐겨 찾는 레시피로 등록되어 있는 경우")
	@Test
	void createFailure_duplicatedRecipeBucket() throws Exception {
		//given
		Long recipeId = 1L;
		RecipeBucketReq recipeBucketReq = new RecipeBucketReq(recipeId);

		doThrow(new BusinessException(ErrorCode.DUPLICATED_RECIPE_BUCKET)).when(
			recipeBucketService).create(eq(recipeBucketReq), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/recipe-buckets")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(recipeBucketReq)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("RB002"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("해당 레시피는 이미 즐겨 찾는 레시피로 등록되어 있습니다."))
			.andDo(print())
			.andDo(document("recipe-bucket/recipe-bucket-create-failure-duplicated-recipe-bucket",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("recipeId").description("레시피 ID")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("즐겨 찾는 레시피 생성 실패 테스트 - 레시피 소유자의 정보와 즐겨 찾는 레시피로 등록하려는 사용자의 정보가 일치하지 않은 경우")
	@Test
	void createFailure_recipeOwnerMismatchToCreateRecipeBucket() throws Exception {
		//given
		Long recipeId = 1L;
		RecipeBucketReq recipeBucketReq = new RecipeBucketReq(recipeId);

		doThrow(new BusinessException(ErrorCode.RECIPE_OWNER_MISMATCH_TO_CREATE_RECIPE_BUCKET)).when(
			recipeBucketService).create(eq(recipeBucketReq), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/recipe-buckets")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(recipeBucketReq)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("RB001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("레시피 소유자의 정보와 즐겨 찾는 레시피로 등록하려는 사용자의 정보가 일치하지 않습니다."))
			.andDo(print())
			.andDo(document("recipe-bucket/recipe-bucket-create-failure-recipe-owner-mismatch-to-create-recipe-bucket",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("recipeId").description("레시피 ID")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("즐겨 찾는 레시피 단건 조회 성공 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long recipeId = 1L;
		Long recipeBucketId = 1L;
		Long writerId = 1L;
		Long originalRecipeId = 1L;
		Long memberId = 1L;
		Long grocery1Id = 1L;
		Long grocery2Id = 2L;
		Long recipeGrocery1Id = 1L;
		Long recipeGrocery2Id = 2L;

		LocalDateTime createdAt = LocalDateTime.now();

		RecipeGroceryRes recipeGroceryRes1 = new RecipeGroceryRes(recipeGrocery1Id, recipeId, recipe.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
		RecipeGroceryRes recipeGroceryRes2 = new RecipeGroceryRes(recipeGrocery2Id, recipeId, recipe.getTitle(), grocery2Id, grocery2.getName(), grocery2.getQuantity());

		RecipeBucketRes recipeBucketRes = new RecipeBucketRes(recipeBucketId, recipeId, writerId, member.getNickName(), recipe.getRecipeType().name(), originalRecipeId, recipe.getTitle(), recipe.getContent(), List.of(recipeGroceryRes1, recipeGroceryRes2), memberId, member.getNickName(), createdAt);

		when(recipeBucketService.findById(anyLong())).thenReturn(recipeBucketRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/recipe-buckets/{id}", recipeBucketId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(recipeBucketRes)))
			.andExpect(jsonPath("$.recipeBucketId").value(recipeBucketRes.recipeBucketId()))
			.andExpect(jsonPath("$.recipeId").value(recipeBucketRes.recipeId()))
			.andExpect(jsonPath("$.writerId").value(recipeBucketRes.writerId()))
			.andExpect(jsonPath("$.writerNickName").value(recipeBucketRes.writerNickName()))
			.andExpect(jsonPath("$.recipeType").value(recipeBucketRes.recipeType()))
			.andExpect(jsonPath("$.originalRecipeId").value(recipeBucketRes.originalRecipeId()))
			.andExpect(jsonPath("$.recipeTitle").value(recipeBucketRes.recipeTitle()))
			.andExpect(jsonPath("$.recipeContent").value(recipeBucketRes.recipeContent()))
			.andExpect(jsonPath("$.materials[0].recipeGroceryId").value(recipeBucketRes.materials().get(0).recipeGroceryId()))
			.andExpect(jsonPath("$.materials[0].recipeId").value(recipeBucketRes.materials().get(0).recipeId()))
			.andExpect(jsonPath("$.materials[0].recipeTitle").value(recipeBucketRes.materials().get(0).recipeTitle()))
			.andExpect(jsonPath("$.materials[0].groceryId").value(recipeBucketRes.materials().get(0).groceryId()))
			.andExpect(jsonPath("$.materials[0].groceryName").value(recipeBucketRes.materials().get(0).groceryName()))
			.andExpect(jsonPath("$.materials[0].groceryQuantity").value(recipeBucketRes.materials().get(0).groceryQuantity()))
			.andExpect(jsonPath("$.memberId").value(recipeBucketRes.memberId()))
			.andExpect(jsonPath("$.memberNickName").value(recipeBucketRes.memberNickName()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(recipeBucketRes.createdAt())))
			.andDo(print())
			.andDo(document("recipe-bucket/recipe-bucket-find-by-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("즐겨 찾는 레시피 ID")),
				responseFields(
					fieldWithPath("recipeBucketId").type(NUMBER).description("즐겨 찾는 레시피 ID"),
					fieldWithPath("recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("writerId").type(NUMBER).description("레시피 작성자 ID"),
					fieldWithPath("writerNickName").type(STRING).description("레시피 작성자 닉네임"),
					fieldWithPath("recipeType").type(STRING).description("레시피 타입"),
					fieldWithPath("originalRecipeId").type(NUMBER).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("recipeTitle").type(STRING).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("recipeContent").type(STRING).description("레시피 내용"),
					fieldWithPath("materials[].recipeGroceryId").type(NUMBER).description("레시피 식재료 연관 관계 ID"),
					fieldWithPath("materials[].recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("materials[].recipeTitle").type(STRING).description("레시피 제목"),
					fieldWithPath("materials[].groceryId").type(NUMBER).description("식재료 ID"),
					fieldWithPath("materials[].groceryName").type(STRING).description("식재료 이름"),
					fieldWithPath("materials[].groceryQuantity").type(STRING).description("식재료 수량"),
					fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("memberNickName").type(STRING).description("회원 닉네임"),
					fieldWithPath("createdAt").type(STRING).description("즐겨 찾는 레시피 등록 시점")
				)
			));
	}

	@DisplayName("즐겨 찾는 레시피 단건 조회 실패 테스트 - 즐겨 찾는 레시피가 존재하지 않는 경우")
	@Test
	void findByIdFailure_notFoundRecipeBucket() throws Exception {
		//given
		Long recipeBucketId = 1L;

		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_RECIPE_BUCKET)).when(
			recipeBucketService).findById(anyLong());

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/recipe-buckets/{id}", recipeBucketId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("RB003"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("즐겨 찾는 레시피가 존재하지 않습니다."))
			.andDo(print())
			.andDo(document("recipe-bucket/recipe-bucket-find-by-id-failure-not-found-recipe-bucket",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("즐겨 찾는 레시피 ID")),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("사용자의 즐겨 찾는 레시피 목록 조회 성공 테스트")
	@Test
	void findAllByMemberId() throws Exception {
		//given
		Long recipe1Id = 1L;
		Long recipe2Id = 2L;
		Long recipeBucket1Id = 1L;
		Long recipeBucket2Id = 2L;
		Long writerId = 1L;
		Long originalRecipe1Id = 1L;
		Long originalRecipe2Id = 2L;
		Long memberId = 1L;
		Long grocery1Id = 1L;
		Long grocery2Id = 2L;
		Long recipeGrocery1Id = 1L;
		Long recipeGrocery2Id = 2L;
		Long recipeGrocery3Id = 3L;
		Long recipeGrocery4Id = 4L;

		LocalDateTime createdAt = LocalDateTime.now();

		RecipeGrocery recipeGrocery3 = RecipeGrocery.builder()
			.recipe(recipe)
			.grocery(grocery1)
			.groceryName(grocery1.getName())
			.build();

		RecipeGrocery recipeGrocery4 = RecipeGrocery.builder()
			.recipe(recipe)
			.grocery(grocery2)
			.groceryName(grocery2.getName())
			.build();

		Recipe recipe2 = Recipe.builder()
			.writer(member)
			.owner(member)
			.recipeType(RecipeType.WRITTEN)
			.title("레시피2")
			.content("내용")
			.build();

		recipe2.addRecipeGrocery(recipeGrocery3);
		recipe2.addRecipeGrocery(recipeGrocery4);

		RecipeGroceryRes recipeGroceryRes1 = new RecipeGroceryRes(recipeGrocery1Id, recipe1Id, recipe.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
		RecipeGroceryRes recipeGroceryRes2 = new RecipeGroceryRes(recipeGrocery2Id, recipe1Id, recipe.getTitle(), grocery2Id, grocery2.getName(), grocery2.getQuantity());
		RecipeGroceryRes recipeGroceryRes3 = new RecipeGroceryRes(recipeGrocery3Id, recipe2Id, recipe2.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
		RecipeGroceryRes recipeGroceryRes4 = new RecipeGroceryRes(recipeGrocery4Id, recipe2Id, recipe2.getTitle(), grocery2Id, grocery2.getName(), grocery2.getQuantity());

		RecipeBucketRes recipeBucketRes1 = new RecipeBucketRes(recipeBucket1Id, recipe1Id, writerId, recipe.getWriter().getNickName(), recipe.getRecipeType().name(), originalRecipe1Id, recipe.getTitle(), recipe.getContent(), List.of(recipeGroceryRes1, recipeGroceryRes2), memberId, member.getNickName(), createdAt);
		RecipeBucketRes recipeBucketRes2 = new RecipeBucketRes(recipeBucket2Id, recipe2Id, writerId, recipe2.getWriter().getNickName(), recipe2.getRecipeType().name(), originalRecipe2Id, recipe2.getTitle(), recipe2.getContent(), List.of(recipeGroceryRes3, recipeGroceryRes4), memberId, member.getNickName(), createdAt);

		RecipeBucketsRes recipeBucketsRes = new RecipeBucketsRes(List.of(recipeBucketRes1, recipeBucketRes2), false);

		when(recipeBucketService.findAllByMemberId(any(), any(), any(), any(), anyString())).thenReturn(recipeBucketsRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/recipe-buckets")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(recipeBucketsRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.recipeBuckets", hasSize(2)))
			.andExpect(jsonPath("$.recipeBuckets[0].recipeBucketId").value(recipeBucketRes1.recipeBucketId()))
			.andExpect(jsonPath("$.recipeBuckets[0].recipeId").value(recipeBucketRes1.recipeId()))
			.andExpect(jsonPath("$.recipeBuckets[0].writerId").value(recipeBucketRes1.writerId()))
			.andExpect(jsonPath("$.recipeBuckets[0].writerNickName").value(recipeBucketRes1.writerNickName()))
			.andExpect(jsonPath("$.recipeBuckets[0].recipeType").value(recipeBucketRes1.recipeType()))
			.andExpect(jsonPath("$.recipeBuckets[0].originalRecipeId").value(recipeBucketRes1.originalRecipeId()))
			.andExpect(jsonPath("$.recipeBuckets[0].recipeTitle").value(recipeBucketRes1.recipeTitle()))
			.andExpect(jsonPath("$.recipeBuckets[0].materials[0].recipeGroceryId").value(recipeBucketRes1.materials().get(0).recipeGroceryId()))
			.andExpect(jsonPath("$.recipeBuckets[0].materials[0].recipeId").value(recipeBucketRes1.materials().get(0).recipeId()))
			.andExpect(jsonPath("$.recipeBuckets[0].materials[0].recipeTitle").value(recipeBucketRes1.materials().get(0).recipeTitle()))
			.andExpect(jsonPath("$.recipeBuckets[0].materials[0].groceryId").value(recipeBucketRes1.materials().get(0).groceryId()))
			.andExpect(jsonPath("$.recipeBuckets[0].materials[0].groceryName").value(recipeBucketRes1.materials().get(0).groceryName()))
			.andExpect(jsonPath("$.recipeBuckets[0].materials[0].groceryQuantity").value(recipeBucketRes1.materials().get(0).groceryQuantity()))
			.andExpect(jsonPath("$.recipeBuckets[0].memberId").value(recipeBucketRes1.memberId()))
			.andExpect(jsonPath("$.recipeBuckets[0].memberNickName").value(recipeBucketRes1.memberNickName()))
//			.andExpect(jsonPath("$.recipeBuckets[0].createdAt").value(substringLocalDateTime(recipeBucketRes1.createdAt())))
			.andExpect(jsonPath("$.hasNext").value(recipeBucketsRes.hasNext()))
			.andDo(print())
			.andDo(document("recipe-bucket/recipe-bucket-find-all-by-member-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				responseFields(
					fieldWithPath("recipeBuckets[].recipeBucketId").type(NUMBER).description("즐겨 찾는 레시피 ID"),
					fieldWithPath("recipeBuckets[].recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("recipeBuckets[].writerId").type(NUMBER).description("레시피 작성자 ID"),
					fieldWithPath("recipeBuckets[].writerNickName").type(STRING).description("레시피 작성자 닉네임"),
					fieldWithPath("recipeBuckets[].recipeType").type(STRING).description("레시피 타입"),
					fieldWithPath("recipeBuckets[].originalRecipeId").type(NUMBER).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("recipeBuckets[].recipeTitle").type(STRING).description("스크랩 된 레시피인 경우 본래 레시피 ID"),
					fieldWithPath("recipeBuckets[].recipeContent").type(STRING).description("레시피 내용"),
					fieldWithPath("recipeBuckets[].materials[].recipeGroceryId").type(NUMBER).description("레시피 식재료 연관 관계 ID"),
					fieldWithPath("recipeBuckets[].materials[].recipeId").type(NUMBER).description("레시피 ID"),
					fieldWithPath("recipeBuckets[].materials[].recipeTitle").type(STRING).description("레시피 제목"),
					fieldWithPath("recipeBuckets[].materials[].groceryId").type(NUMBER).description("식재료 ID"),
					fieldWithPath("recipeBuckets[].materials[].groceryName").type(STRING).description("식재료 이름"),
					fieldWithPath("recipeBuckets[].materials[].groceryQuantity").type(STRING).description("식재료 수량"),
					fieldWithPath("recipeBuckets[].memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("recipeBuckets[].memberNickName").type(STRING).description("회원 닉네임"),
					fieldWithPath("recipeBuckets[].createdAt").type(STRING).description("즐겨 찾는 레시피 등록 시점"),
					fieldWithPath("hasNext").type(BOOLEAN).description("다음 페이지(스크롤) 데이터 존재 유무")
				)
			));
	}

	@DisplayName("사용자의 즐겨 찾는 레시피 목록 조회 실패 테스트 - 회원이 존재하지 않는 경우")
	@Test
	void findAllByMemberIdFailure_notFoundMember() throws Exception {
		//given
		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER)).when(
			recipeBucketService).findAllByMemberId(any(), any(), any(), any(), anyString());

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/recipe-buckets")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("M003"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("회원을 찾을 수 없습니다."))
			.andDo(print())
			.andDo(document("recipe-bucket/recipe-bucket-find-all-by-member-id-failure-not-found-member",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("즐겨 찾는 레시피 삭제 성공 테스트")
	@Test
	void delete() throws Exception {
		//given
		Long recipeBucketId = 1L;

		doNothing().when(recipeBucketService).delete(anyLong(), anyString());

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/recipe-buckets/{id}", recipeBucketId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andDo(document("recipe-bucket/recipe-bucket-delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(
					parameterWithName("id").description("즐겨 찾는 레시피 ID")
				)
			));
	}

	private String substringLocalDateTime(LocalDateTime localDateTime) {
		StringBuilder ret = new StringBuilder(localDateTime.toString());

		if (ret.length() == 26) {
			if (ret.charAt(ret.length() - 1) == '0') {
				return ret.substring(0, ret.length() - 1);
			}

			return ret.toString();
		}

		ret = new StringBuilder(ret.substring(0, ret.length() - 2));

		return ret.toString();
	}
}
