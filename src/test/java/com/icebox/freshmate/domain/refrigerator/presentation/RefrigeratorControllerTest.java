package com.icebox.freshmate.domain.refrigerator.presentation;

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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.icebox.freshmate.domain.refrigerator.application.RefrigeratorService;
import com.icebox.freshmate.domain.refrigerator.application.dto.request.RefrigeratorReq;
import com.icebox.freshmate.domain.refrigerator.application.dto.response.RefrigeratorRes;
import com.icebox.freshmate.domain.refrigerator.application.dto.response.RefrigeratorsRes;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.global.TestPrincipalDetailsService;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;
import com.icebox.freshmate.global.error.exception.InvalidValueException;

@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(value = {RefrigeratorController.class})
@AutoConfigureRestDocs
class RefrigeratorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private RefrigeratorService refrigeratorService;

	private final TestPrincipalDetailsService testUserDetailsService = new TestPrincipalDetailsService();
	private PrincipalDetails principalDetails;

	private Member member;
	private Refrigerator refrigerator;

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
	}

	@DisplayName("냉장고 생성 성공 테스트")
	@Test
	void create() throws Exception {
		//given
		Long refrigeratorId = 1L;
		Long memberId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		RefrigeratorReq refrigeratorReq = new RefrigeratorReq(refrigerator.getName());
		RefrigeratorRes refrigeratorRes = new RefrigeratorRes(refrigeratorId, refrigerator.getName(), memberId, refrigerator.getMember().getUsername(), refrigerator.getMember().getNickName(), createdAt, updatedAt);

		when(refrigeratorService.create(any(RefrigeratorReq.class), any(String.class))).thenReturn(refrigeratorRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/refrigerators")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(refrigeratorReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(refrigeratorRes)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.refrigeratorId").value(refrigeratorRes.refrigeratorId()))
			.andExpect(jsonPath("$.refrigeratorName").value(refrigeratorRes.refrigeratorName()))
			.andExpect(jsonPath("$.memberId").value(refrigeratorRes.memberId()))
			.andExpect(jsonPath("$.memberUsername").value(refrigeratorRes.memberUsername()))
			.andExpect(jsonPath("$.memberNickName").value(refrigeratorRes.memberNickName()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(refrigeratorRes.createdAt())))
//			.andExpect(jsonPath("$.updatedAt").value(substringLocalDateTime(refrigeratorRes.updatedAt())))
			.andDo(print())
			.andDo(document("refrigerator/refrigerator-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("name").description("냉장고 이름")
				),
				responseFields(
					fieldWithPath("refrigeratorId").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("refrigeratorName").type(STRING).description("냉장고 이름"),
					fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("memberUsername").type(STRING).description("회원 아이디"),
					fieldWithPath("memberNickName").type(STRING).description("회원 닉네임"),
					fieldWithPath("createdAt").type(STRING).description("냉장고 생성 날짜"),
					fieldWithPath("updatedAt").type(STRING).description("냉장고 수정 날짜")
				)
			));
	}

	@DisplayName("냉장고 생성 실패 테스트 - 회원이 존재하지 않는 경우")
	@Test
	void createFailure_notFoundMember() throws Exception {
		//given
		RefrigeratorReq refrigeratorReq = new RefrigeratorReq(refrigerator.getName());

		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER)).when(
			refrigeratorService).create(eq(refrigeratorReq), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/refrigerators")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(refrigeratorReq)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("M003"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("회원을 찾을 수 없습니다."))
			.andDo(print())
			.andDo(document("refrigerator/refrigerator-create-failure-not-found-member",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("name").description("냉장고 이름")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("냉장고 단건 조회 성공 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long refrigeratorId = 1L;
		Long memberId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		RefrigeratorRes refrigeratorRes = new RefrigeratorRes(refrigeratorId, refrigerator.getName(), memberId, refrigerator.getMember().getUsername(), refrigerator.getMember().getNickName(), createdAt, updatedAt);

		when(refrigeratorService.findById(anyLong())).thenReturn(refrigeratorRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/refrigerators/{id}", refrigeratorId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(refrigeratorRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.refrigeratorId").value(refrigeratorId))
			.andExpect(jsonPath("$.refrigeratorName").value(refrigerator.getName()))
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.memberUsername").value(member.getUsername()))
			.andExpect(jsonPath("$.memberNickName").value(member.getNickName()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(refrigeratorRes.createdAt())))
//			.andExpect(jsonPath("$.updatedAt").value(substringLocalDateTime(refrigeratorRes.updatedAt())))
			.andDo(print())
			.andDo(document("refrigerator/refrigerator-find-by-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("냉장고 ID")),
				responseFields(
					fieldWithPath("refrigeratorId").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("refrigeratorName").type(STRING).description("냉장고 이름"),
					fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("memberUsername").type(STRING).description("회원 아이디"),
					fieldWithPath("memberNickName").type(STRING).description("회원 닉네임"),
					fieldWithPath("createdAt").type(STRING).description("냉장고 생성 날짜"),
					fieldWithPath("updatedAt").type(STRING).description("냉장고 수정 날짜")
				)
			));
	}

	@DisplayName("냉장고 단건 조회 실패 테스트 - 냉장고가 존재하지 않는 경우")
	@Test
	void findByIdFailure_notFoundRefrigerator() throws Exception {
		//given
		Long refrigeratorId = 1L;

		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_REFRIGERATOR)).when(
			refrigeratorService).findById(refrigeratorId);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/refrigerators/{id}", refrigeratorId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("R001"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("냉장고가 존재하지 않습니다."))
			.andDo(print())
			.andDo(document("refrigerator/refrigerator-find-by-id-failure-not-found-refrigerator",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("냉장고 ID")),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("회원의 모든 냉장고 조회 성공 테스트")
	@Test
	void findAll() throws Exception {
		//given
		Refrigerator refrigerator2 = Refrigerator.builder()
			.name("우리 집 냉장고2")
			.build();

		Long memberId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		RefrigeratorRes refrigeratorRes1 = new RefrigeratorRes(1L, refrigerator.getName(), memberId, refrigerator.getMember().getUsername(), refrigerator.getMember().getNickName(), createdAt, updatedAt);
		RefrigeratorRes refrigeratorRes2 = new RefrigeratorRes(2L, refrigerator2.getName(), memberId, refrigerator.getMember().getUsername(), refrigerator.getMember().getNickName(), createdAt.plusMinutes(1), updatedAt.plusMinutes(1));
		RefrigeratorsRes refrigeratorsRes = new RefrigeratorsRes(List.of(refrigeratorRes1, refrigeratorRes2), false);

		when(refrigeratorService.findAll(any(String.class), any(), any(), any(), any())).thenReturn(refrigeratorsRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/refrigerators")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.refrigerators", hasSize(2)))
			.andExpect(jsonPath("$.refrigerators[0].refrigeratorId").value(refrigeratorRes1.refrigeratorId()))
			.andExpect(jsonPath("$.refrigerators[0].refrigeratorName").value(refrigeratorRes1.refrigeratorName()))
			.andExpect(jsonPath("$.refrigerators[0].memberId").value(refrigeratorRes1.memberId()))
			.andExpect(jsonPath("$.refrigerators[0].memberUsername").value(refrigeratorRes1.memberUsername()))
			.andExpect(jsonPath("$.refrigerators[0].memberNickName").value(refrigeratorRes1.memberNickName()))
//			.andExpect(jsonPath("$.refrigerators[0].createdAt").value(substringLocalDateTime(refrigeratorRes1.createdAt())))
//			.andExpect(jsonPath("$.refrigerators[0].updatedAt").value(substringLocalDateTime(refrigeratorRes1.updatedAt())))
			.andExpect(jsonPath("$.hasNext").value(refrigeratorsRes.hasNext()))
			.andDo(print())
			.andDo(document("refrigerator/refrigerator-find-all",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				responseFields(
					fieldWithPath("refrigerators").type(ARRAY).description("냉장고 배열"),
					fieldWithPath("refrigerators[].refrigeratorId").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("refrigerators[].refrigeratorName").type(STRING).description("냉장고 이름"),
					fieldWithPath("refrigerators[].memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("refrigerators[].memberUsername").type(STRING).description("회원 아이디"),
					fieldWithPath("refrigerators[].memberNickName").type(STRING).description("회원 닉네임"),
					fieldWithPath("refrigerators[].createdAt").type(STRING).description("냉장고 생성 날짜"),
					fieldWithPath("refrigerators[].updatedAt").type(STRING).description("냉장고 수정 날짜"),
					fieldWithPath("hasNext").type(BOOLEAN).description("다음 페이지(스크롤) 데이터 존재 유무")
				)
			));
	}

	@DisplayName("회원의 모든 냉장고 조회 실패 테스트 - 유효하지 않거나 허용되지 않는 냉장고 정렬 타입인 경우")
	@Test
	void findAllFailure_invalidRefrigeratorSortType() throws Exception {
		//given
		doThrow(new InvalidValueException(ErrorCode.INVALID_REFRIGERATOR_SORT_TYPE)).when(
			refrigeratorService).findAll(any(String.class), any(), any(), any(), any());

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/refrigerators")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("R002"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("유효하지 않거나 허용되지 않는 냉장고 정렬 타입입니다."))
			.andDo(print())
			.andDo(document("refrigerator/refrigerator-find-all-failure-invalid-refrigerator-sort-type",
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

	@DisplayName("냉장고 수정 성공 테스트")
	@Test
	void update() throws Exception {
		//given
		Long refrigeratorId = 1L;
		Long memberId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		RefrigeratorReq refrigeratorReq = new RefrigeratorReq(refrigerator.getName());
		RefrigeratorRes refrigeratorRes = new RefrigeratorRes(refrigeratorId, refrigerator.getName(), memberId, refrigerator.getMember().getUsername(), refrigerator.getMember().getNickName(), createdAt, updatedAt);

		when(refrigeratorService.update(any(Long.class), any(RefrigeratorReq.class), any(String.class)))
			.thenReturn(refrigeratorRes);

		//when
		//then
		mockMvc.perform(patch("/api/refrigerators/{id}", refrigeratorId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(refrigeratorReq)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.refrigeratorId").value(refrigeratorRes.refrigeratorId()))
			.andExpect(jsonPath("$.refrigeratorName").value(refrigeratorRes.refrigeratorName()))
			.andExpect(jsonPath("$.memberId").value(refrigeratorRes.memberId()))
			.andExpect(jsonPath("$.memberUsername").value(refrigeratorRes.memberUsername()))
			.andExpect(jsonPath("$.memberNickName").value(refrigeratorRes.memberNickName()))
//			.andExpect(jsonPath("$.createdAt").value(substringLocalDateTime(refrigeratorRes.createdAt())))
//			.andExpect(jsonPath("$.updatedAt").value(substringLocalDateTime(refrigeratorRes.updatedAt())))
			.andDo(print())
			.andDo(document("refrigerator/refrigerator-update",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(
					parameterWithName("id").description("냉장고 ID")
				),
				requestFields(
					fieldWithPath("name").description("수정할 냉장고 이름")
				),
				responseFields(
					fieldWithPath("refrigeratorId").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("refrigeratorName").type(STRING).description("냉장고 이름"),
					fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("memberUsername").type(STRING).description("회원 아이디"),
					fieldWithPath("memberNickName").type(STRING).description("회원 닉네임"),
					fieldWithPath("createdAt").type(STRING).description("냉장고 생성 날짜"),
					fieldWithPath("updatedAt").type(STRING).description("냉장고 수정 날짜")
				)
			));
	}

	@DisplayName("냉장고 수정 실패 테스트 - 냉장고 이름을 입력하지 않은 경우")
	@Test
	void updateFailure_invalidUpdateName() throws Exception {
		//given
		Long refrigeratorId = 1L;
		Long memberId = 1L;

		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime updatedAt = createdAt;

		RefrigeratorReq refrigeratorReq = new RefrigeratorReq("");
		RefrigeratorRes refrigeratorRes = new RefrigeratorRes(refrigeratorId, refrigerator.getName(), memberId, refrigerator.getMember().getUsername(), refrigerator.getMember().getNickName(), createdAt, updatedAt);

		when(refrigeratorService.update(eq(refrigeratorId), eq(refrigeratorReq), any(String.class))).thenReturn(refrigeratorRes);

		//when
		//then
		mockMvc.perform(patch("/api/refrigerators/{id}", refrigeratorId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(refrigeratorReq)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("C001"))
			.andExpect(jsonPath("$.errors").isNotEmpty())
			.andExpect(jsonPath("$.message").value("잘못된 값을 입력하셨습니다."))
			.andDo(print())
			.andDo(document("refrigerator/refrigerator-update-failure-invalid-update-name",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(
					parameterWithName("id").description("냉장고 ID")
				),
				requestFields(
					fieldWithPath("name").description("수정할 냉장고 이름")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("errors[].field").type(STRING).description("오류 필드"),
					fieldWithPath("errors[].value").type(STRING).description("오류 값"),
					fieldWithPath("errors[].reason").type(STRING).description("오류 사유"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}

	@DisplayName("냉장고 삭제 성공 테스트")
	@Test
	void delete() throws Exception {
		//given
		Long refrigeratorId = 1L;
		doNothing().when(refrigeratorService).delete(any(Long.class), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/refrigerators/{id}", refrigeratorId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andDo(document("refrigerator/refrigerator-delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				pathParameters(
					parameterWithName("id").description("냉장고 ID")
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
