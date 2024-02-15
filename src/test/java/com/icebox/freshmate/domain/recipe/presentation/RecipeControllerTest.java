package com.icebox.freshmate.domain.recipe.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
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
import com.icebox.freshmate.domain.recipe.application.RecipeService;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeCreateReq;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeUpdateReq;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipeRes;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipesRes;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeImage;
import com.icebox.freshmate.domain.recipe.domain.RecipeType;
import com.icebox.freshmate.domain.recipegrocery.application.dto.request.RecipeGroceryReq;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageType;
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
	private Refrigerator refrigerator;
	private Storage storage;
	private Grocery grocery1;
	private Grocery grocery2;
	private RecipeGrocery recipeGrocery1;
	private RecipeGrocery recipeGrocery2;
	private RecipeImage recipeImage1;
	private RecipeImage recipeImage2;

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
			.content("내용")
			.build();

		recipe2 = Recipe.builder()
			.writer(member2)
			.owner(member1)
			.recipeType(RecipeType.SCRAPED)
			.title("레시피2")
			.content("내용")
			.build();

		recipe3 = Recipe.builder()
			.writer(member1)
			.owner(member2)
			.recipeType(RecipeType.SCRAPED)
			.title("레시피3")
			.content("내용")
			.build();

		refrigerator = Refrigerator.builder()
			.name("우리 집 냉장고")
			.member(member1)
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

		recipeGrocery1 = RecipeGrocery.builder()
			.recipe(recipe1)
			.grocery(grocery1)
			.groceryName(grocery1.getName())
			.build();

		recipeGrocery2 = RecipeGrocery.builder()
			.recipe(recipe1)
			.grocery(grocery2)
			.groceryName(grocery2.getName())
			.build();

		recipe1.addRecipeGrocery(recipeGrocery1);
		recipe1.addRecipeGrocery(recipeGrocery2);

		String imageFileName1 = "image1.jpg";
		String imagePath1 = "https://test-image-urls.com/image1.jpg";

		recipeImage1 = RecipeImage
			.builder()
			.recipe(recipe1)
			.fileName(imageFileName1)
			.path(imagePath1)
			.build();

		String imageFileName2 = "image2.jpg";
		String imagePath2 = "https://test-image-urls.com/image2.jpg";

		recipeImage2 = RecipeImage
			.builder()
			.recipe(recipe1)
			.fileName(imageFileName2)
			.path(imagePath2)
			.build();

		recipe1.addRecipeImage(recipeImage1);
		recipe1.addRecipeImage(recipeImage2);

		recipe2.addRecipeImage(recipeImage1);
		recipe2.addRecipeImage(recipeImage2);

		recipe3.addRecipeImage(recipeImage1);
		recipe3.addRecipeImage(recipeImage2);
	}

	@DisplayName("레시피 생성 테스트")
	@Test
	void create() throws Exception {
		//given
		Long recipe1Id = 1L;
		Long member1Id = 1L;
		Long grocery1Id = 1L;

		RecipeGroceryReq recipeGroceryReq = new RecipeGroceryReq(grocery1Id, grocery1.getName(), grocery1.getQuantity());
		List<RecipeGroceryReq> recipeGroceriesReq = List.of(recipeGroceryReq);
		RecipeCreateReq recipeCreateReq = new RecipeCreateReq(recipe1.getTitle(), recipeGroceriesReq, recipe1.getContent());

		MockMultipartFile file1 = new MockMultipartFile("imageFiles", "test1.jpg", "image/jpeg", "Spring Framework".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("imageFiles", "test2.jpg", "image/jpeg", "Spring Framework".getBytes());
		MockMultipartFile request = new MockMultipartFile("recipeCreateReq", "recipeCreateReq",
			"application/json",
			objectMapper.writeValueAsString(recipeCreateReq).getBytes());


		ImageRes imageRes1 = new ImageRes(recipeImage1.getFileName(), recipeImage1.getPath());
		ImageRes imageRes2 = new ImageRes(recipeImage2.getFileName(), recipeImage2.getPath());

		RecipeGroceryRes recipeGroceryRes = new RecipeGroceryRes(1L, recipe1Id, recipe1.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
		RecipeRes recipeRes = new RecipeRes(recipe1Id, member1Id, member1.getNickName(), member1Id, member1.getNickName(), RecipeType.WRITTEN.name(), recipe1Id, recipe1.getTitle(), recipe1.getContent(), List.of(recipeGroceryRes), List.of(imageRes1, imageRes2));

		when(recipeService.create(any(RecipeCreateReq.class), any(ImageUploadReq.class), any(String.class))).thenReturn(recipeRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/recipes")
			.file(file1)
			.file(file2)
			.file(request)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON))
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
			.andExpect(jsonPath("$.content").value(recipeRes.content()))
			.andExpect(jsonPath("$.materials[0].groceryId").value(recipeRes.materials().get(0).groceryId()))
			.andExpect(jsonPath("$.materials[0].groceryName").value(recipeRes.materials().get(0).groceryName()))
			.andExpect(jsonPath("$.materials[0].groceryQuantity").value(recipeRes.materials().get(0).groceryQuantity()))
			.andExpect(jsonPath("$.images[0].fileName").value(recipeRes.images().get(0).fileName()))
			.andExpect(jsonPath("$.images[0].path").value(recipeRes.images().get(0).path()))
			.andDo(print())
			.andDo(document("recipe/recipe-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestParts(
					partWithName("imageFiles").description("레시피 이미지들"),
					partWithName("recipeCreateReq").description("레시피 등록 내용")
				),
				requestPartFields("recipeCreateReq",
					fieldWithPath("title").description("레시피 제목"),
					fieldWithPath("materials[].groceryId").description("레시피 식재료 ID"),
					fieldWithPath("materials[].groceryName").description("레시피 식재료 이름"),
					fieldWithPath("materials[].groceryQuantity").description("레시피 식재료 수량"),
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
					fieldWithPath("content").type(STRING).description("레시피 내용"),
					fieldWithPath("materials[].recipeGroceryId").type(NUMBER).description("레시피 식재료 ID"),
					fieldWithPath("materials[].recipeId").type(NUMBER).description("회원이 등록한 레시피 ID"),
					fieldWithPath("materials[].recipeTitle").type(STRING).description("회원이 등록한 레시피 제목"),
					fieldWithPath("materials[].groceryId").type(NUMBER).description("회원이 등록한 식재료 ID"),
					fieldWithPath("materials[].groceryName").type(STRING).description("회원이 등록한 식재료 이름"),
					fieldWithPath("materials[].groceryQuantity").type(STRING).description("회원이 등록한 식재료 수량"),
					fieldWithPath("images[].fileName").type(STRING).description("레시피 이미지 파일 이름"),
					fieldWithPath("images[].path").type(STRING).description("레시피 이미지 파일 경로")
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
		Long recipe1Id = 1L;
		Long grocery1Id = 1L;
		Long recipeGrocery1Id = 1L;

		ImageRes imageRes1 = new ImageRes(recipeImage1.getFileName(), recipeImage1.getPath());
		ImageRes imageRes2 = new ImageRes(recipeImage2.getFileName(), recipeImage2.getPath());

		RecipeGroceryRes recipeGroceryRes = new RecipeGroceryRes(recipeGrocery1Id, recipe1Id, recipe1.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
		RecipeRes recipeRes = new RecipeRes(scrapedRecipeId, writerId, member2.getNickName(), ownerId, member1.getNickName(), RecipeType.SCRAPED.name(), originalRecipeId, recipe1.getTitle(), recipe1.getContent(), List.of(recipeGroceryRes), List.of(imageRes1, imageRes2));

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
			.andExpect(jsonPath("$.content").value(recipeRes.content()))
			.andExpect(jsonPath("$.materials[0].groceryId").value(recipeRes.materials().get(0).groceryId()))
			.andExpect(jsonPath("$.materials[0].groceryName").value(recipeRes.materials().get(0).groceryName()))
			.andExpect(jsonPath("$.materials[0].groceryQuantity").value(recipeRes.materials().get(0).groceryQuantity()))
			.andExpect(jsonPath("$.images[0].fileName").value(recipeRes.images().get(0).fileName()))
			.andExpect(jsonPath("$.images[0].path").value(recipeRes.images().get(0).path()))
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
					fieldWithPath("content").type(STRING).description("레시피 내용"),
					fieldWithPath("materials[].recipeGroceryId").type(NUMBER).description("레시피 식재료 ID"),
					fieldWithPath("materials[].recipeId").type(NUMBER).description("회원이 등록한 레시피 ID"),
					fieldWithPath("materials[].recipeTitle").type(STRING).description("회원이 등록한 레시피 제목"),
					fieldWithPath("materials[].groceryId").type(NUMBER).description("회원이 등록한 식재료 ID"),
					fieldWithPath("materials[].groceryName").type(STRING).description("회원이 등록한 식재료 이름"),
					fieldWithPath("materials[].groceryQuantity").type(STRING).description("회원이 등록한 식재료 수량"),
					fieldWithPath("images[].fileName").type(STRING).description("레시피 이미지 파일 이름"),
					fieldWithPath("images[].path").type(STRING).description("레시피 이미지 파일 경로")
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
		Long grocery1Id = 1L;
		Long recipeGroceryId = 1L;

		ImageRes imageRes1 = new ImageRes(recipeImage1.getFileName(), recipeImage1.getPath());
		ImageRes imageRes2 = new ImageRes(recipeImage2.getFileName(), recipeImage2.getPath());

		RecipeGroceryRes recipeGroceryRes = new RecipeGroceryRes(recipeGroceryId, recipe1Id, recipe1.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
		RecipeRes recipeRes = new RecipeRes(recipe1Id, member1Id, member1.getNickName(), member1Id, member1.getNickName(), RecipeType.WRITTEN.name(), recipe1Id, recipe1.getTitle(), recipe1.getContent(), List.of(recipeGroceryRes), List.of(imageRes1, imageRes2));

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
			.andExpect(jsonPath("$.content").value(recipeRes.content()))
			.andExpect(jsonPath("$.materials[0].recipeGroceryId").value(recipeRes.materials().get(0).recipeGroceryId()))
			.andExpect(jsonPath("$.materials[0].recipeId").value(recipeRes.materials().get(0).recipeId()))
			.andExpect(jsonPath("$.materials[0].recipeTitle").value(recipeRes.materials().get(0).recipeTitle()))
			.andExpect(jsonPath("$.materials[0].groceryId").value(recipeRes.materials().get(0).groceryId()))
			.andExpect(jsonPath("$.materials[0].groceryName").value(recipeRes.materials().get(0).groceryName()))
			.andExpect(jsonPath("$.materials[0].groceryQuantity").value(recipeRes.materials().get(0).groceryQuantity()))
			.andExpect(jsonPath("$.images[0].fileName").value(recipeRes.images().get(0).fileName()))
			.andExpect(jsonPath("$.images[0].path").value(recipeRes.images().get(0).path()))
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
					fieldWithPath("content").type(STRING).description("레시피 내용"),
					fieldWithPath("materials[].recipeGroceryId").type(NUMBER).description("레시피 식재료 ID"),
					fieldWithPath("materials[].recipeId").type(NUMBER).description("회원이 등록한 레시피 ID"),
					fieldWithPath("materials[].recipeTitle").type(STRING).description("회원이 등록한 레시피 제목"),
					fieldWithPath("materials[].groceryId").type(NUMBER).description("회원이 등록한 식재료 ID"),
					fieldWithPath("materials[].groceryName").type(STRING).description("회원이 등록한 식재료 이름"),
					fieldWithPath("materials[].groceryQuantity").type(STRING).description("회원이 등록한 식재료 수량"),
					fieldWithPath("images[].fileName").type(STRING).description("레시피 이미지 파일 이름"),
					fieldWithPath("images[].path").type(STRING).description("레시피 이미지 파일 경로")
				)
			));
	}

	@DisplayName("사용자의 모든 레시피 조회 테스트")
	@Test
	void findAllByMemberId() throws Exception {
		//given
		Long member1Id = 1L;
		Long member2Id = 2L;
		Long recipe1Id = 1L;
		Long recipe3Id = 3L;
		Long recipeGrocery1Id = 1L;
		Long recipeGrocery2Id = 1L;
		Long grocery1Id = 1L;
		Long grocery2Id = 2L;

		ImageRes imageRes1 = new ImageRes(recipeImage1.getFileName(), recipeImage1.getPath());
		ImageRes imageRes2 = new ImageRes(recipeImage2.getFileName(), recipeImage2.getPath());

		RecipeGroceryRes recipeGroceryRes1 = new RecipeGroceryRes(recipeGrocery1Id, recipe1Id, recipe1.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
		RecipeGroceryRes recipeGroceryRes2 = new RecipeGroceryRes(recipeGrocery2Id, recipe3Id, recipe3.getTitle(), grocery2Id, grocery2.getName(), grocery2.getQuantity());

		RecipeRes recipeRes1 = new RecipeRes(recipe1Id, member1Id, member1.getNickName(), member1Id, member1.getNickName(), RecipeType.WRITTEN.name(), recipe1Id, recipe1.getTitle(), recipe1.getContent(), List.of(recipeGroceryRes1), List.of(imageRes1, imageRes2));
		RecipeRes recipeRes2 = new RecipeRes(recipe3Id, member1Id, member1.getNickName(), member2Id, member2.getNickName(), RecipeType.SCRAPED.name(), recipe3Id, recipe3.getTitle(), recipe3.getContent(), List.of(recipeGroceryRes2), List.of(imageRes1, imageRes2));

		RecipesRes recipesRes = new RecipesRes(List.of(recipeRes1, recipeRes2), false);

		when(recipeService.findAllByWriterIdAndRecipeType(any(), any(), any(), any())).thenReturn(recipesRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/recipes")
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
			.andExpect(jsonPath("$.recipes[0].content").value(recipeRes1.content()))
			.andExpect(jsonPath("$.recipes[0].materials[0].recipeGroceryId").value(recipeRes1.materials().get(0).recipeGroceryId()))
			.andExpect(jsonPath("$.recipes[0].materials[0].recipeId").value(recipeRes1.materials().get(0).recipeId()))
			.andExpect(jsonPath("$.recipes[0].materials[0].recipeTitle").value(recipeRes1.materials().get(0).recipeTitle()))
			.andExpect(jsonPath("$.recipes[0].materials[0].groceryId").value(recipeRes1.materials().get(0).groceryId()))
			.andExpect(jsonPath("$.recipes[0].materials[0].groceryName").value(recipeRes1.materials().get(0).groceryName()))
			.andExpect(jsonPath("$.recipes[0].materials[0].groceryQuantity").value(recipeRes1.materials().get(0).groceryQuantity()))
			.andExpect(jsonPath("$.recipes[0].images[0].fileName").value(recipeRes1.images().get(0).fileName()))
			.andExpect(jsonPath("$.recipes[0].images[0].path").value(recipeRes1.images().get(0).path()))
			.andExpect(jsonPath("$.hasNext").value(recipesRes.hasNext()))
			.andDo(print())
			.andDo(document("recipe/recipe-find-all-by-member-id",
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
					fieldWithPath("recipes[].content").type(STRING).description("레시피 내용"),
					fieldWithPath("recipes[].materials[].recipeGroceryId").type(NUMBER).description("레시피 식재료 ID"),
					fieldWithPath("recipes[].materials[].recipeId").type(NUMBER).description("회원이 등록한 레시피 ID"),
					fieldWithPath("recipes[].materials[].recipeTitle").type(STRING).description("회원이 등록한 레시피 제목"),
					fieldWithPath("recipes[].materials[].groceryId").type(NUMBER).description("회원이 등록한 식재료 ID"),
					fieldWithPath("recipes[].materials[].groceryName").type(STRING).description("회원이 등록한 식재료 이름"),
					fieldWithPath("recipes[].materials[].groceryQuantity").type(STRING).description("회원이 등록한 식재료 수량"),
					fieldWithPath("recipes[].images[].fileName").type(STRING).description("레시피 이미지 파일 이름"),
					fieldWithPath("recipes[].images[].path").type(STRING).description("레시피 이미지 파일 경로"),
					fieldWithPath("hasNext").type(BOOLEAN).description("다음 페이지(스크롤) 데이터 존재 유무")
				)
			));
	}

	@DisplayName("사용자가 작성한 레시피 수정 성공 테스트")
	@Test
	void update() throws Exception {
		//given
		Long recipe1Id = 1L;
		Long member1Id = 1L;
		Long grocery1Id = 1L;

		RecipeUpdateReq recipeUpdateReq = new RecipeUpdateReq("제목수정", "내용수정");

		ImageRes imageRes1 = new ImageRes(recipeImage1.getFileName(), recipeImage1.getPath());
		ImageRes imageRes2 = new ImageRes(recipeImage2.getFileName(), recipeImage2.getPath());

		RecipeGroceryRes recipeGroceryRes = new RecipeGroceryRes(1L, recipe1Id, recipe1.getTitle(), grocery1Id, grocery1.getName(), grocery1.getQuantity());
		RecipeRes recipeRes = new RecipeRes(recipe1Id, member1Id, member1.getNickName(), member1Id, member1.getNickName(), RecipeType.WRITTEN.name(), recipe1Id, recipeUpdateReq.title(), recipeUpdateReq.content(), List.of(recipeGroceryRes), List.of(imageRes1, imageRes2));

		when(recipeService.update(anyLong(), any(RecipeUpdateReq.class), any(String.class))).thenReturn(recipeRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/recipes/{id}", recipe1Id)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(recipeUpdateReq)))
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
			.andExpect(jsonPath("$.content").value(recipeRes.content()))
			.andExpect(jsonPath("$.materials[0].recipeGroceryId").value(recipeRes.materials().get(0).recipeGroceryId()))
			.andExpect(jsonPath("$.materials[0].recipeId").value(recipeRes.materials().get(0).recipeId()))
			.andExpect(jsonPath("$.materials[0].recipeTitle").value(recipeRes.materials().get(0).recipeTitle()))
			.andExpect(jsonPath("$.materials[0].groceryId").value(recipeRes.materials().get(0).groceryId()))
			.andExpect(jsonPath("$.materials[0].groceryName").value(recipeRes.materials().get(0).groceryName()))
			.andExpect(jsonPath("$.materials[0].groceryQuantity").value(recipeRes.materials().get(0).groceryQuantity()))
			.andExpect(jsonPath("$.images[0].fileName").value(recipeRes.images().get(0).fileName()))
			.andExpect(jsonPath("$.images[0].path").value(recipeRes.images().get(0).path()))
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
					fieldWithPath("content").type(STRING).description("수정된 레시피 내용"),
					fieldWithPath("materials[].recipeGroceryId").type(NUMBER).description("레시피 식재료 ID"),
					fieldWithPath("materials[].recipeId").type(NUMBER).description("회원이 등록한 레시피 ID"),
					fieldWithPath("materials[].recipeTitle").type(STRING).description("회원이 등록한 레시피 제목"),
					fieldWithPath("materials[].groceryId").type(NUMBER).description("회원이 등록한 식재료 ID"),
					fieldWithPath("materials[].groceryName").type(STRING).description("회원이 등록한 식재료 이름"),
					fieldWithPath("materials[].groceryQuantity").type(STRING).description("회원이 등록한 식재료 수량"),
					fieldWithPath("images[].fileName").type(STRING).description("레시피 이미지 파일 이름"),
					fieldWithPath("images[].path").type(STRING).description("레시피 이미지 파일 경로")
				)
			));
	}

	@DisplayName("사용자가 작성한 레시피 수정 실패 테스트 - 스크랩한 레시피는 수정 불가")
	@Test
	void updateFailure_invalidUpdateAttemptToScrapedRecipe() throws Exception {
		//given
		Long recipe1Id = 1L;

		RecipeUpdateReq recipeUpdateReq = new RecipeUpdateReq("제목수정", "내용수정");

		doThrow(new BusinessException(ErrorCode.INVALID_UPDATE_ATTEMPT_TO_SCRAPED_RECIPE)).when(
			recipeService).update(anyLong(), any(RecipeUpdateReq.class), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/recipes/{id}", recipe1Id)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(recipeUpdateReq)))
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
