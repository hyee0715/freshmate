package com.icebox.freshmate.domain.storage.presentation;

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
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
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
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.storage.application.StorageService;
import com.icebox.freshmate.domain.storage.application.dto.request.StorageCreateReq;
import com.icebox.freshmate.domain.storage.application.dto.request.StorageUpdateReq;
import com.icebox.freshmate.domain.storage.application.dto.response.StorageRes;
import com.icebox.freshmate.domain.storage.application.dto.response.StoragesRes;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageType;
import com.icebox.freshmate.global.TestPrincipalDetailsService;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;
import com.icebox.freshmate.global.error.exception.InvalidValueException;

@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(value = {StorageController.class})
@AutoConfigureRestDocs
class StorageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private StorageService storageService;

	private final TestPrincipalDetailsService testUserDetailsService = new TestPrincipalDetailsService();
	private PrincipalDetails principalDetails;

	private Member member;
	private Refrigerator refrigerator;
	private Storage storage;

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
	}

	@DisplayName("냉장고 저장소 생성 성공 테스트")
	@Test
	void create() throws Exception {
		//given
		Long refrigeratorId = 1L;
		Long storageId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		StorageCreateReq storageCreateReq = new StorageCreateReq(storage.getName(), storage.getStorageType().name(), refrigeratorId);
		StorageRes storageRes = new StorageRes(storageId, storage.getName(), storage.getStorageType().name(), refrigeratorId, refrigerator.getName(), createdAt, updatedAt);

		when(storageService.create(any(StorageCreateReq.class), any(String.class))).thenReturn(storageRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/storages")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(storageCreateReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(storageRes)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.storageId").value(storageRes.storageId()))
			.andExpect(jsonPath("$.storageName").value(storageRes.storageName()))
			.andExpect(jsonPath("$.storageType").value(storageRes.storageType()))
			.andExpect(jsonPath("$.refrigeratorId").value(storageRes.refrigeratorId()))
			.andExpect(jsonPath("$.refrigeratorName").value(storageRes.refrigeratorName()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(storageRes.createdAt())))
//			.andExpect(jsonPath("$.updatedAt").value(substringLocalDateTime(storageRes.updatedAt())))
			.andDo(print())
			.andDo(document("storage/storage-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("name").description("냉장고 저장소 이름"),
					fieldWithPath("storageType").description("냉장고 저장소 타입"),
					fieldWithPath("refrigeratorId").description("냉장고 ID")
				),
				responseFields(
					fieldWithPath("storageId").type(NUMBER).description("냉장고 저장소 ID"),
					fieldWithPath("storageName").type(STRING).description("냉장고 저장소 이름"),
					fieldWithPath("storageType").type(STRING).description("냉장고 저장소 타입"),
					fieldWithPath("refrigeratorId").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("refrigeratorName").type(STRING).description("냉장고 이름"),
					fieldWithPath("createdAt").type(STRING).description("냉장고 저장소 생성 날짜"),
					fieldWithPath("updatedAt").type(STRING).description("냉장고 저장소 수정 날짜")
				)
			));
	}

	@DisplayName("냉장고 저장소 생성 실패 테스트 - 회원이 존재하지 않는 경우")
	@Test
	void createFailure_notFoundMember() throws Exception {
		//given
		Long refrigeratorId = 1L;
		StorageCreateReq storageCreateReq = new StorageCreateReq(storage.getName(), storage.getStorageType().name(), refrigeratorId);

		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER)).when(storageService).create(eq(storageCreateReq), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/storages")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(storageCreateReq)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("M003"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("회원을 찾을 수 없습니다."))
			.andDo(print())
			.andDo(document("storage/storage-create-failure-not-found-member",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("name").description("냉장고 저장소 이름"),
					fieldWithPath("storageType").description("냉장고 저장소 타입"),
					fieldWithPath("refrigeratorId").description("냉장고 ID")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("냉장고 저장소 단건 조회 성공 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long refrigeratorId = 1L;
		Long storageId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		StorageRes storageRes = new StorageRes(storageId, storage.getName(), storage.getStorageType().name(), refrigeratorId, refrigerator.getName(), createdAt, updatedAt);

		when(storageService.findById(storageId))
			.thenReturn(storageRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/storages/{id}", storageId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(storageRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.storageId").value(storageId))
			.andExpect(jsonPath("$.storageName").value(storage.getName()))
			.andExpect(jsonPath("$.storageType").value(storage.getStorageType().name()))
			.andExpect(jsonPath("$.refrigeratorId").value(refrigeratorId))
			.andExpect(jsonPath("$.refrigeratorName").value(refrigerator.getName()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(storageRes.createdAt())))
//			.andExpect(jsonPath("$.updatedAt").value(substringLocalDateTime(storageRes.updatedAt())))
			.andDo(print())
			.andDo(document("storage/storage-find-by-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("냉장고 저장소 ID")),
				responseFields(
					fieldWithPath("storageId").type(NUMBER).description("냉장고 저장소 ID"),
					fieldWithPath("storageName").type(STRING).description("냉장고 저장소 이름"),
					fieldWithPath("storageType").type(STRING).description("냉장고 저장소 타입"),
					fieldWithPath("refrigeratorId").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("refrigeratorName").type(STRING).description("냉장고 이름"),
					fieldWithPath("createdAt").type(STRING).description("냉장고 저장소 생성 날짜"),
					fieldWithPath("updatedAt").type(STRING).description("냉장고 저장소 수정 날짜")
				)
			));
	}

	@DisplayName("냉장고 저장소 단건 조회 실패 테스트 - 냉장고 저장소가 존재하지 않는 경우")
	@Test
	void findByIdFailure_notFoundStorage() throws Exception {
		//given
		Long storageId = 1L;

		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_STORAGE)).when(
			storageService).findById(storageId);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/storages/{id}", storageId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("S002"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("냉장고 저장소가 존재하지 않습니다."))
			.andDo(print())
			.andDo(document("storage/storage-find-by-id-failure-not-found-storage",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("냉장고 저장소 ID")),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("특정 냉장고의 모든 냉장고 저장소 조회 성공 테스트")
	@Test
	void findAllByRefrigeratorId() throws Exception {
		//given
		Long refrigeratorId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		StorageRes storageRes = new StorageRes(1L, storage.getName(), storage.getStorageType().name(), refrigeratorId, refrigerator.getName(), createdAt, updatedAt);

		createdAt = LocalDateTime.now().plusMinutes(1);
		updatedAt = createdAt;

		Storage storage2 = Storage.builder()
			.name("냉동실")
			.storageType(StorageType.FREEZER)
			.refrigerator(refrigerator)
			.build();

		StorageRes storageRes2 = new StorageRes(2L, storage2.getName(), storage2.getStorageType().name(), refrigeratorId, refrigerator.getName(), createdAt, updatedAt);

		StoragesRes storagesRes = new StoragesRes(List.of(storageRes, storageRes2), false);

		when(storageService.findAllByRefrigeratorId(eq(refrigeratorId), any(), any(), any(), any(), any(), anyString())).thenReturn(storagesRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/storages/refrigerators/{refrigeratorId}", refrigeratorId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(storagesRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.storages", hasSize(2)))
			.andExpect(jsonPath("$.storages[0].storageId").value(storageRes.storageId()))
			.andExpect(jsonPath("$.storages[0].storageName").value(storageRes.storageName()))
			.andExpect(jsonPath("$.storages[0].storageType").value(storageRes.storageType()))
			.andExpect(jsonPath("$.storages[0].refrigeratorId").value(storageRes.refrigeratorId()))
			.andExpect(jsonPath("$.storages[0].refrigeratorName").value(storageRes.refrigeratorName()))
//			.andExpect(jsonPath("$.storages[0].createdAt").value(substringLocalDateTime(storageRes.createdAt())))
//			.andExpect(jsonPath("$.storages[0].updatedAt").value(substringLocalDateTime(storageRes.updatedAt())))
			.andExpect(jsonPath("$.hasNext").value(storagesRes.hasNext()))
			.andDo(print())
			.andDo(document("storage/storage-find-all-by-refrigerator-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(parameterWithName("refrigeratorId").description("냉장고 ID")),
				responseFields(
					fieldWithPath("storages").type(ARRAY).description("냉장고 저장소 배열"),
					fieldWithPath("storages[].storageId").type(NUMBER).description("냉장고 저장소 ID"),
					fieldWithPath("storages[].storageName").type(STRING).description("냉장고 저장소 이름"),
					fieldWithPath("storages[].storageType").type(STRING).description("냉장고 저장소 타입"),
					fieldWithPath("storages[].refrigeratorId").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("storages[].refrigeratorName").type(STRING).description("냉장고 이름"),
					fieldWithPath("storages[].createdAt").type(STRING).description("냉장고 저장소 생성 날짜"),
					fieldWithPath("storages[].updatedAt").type(STRING).description("냉장고 저장소 수정 날짜"),
					fieldWithPath("hasNext").type(BOOLEAN).description("다음 페이지(스크롤) 데이터 존재 유무")
				)
			));
	}

	@DisplayName("특정 냉장고의 모든 냉장고 저장소 조회 실패 테스트 - 냉장고가 존재하지 않는 경우")
	@Test
	void findAllByRefrigeratorIdFailure_notFoundRefrigerator() throws Exception {
		//given
		Long refrigeratorId = 1L;

		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_REFRIGERATOR)).when(
			storageService).findAllByRefrigeratorId(eq(refrigeratorId), any(), any(), any(), any(), any(), anyString());

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/storages/refrigerators/{refrigeratorId}", refrigeratorId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("R001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("냉장고가 존재하지 않습니다."))
			.andDo(print())
			.andDo(document("storage/storage-find-all-by-refrigerator-id-failure-not-found-refrigerator",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(parameterWithName("refrigeratorId").description("냉장고 ID")),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("특정 냉장고의 모든 냉장고 저장소 조회 실패 테스트 - 유효하지 않거나 허용되지 않는 냉장고 저장소 정렬 타입인 경우")
	@Test
	void findAllByRefrigeratorIdFailure_invalidStorageSortType() throws Exception {
		//given
		Long refrigeratorId = 1L;

		doThrow(new InvalidValueException(ErrorCode.INVALID_STORAGE_SORT_TYPE)).when(
			storageService).findAllByRefrigeratorId(eq(refrigeratorId), any(), any(), any(), any(), any(), anyString());

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/storages/refrigerators/{refrigeratorId}", refrigeratorId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("S003"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("유효하지 않거나 허용되지 않는 냉장고 저장소 정렬 타입입니다."))
			.andDo(print())
			.andDo(document("storage/storage-find-all-by-refrigerator-id-failure-invalid-storage-sort-type",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(parameterWithName("refrigeratorId").description("냉장고 ID")),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("냉장고 저장소 수정 성공 테스트")
	@Test
	void update() throws Exception {
		//given
		Long storageId = 1L;
		Long refrigeratorId = 1L;
		String updateStorageName = "냉동실 수정";
		String updateStorageType = StorageType.FREEZER.name();

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		StorageUpdateReq storageUpdateReq = new StorageUpdateReq(updateStorageName, updateStorageType);
		StorageRes storageRes = new StorageRes(storageId, updateStorageName, updateStorageType, refrigeratorId, refrigerator.getName(), createdAt, updatedAt);

		when(storageService.update(any(Long.class), any(StorageUpdateReq.class), any(String.class))).thenReturn(storageRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/storages/{id}", storageId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(storageUpdateReq)))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(storageRes)))
			.andExpect(jsonPath("$.storageId").value(storageRes.storageId()))
			.andExpect(jsonPath("$.storageName").value(storageRes.storageName()))
			.andExpect(jsonPath("$.storageType").value(storageRes.storageType()))
			.andExpect(jsonPath("$.refrigeratorId").value(storageRes.refrigeratorId()))
			.andExpect(jsonPath("$.refrigeratorName").value(storageRes.refrigeratorName()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(storageRes.createdAt())))
//			.andExpect(jsonPath("$.updatedAt").value(substringLocalDateTime(storageRes.updatedAt())))
			.andDo(print())
			.andDo(document("storage/storage-update",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(
					parameterWithName("id").description("냉장고 저장소 ID")
				),
				requestFields(
					fieldWithPath("name").description("수정할 냉장고 저장소 이름"),
					fieldWithPath("storageType").description("수정할 냉장고 타입")
				),
				responseFields(
					fieldWithPath("storageId").type(NUMBER).description("냉장고 저장소 ID"),
					fieldWithPath("storageName").type(STRING).description("수정된 냉장고 저장소 이름"),
					fieldWithPath("storageType").type(STRING).description("수정된 냉장고 저장소 타입"),
					fieldWithPath("refrigeratorId").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("refrigeratorName").type(STRING).description("냉장고 이름"),
					fieldWithPath("createdAt").type(STRING).description("냉장고 저장소 생성 날짜"),
					fieldWithPath("updatedAt").type(STRING).description("냉장고 저장소 수정 날짜")
				)
			));
	}

	@DisplayName("냉장고 저장소 수정 실패 테스트 - 냉장고 저장소 타입이 유효하지 않은 경우")
	@Test
	void updateFailure_invalidStorageType() throws Exception {
		//given
		Long storageId = 1L;
		String updateStorageName = "냉동실 수정";
		String updateStorageType = "fakeFreezer";

		StorageUpdateReq storageUpdateReq = new StorageUpdateReq(updateStorageName, updateStorageType);

		doThrow(new InvalidValueException(ErrorCode.INVALID_STORAGE_TYPE)).when(
			storageService).update(eq(storageId), eq(storageUpdateReq), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/storages/{id}", storageId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(storageUpdateReq)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("S001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("유효하지 않은 냉장고 저장소 타입입니다."))
			.andDo(print())
			.andDo(document("storage/storage-update-failure-invalid-storage-type",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(
					parameterWithName("id").description("냉장고 저장소 ID")
				),
				requestFields(
					fieldWithPath("name").description("수정할 냉장고 저장소 이름"),
					fieldWithPath("storageType").description("수정할 냉장고 타입")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("냉장고 저장소 삭제 성공 테스트")
	@Test
	void delete() throws Exception {
		//given
		Long storageId = 1L;

		doNothing().when(storageService).delete(any(Long.class), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/storages/{id}", storageId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andDo(document("storage/storage-delete",
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
