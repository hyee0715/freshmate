package com.icebox.freshmate.domain.grocery.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.icebox.freshmate.domain.grocery.application.GroceryService;
import com.icebox.freshmate.domain.grocery.application.dto.request.GroceryReq;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceriesRes;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceryRes;
import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageType;
import com.icebox.freshmate.global.TestPrincipalDetailsService;

@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(value = {GroceryController.class})
@AutoConfigureRestDocs
class GroceryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private GroceryService groceryService;

	private final TestPrincipalDetailsService testUserDetailsService = new TestPrincipalDetailsService();
	private PrincipalDetails principalDetails;

	private Member member;
	private Refrigerator refrigerator;
	private Storage storage;
	private Grocery grocery;

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

		grocery = Grocery.builder()
			.storage(storage)
			.name("양배추")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().plusDays(7))
			.build();
	}

	@DisplayName("식료품 생성 테스트")
	@Test
	void create() throws Exception {
		//given
		Long groceryId = 1L;
		Long storageId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();

		GroceryReq groceryReq = new GroceryReq(grocery.getName(), grocery.getGroceryType().name(), grocery.getQuantity(), grocery.getDescription(), grocery.getExpirationDate(), grocery.getStorage().getId());
		GroceryRes groceryRes = new GroceryRes(groceryId, grocery.getName(), grocery.getGroceryType().name(), grocery.getQuantity(), grocery.getDescription(), grocery.getExpirationDate(), storageId, grocery.getStorage().getName(), grocery.getGroceryExpirationType().name(), createdAt);

		when(groceryService.create(any(GroceryReq.class), any(String.class))).thenReturn(groceryRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/groceries")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(groceryReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(groceryRes)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.groceryId").value(groceryId))
			.andExpect(jsonPath("$.groceryName").value(grocery.getName()))
			.andExpect(jsonPath("$.groceryType").value(grocery.getGroceryType().name()))
			.andExpect(jsonPath("$.quantity").value(grocery.getQuantity()))
			.andExpect(jsonPath("$.description").value(grocery.getDescription()))
			.andExpect(jsonPath("$.expirationDate").value(formatLocalDate(grocery.getExpirationDate())))
			.andExpect(jsonPath("$.storageId").value(storageId))
			.andExpect(jsonPath("$.storageName").value(grocery.getStorage().getName()))
			.andExpect(jsonPath("$.groceryExpirationType").value(grocery.getGroceryExpirationType().name()))
			.andExpect(jsonPath("$.createdAt").value(formatLocalDateTime(createdAt)))
			.andDo(print())
			.andDo(document("grocery/grocery-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("name").description("식료품 이름"),
					fieldWithPath("groceryType").description("식료품 타입"),
					fieldWithPath("quantity").description("식료품 수량"),
					fieldWithPath("description").description("식료품 설명"),
					fieldWithPath("expirationDate").description("식료품 유통기한"),
					fieldWithPath("storageId").description("냉장고 저장소 ID")
				),
				responseFields(
					fieldWithPath("groceryId").type(NUMBER).description("식료품 ID"),
					fieldWithPath("groceryName").type(STRING).description("식료품 이름"),
					fieldWithPath("groceryType").type(STRING).description("식료품 타입"),
					fieldWithPath("quantity").type(STRING).description("식료품 수량"),
					fieldWithPath("description").type(STRING).description("식료품 설명"),
					fieldWithPath("expirationDate").type(STRING).description("식료품 유통기한"),
					fieldWithPath("storageId").type(NUMBER).description("냉장고 저장소 ID"),
					fieldWithPath("storageName").type(STRING).description("냉장고 저장소 이름"),
					fieldWithPath("groceryExpirationType").type(STRING).description("식료품 유통기한 만료 상태"),
					fieldWithPath("createdAt").type(STRING).description("식료품 생성 날짜")
				)
			));
	}

	@DisplayName("식료품 단건 조회 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long groceryId = 1L;
		Long storageId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();

		GroceryRes groceryRes = new GroceryRes(groceryId, grocery.getName(), grocery.getGroceryType().name(), grocery.getQuantity(), grocery.getDescription(), grocery.getExpirationDate(), storageId, grocery.getStorage().getName(), grocery.getGroceryExpirationType().name(), createdAt);

		when(groceryService.findById(groceryId)).thenReturn(groceryRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/groceries/{id}", groceryId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(groceryRes)))
			.andExpect(jsonPath("$.groceryId").value(groceryId))
			.andExpect(jsonPath("$.groceryName").value(grocery.getName()))
			.andExpect(jsonPath("$.groceryType").value(grocery.getGroceryType().name()))
			.andExpect(jsonPath("$.quantity").value(grocery.getQuantity()))
			.andExpect(jsonPath("$.description").value(grocery.getDescription()))
			.andExpect(jsonPath("$.expirationDate").value(formatLocalDate(grocery.getExpirationDate())))
			.andExpect(jsonPath("$.storageId").value(storageId))
			.andExpect(jsonPath("$.storageName").value(grocery.getStorage().getName()))
			.andExpect(jsonPath("$.groceryExpirationType").value(grocery.getGroceryExpirationType().name()))
			.andExpect(jsonPath("$.createdAt").value(formatLocalDateTime(createdAt)))
			.andDo(print())
			.andDo(document("grocery/grocery-find-by-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("식료품 ID")),
				responseFields(
					fieldWithPath("groceryId").type(NUMBER).description("식료품 ID"),
					fieldWithPath("groceryName").type(STRING).description("식료품 이름"),
					fieldWithPath("groceryType").type(STRING).description("식료품 타입"),
					fieldWithPath("quantity").type(STRING).description("식료품 수량"),
					fieldWithPath("description").type(STRING).description("식료품 설명"),
					fieldWithPath("expirationDate").type(STRING).description("식료품 유통기한"),
					fieldWithPath("storageId").type(NUMBER).description("냉장고 저장소 ID"),
					fieldWithPath("storageName").type(STRING).description("냉장고 저장소 이름"),
					fieldWithPath("groceryExpirationType").type(STRING).description("식료품 유통기한 만료 상태"),
					fieldWithPath("createdAt").type(STRING).description("식료품 생성 날짜")
				)
			));
	}

	@DisplayName("특정 냉장고 저장소의 모든 식료품 조회 테스트")
	@Test
	void findAllByStorageId() throws Exception {
		//given
		Long storageId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();

		Grocery grocery2 = Grocery.builder()
			.storage(storage)
			.name("배추")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("2개")
			.description("김장용")
			.expirationDate(LocalDate.now().plusDays(7))
			.build();

		GroceryRes groceryRes1 = new GroceryRes(1L, grocery.getName(), grocery.getGroceryType().name(), grocery.getQuantity(), grocery.getDescription(), grocery.getExpirationDate(), storageId, grocery.getStorage().getName(), grocery.getGroceryExpirationType().name(), createdAt);
		GroceryRes groceryRes2 = new GroceryRes(2L, grocery2.getName(), grocery2.getGroceryType().name(), grocery2.getQuantity(), grocery2.getDescription(), grocery.getExpirationDate(), storageId, grocery2.getStorage().getName(), grocery2.getGroceryExpirationType().name(), createdAt);

		GroceriesRes groceriesRes = new GroceriesRes(List.of(groceryRes1, groceryRes2));

		when(groceryService.findAllByStorageId(eq(storageId), anyString())).thenReturn(groceriesRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/groceries/storages/{storageId}", storageId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(groceriesRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.groceries", hasSize(2)))
			.andExpect(jsonPath("$.groceries[0].groceryId").value(groceryRes1.groceryId()))
			.andExpect(jsonPath("$.groceries[0].groceryName").value(groceryRes1.groceryName()))
			.andExpect(jsonPath("$.groceries[0].groceryType").value(groceryRes1.groceryType()))
			.andExpect(jsonPath("$.groceries[0].quantity").value(groceryRes1.quantity()))
			.andExpect(jsonPath("$.groceries[0].description").value(groceryRes1.description()))
			.andExpect(jsonPath("$.groceries[0].expirationDate").value(formatLocalDate(groceryRes1.expirationDate())))
			.andExpect(jsonPath("$.groceries[0].storageId").value(storageId))
			.andExpect(jsonPath("$.groceries[0].storageName").value(groceryRes1.storageName()))
			.andExpect(jsonPath("$.groceries[0].groceryExpirationType").value(groceryRes1.groceryExpirationType()))
			.andExpect(jsonPath("$.groceries[0].createdAt").value(formatLocalDateTime(createdAt)))
			.andDo(print())
			.andDo(document("grocery/grocery-find-all-by-storage-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(parameterWithName("storageId").description("냉장고 저장소 ID")),
				responseFields(
					fieldWithPath("groceries").type(ARRAY).description("식료품 배열"),
					fieldWithPath("groceries[].groceryId").type(NUMBER).description("식료품 ID"),
					fieldWithPath("groceries[].groceryName").type(STRING).description("식료품 이름"),
					fieldWithPath("groceries[].groceryType").type(STRING).description("식료품 타입"),
					fieldWithPath("groceries[].quantity").type(STRING).description("식료품 수량"),
					fieldWithPath("groceries[].description").type(STRING).description("식료품 설명"),
					fieldWithPath("groceries[].expirationDate").type(STRING).description("식료품 유통기한"),
					fieldWithPath("groceries[].storageId").type(NUMBER).description("냉장고 저장소 ID"),
					fieldWithPath("groceries[].storageName").type(STRING).description("냉장고 저장소 이름"),
					fieldWithPath("groceries[].groceryExpirationType").type(STRING).description("식료품 유통기한 만료 상태"),
					fieldWithPath("groceries[].createdAt").type(STRING).description("식료품 생성 날짜")
				)
			));
	}

	@DisplayName("식료품 수정 테스트")
	@Test
	void update() throws Exception {
		//given
		Long groceryId = 1L;
		Long storageId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();

		GroceryReq groceryReq = new GroceryReq("식료품 수정", GroceryType.SNACKS.name(), "10개", "수정", LocalDate.now().plusDays(2), storageId);
		GroceryRes groceryRes = new GroceryRes(groceryId, groceryReq.name(), groceryReq.groceryType(), groceryReq.quantity(), groceryReq.description(), groceryReq.expirationDate(), storageId, grocery.getStorage().getName(), grocery.getGroceryExpirationType().name(), createdAt);

		when(groceryService.update(eq(groceryId), any(GroceryReq.class), anyString())).thenReturn(groceryRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/groceries/{id}", groceryId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(groceryReq)))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(groceryRes)))
			.andExpect(jsonPath("$.groceryId").value(groceryId))
			.andExpect(jsonPath("$.groceryName").value(groceryRes.groceryName()))
			.andExpect(jsonPath("$.groceryType").value(groceryRes.groceryType()))
			.andExpect(jsonPath("$.quantity").value(groceryRes.quantity()))
			.andExpect(jsonPath("$.description").value(groceryRes.description()))
			.andExpect(jsonPath("$.expirationDate").value(formatLocalDate(groceryRes.expirationDate())))
			.andExpect(jsonPath("$.storageId").value(storageId))
			.andExpect(jsonPath("$.storageName").value(groceryRes.storageName()))
			.andExpect(jsonPath("$.groceryExpirationType").value(groceryRes.groceryExpirationType()))
			.andExpect(jsonPath("$.createdAt").value(formatLocalDateTime(createdAt)))
			.andDo(print())
			.andDo(document("grocery/grocery-update",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(parameterWithName("id").description("식료품 ID")),
				requestFields(
					fieldWithPath("name").description("수정할 식료품 이름"),
					fieldWithPath("groceryType").description("수정할 식료품 타입"),
					fieldWithPath("quantity").description("수정할 식료품 수량"),
					fieldWithPath("description").description("수정할 식료품 설명"),
					fieldWithPath("expirationDate").description("수정할 식료품 유통기한"),
					fieldWithPath("storageId").description("수정할 냉장고 저장소 ID")
				),
				responseFields(
					fieldWithPath("groceryId").type(NUMBER).description("식료품 ID"),
					fieldWithPath("groceryName").type(STRING).description("식료품 이름"),
					fieldWithPath("groceryType").type(STRING).description("식료품 타입"),
					fieldWithPath("quantity").type(STRING).description("식료품 수량"),
					fieldWithPath("description").type(STRING).description("식료품 설명"),
					fieldWithPath("expirationDate").type(STRING).description("식료품 유통기한"),
					fieldWithPath("storageId").type(NUMBER).description("냉장고 저장소 ID"),
					fieldWithPath("storageName").type(STRING).description("냉장고 저장소 이름"),
					fieldWithPath("groceryExpirationType").type(STRING).description("식료품 유통기한 만료 상태"),
					fieldWithPath("createdAt").type(STRING).description("식료품 생성 날짜")
				)
			));
	}

	@DisplayName("식료품 삭제 테스트")
	@Test
	void delete() throws Exception {
		//given
		Long groceryId = 1L;

		doNothing().when(groceryService).delete(groceryId, member.getUsername());

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/groceries/{id}", groceryId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andDo(document("grocery/grocery-delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(
					parameterWithName("id").description("냉장고 저장소 ID")
				)
			));
	}

	private String formatLocalDate(LocalDate localDate) {

		return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate);
	}

	private String formatLocalDateTime(LocalDateTime localDateTime) {

		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(localDateTime);
	}
}
