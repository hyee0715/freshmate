package com.icebox.freshmate.domain.grocerybucket.presentation;

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
import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.grocerybucket.application.GroceryBucketService;
import com.icebox.freshmate.domain.grocerybucket.application.dto.request.GroceryBucketReq;
import com.icebox.freshmate.domain.grocerybucket.application.dto.response.GroceryBucketRes;
import com.icebox.freshmate.domain.grocerybucket.application.dto.response.GroceryBucketsRes;
import com.icebox.freshmate.domain.grocerybucket.domain.GroceryBucket;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.global.TestPrincipalDetailsService;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(value = {GroceryBucketController.class})
@AutoConfigureRestDocs
class GroceryBucketControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private GroceryBucketService groceryBucketService;

	private final TestPrincipalDetailsService testUserDetailsService = new TestPrincipalDetailsService();
	private PrincipalDetails principalDetails;

	private Member member;
	private GroceryBucket groceryBucket;

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

		groceryBucket = GroceryBucket.builder()
			.member(member)
			.groceryName("양배추")
			.groceryType(GroceryType.VEGETABLES)
			.groceryDescription("필수 식재료")
			.build();
	}

	@DisplayName("즐겨 찾는 식료품 생성 성공 테스트")
	@Test
	void create() throws Exception {
		//given
		Long groceryBucketId = 1L;
		Long memberId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		GroceryBucketReq groceryBucketReq = new GroceryBucketReq(groceryBucket.getGroceryName(), groceryBucket.getGroceryType().name(), groceryBucket.getGroceryDescription());
		GroceryBucketRes groceryBucketRes = new GroceryBucketRes(groceryBucketId, memberId, member.getNickName(), groceryBucket.getGroceryName(), groceryBucket.getGroceryType().name(), groceryBucket.getGroceryDescription(), createdAt, updatedAt);

		when(groceryBucketService.create(any(GroceryBucketReq.class), any(String.class))).thenReturn(groceryBucketRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/grocery-buckets")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(groceryBucketReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(groceryBucketRes)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.groceryBucketId").value(groceryBucketRes.groceryBucketId()))
			.andExpect(jsonPath("$.memberId").value(groceryBucketRes.memberId()))
			.andExpect(jsonPath("$.memberNickName").value(groceryBucketRes.memberNickName()))
			.andExpect(jsonPath("$.groceryName").value(groceryBucketRes.groceryName()))
			.andExpect(jsonPath("$.groceryType").value(groceryBucketRes.groceryType()))
			.andExpect(jsonPath("$.groceryDescription").value(groceryBucketRes.groceryDescription()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(groceryBucketRes.createdAt())))
//			.andExpect(jsonPath("$.updatedAt").value(substringLocalDateTime(groceryBucketRes.updatedAt())))
			.andDo(print())
			.andDo(document("grocery-bucket/grocery-bucket-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("groceryName").description("즐겨 찾는 식료품 목록에 등록할 식료품 이름"),
					fieldWithPath("groceryType").description("즐겨 찾는 식료품 목록에 등록할 식료품 타입"),
					fieldWithPath("groceryDescription").description("즐겨 찾는 식료품 목록에 등록할 식료품 설명")
				),
				responseFields(
					fieldWithPath("groceryBucketId").type(NUMBER).description("즐겨 찾는 식료품 ID"),
					fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("memberNickName").type(STRING).description("회원 닉네임"),
					fieldWithPath("groceryName").type(STRING).description("식료품 이름"),
					fieldWithPath("groceryType").type(STRING).description("식료품 타입"),
					fieldWithPath("groceryDescription").type(STRING).description("식료품 설명"),
					fieldWithPath("createdAt").type(STRING).description("즐겨 찾는 생성 날짜"),
					fieldWithPath("updatedAt").type(STRING).description("즐겨 찾는 수정 날짜")
				)
			));
	}

	@DisplayName("즐겨 찾는 식료품 생성 실패 테스트 - 회원이 존재하지 않는 경우")
	@Test
	void createFailure_notFoundMember() throws Exception {
		//given
		GroceryBucketReq groceryBucketReq = new GroceryBucketReq(groceryBucket.getGroceryName(), groceryBucket.getGroceryType().name(), groceryBucket.getGroceryDescription());

		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER)).when(
			groceryBucketService).create(eq(groceryBucketReq), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/grocery-buckets")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(groceryBucketReq)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("M003"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("회원을 찾을 수 없습니다."))
			.andDo(print())
			.andDo(document("grocery-bucket/grocery-bucket-create-failure-not-found-member",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("groceryName").description("즐겨 찾는 식료품 목록에 등록할 식료품 이름"),
					fieldWithPath("groceryType").description("즐겨 찾는 식료품 목록에 등록할 식료품 타입"),
					fieldWithPath("groceryDescription").description("즐겨 찾는 식료품 목록에 등록할 식료품 설명")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("즐겨 찾는 식료품 단건 조회 성공 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long groceryBucketId = 1L;
		Long memberId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		GroceryBucketRes groceryBucketRes = new GroceryBucketRes(groceryBucketId, memberId, member.getNickName(), groceryBucket.getGroceryName(), groceryBucket.getGroceryType().name(), groceryBucket.getGroceryDescription(), createdAt, updatedAt);

		when(groceryBucketService.findById(any(Long.class))).thenReturn(groceryBucketRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/grocery-buckets/{id}", groceryBucketId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.groceryBucketId").value(groceryBucketRes.groceryBucketId()))
			.andExpect(jsonPath("$.memberId").value(groceryBucketRes.memberId()))
			.andExpect(jsonPath("$.memberNickName").value(groceryBucketRes.memberNickName()))
			.andExpect(jsonPath("$.groceryName").value(groceryBucketRes.groceryName()))
			.andExpect(jsonPath("$.groceryType").value(groceryBucketRes.groceryType()))
			.andExpect(jsonPath("$.groceryDescription").value(groceryBucketRes.groceryDescription()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(groceryBucketRes.createdAt())))
//			.andExpect(jsonPath("$.updatedAt").value(substringLocalDateTime(groceryBucketRes.updatedAt())))
			.andDo(print())
			.andDo(document("grocery-bucket/grocery-bucket-find-by-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("즐겨 찾는 식료품 ID")),
				responseFields(
					fieldWithPath("groceryBucketId").type(NUMBER).description("즐겨 찾는 식료품 ID"),
					fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("memberNickName").type(STRING).description("회원 닉네임"),
					fieldWithPath("groceryName").type(STRING).description("식료품 이름"),
					fieldWithPath("groceryType").type(STRING).description("식료품 타입"),
					fieldWithPath("groceryDescription").type(STRING).description("식료품 설명"),
					fieldWithPath("createdAt").type(STRING).description("즐겨 찾는 생성 날짜"),
					fieldWithPath("updatedAt").type(STRING).description("즐겨 찾는 수정 날짜")
				)
			));
	}

	@DisplayName("즐겨 찾는 식료품 단건 조회 실패 테스트 - 즐겨 찾는 식료품이 존재하지 않는 경우")
	@Test
	void findByIdFailure_notFoundGroceryBucket() throws Exception {
		//given
		Long groceryBucketId = 1L;

		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_GROCERY_BUCKET)).when(
			groceryBucketService).findById(groceryBucketId);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/grocery-buckets/{id}", groceryBucketId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("GB001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("즐겨 찾는 식료품이 존재하지 않습니다."))
			.andDo(print())
			.andDo(document("grocery-bucket/grocery-bucket-find-by-id-failure-not-found-grocery-bucket",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("즐겨 찾는 식료품 ID")),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("사용자의 즐겨 찾는 식료품 목록 조회 성공 테스트")
	@Test
	void findAll() throws Exception {
		//given
		Long memberId = 1L;

		GroceryBucket groceryBucket2 = GroceryBucket.builder()
			.member(member)
			.groceryName("배추")
			.groceryType(GroceryType.VEGETABLES)
			.groceryDescription("김장용")
			.build();

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		GroceryBucketRes groceryBucketRes1 = new GroceryBucketRes(1L, memberId, member.getNickName(), groceryBucket.getGroceryName(), groceryBucket.getGroceryType().name(), groceryBucket.getGroceryDescription(), createdAt, updatedAt);
		GroceryBucketRes groceryBucketRes2 = new GroceryBucketRes(2L, memberId, member.getNickName(), groceryBucket2.getGroceryName(), groceryBucket2.getGroceryType().name(), groceryBucket2.getGroceryDescription(), createdAt.plusMinutes(1), updatedAt.plusMinutes(1));

		GroceryBucketsRes groceryBucketsRes = new GroceryBucketsRes(List.of(groceryBucketRes1, groceryBucketRes2), false);

		when(groceryBucketService.findAll(any(), any(), any(), any(), eq(member.getUsername()))).thenReturn(groceryBucketsRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/grocery-buckets")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(groceryBucketsRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.groceryBuckets", hasSize(2)))
			.andExpect(jsonPath("$.groceryBuckets[0].groceryBucketId").value(groceryBucketsRes.groceryBuckets().get(0).groceryBucketId()))
			.andExpect(jsonPath("$.groceryBuckets[0].memberId").value(groceryBucketsRes.groceryBuckets().get(0).memberId()))
			.andExpect(jsonPath("$.groceryBuckets[0].memberNickName").value(groceryBucketsRes.groceryBuckets().get(0).memberNickName()))
			.andExpect(jsonPath("$.groceryBuckets[0].groceryName").value(groceryBucketsRes.groceryBuckets().get(0).groceryName()))
			.andExpect(jsonPath("$.groceryBuckets[0].groceryType").value(groceryBucketsRes.groceryBuckets().get(0).groceryType()))
			.andExpect(jsonPath("$.groceryBuckets[0].groceryDescription").value(groceryBucketsRes.groceryBuckets().get(0).groceryDescription()))
//			.andExpect(jsonPath("$.groceryBuckets[0].createdAt").value(substringLocalDateTime(groceryBucketsRes.groceryBuckets().get(0).createdAt())))
//			.andExpect(jsonPath("$.groceryBuckets[0].updatedAt").value(substringLocalDateTime(groceryBucketsRes.groceryBuckets().get(0).updatedAt())))
			.andExpect(jsonPath("$.hasNext").value(groceryBucketsRes.hasNext()))
			.andDo(print())
			.andDo(document("grocery-bucket/grocery-bucket-find-all",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				responseFields(
					fieldWithPath("groceryBuckets").type(ARRAY).description("즐겨 찾는 식료품 배열"),
					fieldWithPath("groceryBuckets[].groceryBucketId").type(NUMBER).description("즐겨 찾는 식료품 ID"),
					fieldWithPath("groceryBuckets[].memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("groceryBuckets[].memberNickName").type(STRING).description("회원 닉네임"),
					fieldWithPath("groceryBuckets[].groceryName").type(STRING).description("즐겨 찾는 식료품 이름"),
					fieldWithPath("groceryBuckets[].groceryType").type(STRING).description("즐겨 찾는 식료품 타입"),
					fieldWithPath("groceryBuckets[].groceryDescription").type(STRING).description("즐겨 찾는 식료품 설명"),
					fieldWithPath("groceryBuckets[].createdAt").type(STRING).description("즐겨 찾는 식료품 생성 날짜"),
					fieldWithPath("groceryBuckets[].updatedAt").type(STRING).description("즐겨 찾는 식료품 수정 날짜"),
					fieldWithPath("hasNext").type(BOOLEAN).description("다음 페이지(스크롤) 데이터 존재 유무")
				)
			));
	}

	@DisplayName("즐겨 찾는 식료품 수정 성공 테스트")
	@Test
	void update() throws Exception {
		//given
		Long groceryBucketId = 1L;
		Long memberId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		GroceryBucketReq groceryBucketReq = new GroceryBucketReq("식료품 이름 수정", GroceryType.SNACKS.name(), "설명 수정");
		GroceryBucketRes groceryBucketRes = new GroceryBucketRes(groceryBucketId, memberId, member.getNickName(), groceryBucket.getGroceryName(), groceryBucket.getGroceryType().name(), groceryBucket.getGroceryDescription(), createdAt, updatedAt);

		when(groceryBucketService.update(anyLong(), any(GroceryBucketReq.class), anyString())).thenReturn(groceryBucketRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/grocery-buckets/{id}", groceryBucketId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(groceryBucketReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(groceryBucketRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.groceryBucketId").value(groceryBucketRes.groceryBucketId()))
			.andExpect(jsonPath("$.memberId").value(groceryBucketRes.memberId()))
			.andExpect(jsonPath("$.memberNickName").value(groceryBucketRes.memberNickName()))
			.andExpect(jsonPath("$.groceryName").value(groceryBucketRes.groceryName()))
			.andExpect(jsonPath("$.groceryType").value(groceryBucketRes.groceryType()))
			.andExpect(jsonPath("$.groceryDescription").value(groceryBucketRes.groceryDescription()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(groceryBucketRes.createdAt())))
//			.andExpect(jsonPath("$.updatedAt").value(substringLocalDateTime(groceryBucketRes.updatedAt())))
			.andDo(print())
			.andDo(document("grocery-bucket/grocery-bucket-update",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("즐겨 찾는 식료품 ID")),
				requestFields(
					fieldWithPath("groceryName").description("수정할 식료품 이름"),
					fieldWithPath("groceryType").description("수정할 식료품 타입"),
					fieldWithPath("groceryDescription").description("수정할 식료품 설명")
				),
				responseFields(
					fieldWithPath("groceryBucketId").type(NUMBER).description("즐겨 찾는 식료품 ID"),
					fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("memberNickName").type(STRING).description("회원 닉네임"),
					fieldWithPath("groceryName").type(STRING).description("식료품 이름"),
					fieldWithPath("groceryType").type(STRING).description("식료품 타입"),
					fieldWithPath("groceryDescription").type(STRING).description("식료품 설명"),
					fieldWithPath("createdAt").type(STRING).description("즐겨 찾는 생성 날짜"),
					fieldWithPath("updatedAt").type(STRING).description("즐겨 찾는 수정 날짜")
				)
			));
	}

	@DisplayName("즐겨 찾는 식료품 삭제 성공 테스트")
	@Test
	void delete() throws Exception {
		//given
		Long groceryBucketId = 1L;

		doNothing().when(groceryBucketService).delete(anyLong(), anyString());

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/grocery-buckets/{id}", groceryBucketId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andDo(document("grocery-bucket/grocery-bucket-delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(
					parameterWithName("id").description("즐겨 찾는 식료품 ID")
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
