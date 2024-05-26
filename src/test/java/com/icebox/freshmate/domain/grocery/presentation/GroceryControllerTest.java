package com.icebox.freshmate.domain.grocery.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.grocery.application.GroceryService;
import com.icebox.freshmate.domain.grocery.application.dto.request.GroceryReq;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceriesRes;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceryRes;
import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryImage;
import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageType;
import com.icebox.freshmate.global.TestPrincipalDetailsService;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;
import com.icebox.freshmate.global.error.exception.InvalidValueException;

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
	private GroceryImage groceryImage1;
	private GroceryImage groceryImage2;

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

		String imageFileName1 = "image1.jpg";
		String imagePath1 = "https://test-image-urls.com/image1.jpg";

		groceryImage1 = GroceryImage
			.builder()
			.grocery(grocery)
			.fileName(imageFileName1)
			.path(imagePath1)
			.build();

		String imageFileName2 = "image2.jpg";
		String imagePath2 = "https://test-image-urls.com/image2.jpg";

		groceryImage2 = GroceryImage
			.builder()
			.grocery(grocery)
			.fileName(imageFileName2)
			.path(imagePath2)
			.build();

		grocery.addGroceryImage(groceryImage1);
		grocery.addGroceryImage(groceryImage2);
	}

	@DisplayName("식료품 생성 성공 테스트")
	@Test
	void create() throws Exception {
		//given
		Long groceryId = 1L;
		Long storageId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		GroceryReq groceryReq = new GroceryReq(grocery.getName(), grocery.getGroceryType().name(), grocery.getQuantity(), grocery.getDescription(), grocery.getExpirationDate(), storageId);

		MockMultipartFile file1 = new MockMultipartFile("imageFiles", "test1.jpg", "image/jpeg", "Spring Framework" .getBytes());
		MockMultipartFile file2 = new MockMultipartFile("imageFiles", "test2.jpg", "image/jpeg", "Spring Framework" .getBytes());
		MockMultipartFile request = new MockMultipartFile("groceryReq", "groceryReq",
			"application/json",
			objectMapper.writeValueAsString(groceryReq).getBytes(StandardCharsets.UTF_8));

		ImageRes imageRes1 = new ImageRes(groceryImage1.getFileName(), groceryImage1.getPath());
		ImageRes imageRes2 = new ImageRes(groceryImage2.getFileName(), groceryImage2.getPath());

		GroceryRes groceryRes = new GroceryRes(groceryId, grocery.getName(), grocery.getGroceryType().name(), grocery.getQuantity(), grocery.getDescription(), grocery.getExpirationDate(), storageId, grocery.getStorage().getName(), grocery.getGroceryExpirationType().name(), createdAt, updatedAt, List.of(imageRes1, imageRes2));

		when(groceryService.create(any(), any(), any())).thenReturn(groceryRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/groceries")
				.file(file1)
				.file(file2)
				.file(request)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(groceryReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(groceryRes)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.groceryId").value(groceryRes.groceryId()))
			.andExpect(jsonPath("$.groceryName").value(groceryRes.groceryName()))
			.andExpect(jsonPath("$.groceryType").value(groceryRes.groceryType()))
			.andExpect(jsonPath("$.quantity").value(groceryRes.quantity()))
			.andExpect(jsonPath("$.description").value(groceryRes.description()))
			.andExpect(jsonPath("$.expirationDate").value(formatLocalDate(groceryRes.expirationDate())))
			.andExpect(jsonPath("$.storageId").value(groceryRes.storageId()))
			.andExpect(jsonPath("$.storageName").value(groceryRes.storageName()))
			.andExpect(jsonPath("$.groceryExpirationType").value(groceryRes.groceryExpirationType()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(groceryRes.createdAt())))
//			.andExpect(jsonPath("$.updatedAt").value(substringLocalDateTime(groceryRes.updatedAt())))
			.andExpect(jsonPath("$.images[0].fileName").value(groceryRes.images().get(0).fileName()))
			.andExpect(jsonPath("$.images[0].path").value(groceryRes.images().get(0).path()))
			.andDo(print())
			.andDo(document("grocery/grocery-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestParts(
					partWithName("imageFiles").description("식료품 이미지"),
					partWithName("groceryReq").description("식료품 등록 내용")
				),
				requestPartFields("groceryReq",
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
					fieldWithPath("createdAt").type(STRING).description("식료품 생성 날짜"),
					fieldWithPath("updatedAt").type(STRING).description("식료품 수정 날짜"),
					fieldWithPath("images[].fileName").type(STRING).description("식료품 이미지 파일 이름"),
					fieldWithPath("images[].path").type(STRING).description("식료품 이미지 파일 경로")
				)
			));
	}

	@DisplayName("식료품 생성 실패 테스트 - 회원이 존재하지 않는 경우")
	@Test
	void createFailure_notFoundMember() throws Exception {
		//given
		Long storageId = 1L;
		GroceryReq groceryReq = new GroceryReq(grocery.getName(), grocery.getGroceryType().name(), grocery.getQuantity(), grocery.getDescription(), grocery.getExpirationDate(), storageId);

		MockMultipartFile file1 = new MockMultipartFile("imageFiles", "test1.jpg", "image/jpeg", "Spring Framework" .getBytes());
		MockMultipartFile file2 = new MockMultipartFile("imageFiles", "test2.jpg", "image/jpeg", "Spring Framework" .getBytes());
		MockMultipartFile request = new MockMultipartFile("groceryReq", "groceryReq",
			"application/json",
			objectMapper.writeValueAsString(groceryReq).getBytes(StandardCharsets.UTF_8));

		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER)).when(
			groceryService).create(any(), any(), any());
		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/groceries")
				.file(file1)
				.file(file2)
				.file(request)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(groceryReq)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("M003"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("회원을 찾을 수 없습니다."))
			.andDo(print())
			.andDo(document("grocery/grocery-create-failure-not-found-member",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestParts(
					partWithName("imageFiles").description("식료품 이미지"),
					partWithName("groceryReq").description("식료품 등록 내용")
				),
				requestPartFields("groceryReq",
					fieldWithPath("name").description("식료품 이름"),
					fieldWithPath("groceryType").description("식료품 타입"),
					fieldWithPath("quantity").description("식료품 수량"),
					fieldWithPath("description").description("식료품 설명"),
					fieldWithPath("expirationDate").description("식료품 유통기한"),
					fieldWithPath("storageId").description("냉장고 저장소 ID")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("식료품 생성 실패 테스트 - 냉장고 저장소가 존재하지 않는 경우")
	@Test
	void createFailure_notFoundStorage() throws Exception {
		//given
		Long storageId = 1L;
		GroceryReq groceryReq = new GroceryReq(grocery.getName(), grocery.getGroceryType().name(), grocery.getQuantity(), grocery.getDescription(), grocery.getExpirationDate(), storageId);

		MockMultipartFile file1 = new MockMultipartFile("imageFiles", "test1.jpg", "image/jpeg", "Spring Framework" .getBytes());
		MockMultipartFile file2 = new MockMultipartFile("imageFiles", "test2.jpg", "image/jpeg", "Spring Framework" .getBytes());
		MockMultipartFile request = new MockMultipartFile("groceryReq", "groceryReq",
			"application/json",
			objectMapper.writeValueAsString(groceryReq).getBytes(StandardCharsets.UTF_8));

		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_STORAGE)).when(
			groceryService).create(any(), any(), any());
		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/groceries")
				.file(file1)
				.file(file2)
				.file(request)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(groceryReq)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("S002"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("냉장고 저장소가 존재하지 않습니다."))
			.andDo(print())
			.andDo(document("grocery/grocery-create-failure-not-found-storage",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestParts(
					partWithName("imageFiles").description("식료품 이미지"),
					partWithName("groceryReq").description("식료품 등록 내용")
				),
				requestPartFields("groceryReq",
					fieldWithPath("name").description("식료품 이름"),
					fieldWithPath("groceryType").description("식료품 타입"),
					fieldWithPath("quantity").description("식료품 수량"),
					fieldWithPath("description").description("식료품 설명"),
					fieldWithPath("expirationDate").description("식료품 유통기한"),
					fieldWithPath("storageId").description("냉장고 저장소 ID")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("식료품 단건 조회 성공 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long groceryId = 1L;
		Long storageId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		ImageRes imageRes1 = new ImageRes(groceryImage1.getFileName(), groceryImage1.getPath());
		ImageRes imageRes2 = new ImageRes(groceryImage2.getFileName(), groceryImage2.getPath());

		GroceryRes groceryRes = new GroceryRes(groceryId, grocery.getName(), grocery.getGroceryType().name(), grocery.getQuantity(), grocery.getDescription(), grocery.getExpirationDate(), storageId, grocery.getStorage().getName(), grocery.getGroceryExpirationType().name(), createdAt, updatedAt, List.of(imageRes1, imageRes2));

		when(groceryService.findById(groceryId)).thenReturn(groceryRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/groceries/{id}", groceryId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.groceryId").value(groceryRes.groceryId()))
			.andExpect(jsonPath("$.groceryName").value(groceryRes.groceryName()))
			.andExpect(jsonPath("$.groceryType").value(groceryRes.groceryType()))
			.andExpect(jsonPath("$.quantity").value(groceryRes.quantity()))
			.andExpect(jsonPath("$.description").value(groceryRes.description()))
			.andExpect(jsonPath("$.expirationDate").value(formatLocalDate(groceryRes.expirationDate())))
			.andExpect(jsonPath("$.storageId").value(groceryRes.storageId()))
			.andExpect(jsonPath("$.storageName").value(groceryRes.storageName()))
			.andExpect(jsonPath("$.groceryExpirationType").value(groceryRes.groceryExpirationType()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(groceryRes.createdAt())))
//			.andExpect(jsonPath("$.updatedAt").value(substringLocalDateTime(groceryRes.updatedAt())))
			.andExpect(jsonPath("$.images[0].fileName").value(groceryRes.images().get(0).fileName()))
			.andExpect(jsonPath("$.images[0].path").value(groceryRes.images().get(0).path()))
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
					fieldWithPath("createdAt").type(STRING).description("식료품 생성 날짜"),
					fieldWithPath("updatedAt").type(STRING).description("식료품 수정 날짜"),
					fieldWithPath("images[].fileName").type(STRING).description("식료품 이미지 파일 이름"),
					fieldWithPath("images[].path").type(STRING).description("식료품 이미지 파일 경로")
				)
			));
	}

	@DisplayName("식료품 단건 조회 실패 테스트 - 식료품이 존재하지 않는 경우")
	@Test
	void findByIdFailure_notFoundGrocery() throws Exception {
		//given
		Long groceryId = 1L;

		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_GROCERY)).when(
			groceryService).findById(groceryId);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/groceries/{id}", groceryId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("G002"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("식료품이 존재하지 않습니다."))
			.andDo(print())
			.andDo(document("grocery/grocery-find-by-id-failure-not-found-grocery",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("식료품 ID")),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("특정 냉장고 저장소의 모든 식료품 조회 성공 테스트")
	@Test
	void findAllByStorageId() throws Exception {
		//given
		Long storageId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		Grocery grocery2 = Grocery.builder()
			.storage(storage)
			.name("햄")
			.groceryType(GroceryType.MEAT)
			.quantity("2개")
			.description("김밥 재료")
			.expirationDate(LocalDate.now().plusDays(7))
			.build();

		grocery2.addGroceryImage(groceryImage1);
		grocery2.addGroceryImage(groceryImage2);

		ImageRes imageRes1 = new ImageRes(groceryImage1.getFileName(), groceryImage1.getPath());
		ImageRes imageRes2 = new ImageRes(groceryImage2.getFileName(), groceryImage2.getPath());

		GroceryRes groceryRes1 = new GroceryRes(1L, grocery.getName(), grocery.getGroceryType().name(), grocery.getQuantity(), grocery.getDescription(), grocery.getExpirationDate(), storageId, grocery.getStorage().getName(), grocery.getGroceryExpirationType().name(), createdAt, updatedAt, List.of(imageRes1, imageRes2));
		GroceryRes groceryRes2 = new GroceryRes(2L, grocery2.getName(), grocery2.getGroceryType().name(), grocery2.getQuantity(), grocery2.getDescription(), grocery.getExpirationDate(), storageId, grocery2.getStorage().getName(), grocery2.getGroceryExpirationType().name(), createdAt, updatedAt, List.of(imageRes1, imageRes2));

		GroceriesRes groceriesRes = new GroceriesRes(List.of(groceryRes1, groceryRes2), false);

		when(groceryService.findAllByStorageId(eq(storageId), any(), any(), any(), any(), any(), any(), any(), any(), anyString())).thenReturn(groceriesRes);

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
//			.andExpect(jsonPath("$.groceries[0].createdAt").value(substringLocalDateTime(groceryRes1.createdAt())))
//			.andExpect(jsonPath("$.groceries[0].updatedAt").value(substringLocalDateTime(groceryRes1.updatedAt())))
			.andExpect(jsonPath("$.groceries[0].images[0].fileName").value(groceryRes1.images().get(0).fileName()))
			.andExpect(jsonPath("$.groceries[0].images[0].path").value(groceryRes1.images().get(0).path()))
			.andExpect(jsonPath("$.hasNext").value(groceriesRes.hasNext()))
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
					fieldWithPath("groceries[].createdAt").type(STRING).description("식료품 생성 날짜"),
					fieldWithPath("groceries[].updatedAt").type(STRING).description("식료품 수정 날짜"),
					fieldWithPath("groceries[].images[].fileName").type(STRING).description("식료품 이미지 파일 이름"),
					fieldWithPath("groceries[].images[].path").type(STRING).description("식료품 이미지 파일 경로"),
					fieldWithPath("hasNext").type(BOOLEAN).description("다음 페이지(스크롤) 데이터 존재 유무")
				)
			));
	}

	@DisplayName("특정 냉장고 저장소의 모든 식료품 조회 실패 테스트 - 유효하지 않거나 허용되지 않은 식료품 정렬 타입인 경우")
	@Test
	void findAllByStorageIdFailure_invalidGrocerySortType() throws Exception {
		//given
		Long storageId = 1L;

		doThrow(new InvalidValueException(ErrorCode.INVALID_GROCERY_SORT_TYPE)).when(
			groceryService).findAllByStorageId(eq(storageId), any(), any(), any(), any(), any(), any(), any(), any(), anyString());

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/groceries/storages/{storageId}", storageId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("GOO4"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("유효하지 않거나 허용되지 않는 식료품 정렬 타입입니다."))
			.andDo(print())
			.andDo(document("grocery/grocery-find-all-by-storage-id-failure-invalid-grocery-sort-type",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(parameterWithName("storageId").description("냉장고 저장소 ID")),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("특정 냉장고 저장소의 모든 식료품 조회 실패 테스트 - 유효하지 않은 식료품 타입인 경우")
	@Test
	void findAllByStorageIdFailure_invalidGroceryType() throws Exception {
		//given
		Long storageId = 1L;

		doThrow(new InvalidValueException(ErrorCode.INVALID_GROCERY_TYPE)).when(
			groceryService).findAllByStorageId(eq(storageId), any(), any(), any(), any(), any(), any(), any(), any(), anyString());

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/groceries/storages/{storageId}", storageId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("G001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("유효하지 않은 식료품 타입입니다."))
			.andDo(print())
			.andDo(document("grocery/grocery-find-all-by-storage-id-failure-invalid-grocery-type",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(parameterWithName("storageId").description("냉장고 저장소 ID")),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("식료품 수정 성공 테스트")
	@Test
	void update() throws Exception {
		//given
		Long groceryId = 1L;
		Long storageId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		ImageRes imageRes1 = new ImageRes(groceryImage1.getFileName(), groceryImage1.getPath());
		ImageRes imageRes2 = new ImageRes(groceryImage2.getFileName(), groceryImage2.getPath());

		GroceryReq groceryReq = new GroceryReq("식료품 수정", GroceryType.SNACKS.name(), "10개", "수정", LocalDate.now().plusDays(2), storageId);
		GroceryRes groceryRes = new GroceryRes(groceryId, groceryReq.name(), groceryReq.groceryType(), groceryReq.quantity(), groceryReq.description(), groceryReq.expirationDate(), storageId, grocery.getStorage().getName(), grocery.getGroceryExpirationType().name(), createdAt, updatedAt, List.of(imageRes1, imageRes2));

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
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(groceryRes.createdAt())))
//			.andExpect(jsonPath("$.updatedAt").value(substringLocalDateTime(groceryRes.updatedAt())))
			.andExpect(jsonPath("$.images[0].fileName").value(groceryRes.images().get(0).fileName()))
			.andExpect(jsonPath("$.images[0].path").value(groceryRes.images().get(0).path()))
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
					fieldWithPath("createdAt").type(STRING).description("식료품 생성 날짜"),
					fieldWithPath("updatedAt").type(STRING).description("식료품 수정 날짜"),
					fieldWithPath("images[].fileName").type(STRING).description("식료품 이미지 파일 이름"),
					fieldWithPath("images[].path").type(STRING).description("식료품 이미지 파일 경로")
				)
			));
	}

	@DisplayName("식료품 삭제 성공 테스트")
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
