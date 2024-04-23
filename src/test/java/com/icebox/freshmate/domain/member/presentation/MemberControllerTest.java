package com.icebox.freshmate.domain.member.presentation;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.member.application.MemberService;
import com.icebox.freshmate.domain.member.application.dto.request.MemberUpdateInfoReq;
import com.icebox.freshmate.domain.member.application.dto.request.MemberUpdatePasswordReq;
import com.icebox.freshmate.domain.member.application.dto.response.MemberInfoRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.global.TestPrincipalDetailsService;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;

@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(value = {MemberController.class})
@AutoConfigureRestDocs
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private MemberService memberService;

	private final TestPrincipalDetailsService testUserDetailsService = new TestPrincipalDetailsService();
	private PrincipalDetails principalDetails;

	private Member member;

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
	}

	@DisplayName("회원 ID로 회원 정보 조회 테스트")
	@Test
	void findInfoById() throws Exception {
		//given
		Long memberId = 1L;
		MemberInfoRes memberInfoRes = new MemberInfoRes(memberId, member.getUsername(), member.getRealName(), member.getNickName(), member.getRole().name());

		when(memberService.findInfoById(memberId)).thenReturn(memberInfoRes);

		//when
		//then
		mockMvc.perform(get("/api/member/{id}", memberId)
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(memberInfoRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.username").value(member.getUsername()))
			.andExpect(jsonPath("$.realName").value(member.getRealName()))
			.andExpect(jsonPath("$.nickName").value(member.getNickName()))
			.andExpect(jsonPath("$.role").value(member.getRole().name()))
			.andDo(print())
			.andDo(document("member/member-find-info-by-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("회원 ID")),
				responseFields(
					fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("username").type(STRING).description("아이디"),
					fieldWithPath("realName").type(STRING).description("사용자 이름"),
					fieldWithPath("nickName").type(STRING).description("닉네임"),
					fieldWithPath("role").type(STRING).description("사용자 ROLE")
				)
			));
	}

	@DisplayName("접속중인 회원 정보 조회 테스트")
	@Test
	void findInfo() throws Exception {
		//given
		Long memberId = 1L;
		MemberInfoRes memberInfoRes = new MemberInfoRes(memberId, member.getUsername(), member.getRealName(), member.getNickName(), member.getRole().name());

		when(memberService.findInfo(member.getUsername())).thenReturn(memberInfoRes);

		//when
		//then
		mockMvc.perform(get("/api/member")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.username").value(member.getUsername()))
			.andExpect(jsonPath("$.realName").value(member.getRealName()))
			.andExpect(jsonPath("$.nickName").value(member.getNickName()))
			.andExpect(jsonPath("$.role").value(member.getRole().name()))
			.andDo(print())
			.andDo(document("member/member-find-info",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				responseFields(
					fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("username").type(STRING).description("아이디"),
					fieldWithPath("realName").type(STRING).description("사용자 이름"),
					fieldWithPath("nickName").type(STRING).description("닉네임"),
					fieldWithPath("role").type(STRING).description("사용자 ROLE")
				)
			));
	}

	@DisplayName("회원 정보 수정 테스트")
	@Test
	void updateInfo() throws Exception {
		//given
		Long memberId = 1L;
		String updatedRealName = "이름수정";
		String updatedNickName = "닉네임수정";

		MemberUpdateInfoReq updateInfoReq = new MemberUpdateInfoReq(updatedRealName, updatedNickName);
		MemberInfoRes memberInfoRes = new MemberInfoRes(memberId, member.getUsername(), updatedRealName, updatedNickName, member.getRole().name());

		when(memberService.updateInfo(eq(updateInfoReq), eq(member.getUsername()))).thenReturn(memberInfoRes);

		//when
		//then
		mockMvc.perform(patch("/api/member")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(updateInfoReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(memberInfoRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberId").value(memberInfoRes.memberId()))
			.andExpect(jsonPath("$.username").value(memberInfoRes.username()))
			.andExpect(jsonPath("$.realName").value(memberInfoRes.realName()))
			.andExpect(jsonPath("$.nickName").value(memberInfoRes.nickName()))
			.andExpect(jsonPath("$.role").value(memberInfoRes.role()))
			.andDo(print())
			.andDo(document("member/member-update-info",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("realName").description("수정할 사용자 이름"),
					fieldWithPath("nickName").description("수정할 닉네임")
				),
				responseFields(
					fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("username").type(STRING).description("아이디"),
					fieldWithPath("realName").type(STRING).description("수정된 사용자 이름"),
					fieldWithPath("nickName").type(STRING).description("수정된 닉네임"),
					fieldWithPath("role").type(STRING).description("사용자 ROLE")
				)
			));
	}

	@DisplayName("회원 비밀번호 수정 테스트")
	@Test
	void updatePassword() throws Exception {
		//given
		Long memberId = 1L;
		String updatedPassword = "updatedPassword11!!";
		MemberUpdatePasswordReq memberUpdatePasswordReq = new MemberUpdatePasswordReq(member.getPassword(), updatedPassword);
		MemberInfoRes memberInfoRes = new MemberInfoRes(memberId, member.getUsername(), member.getRealName(), member.getNickName(), member.getRole().name());

		when(memberService.updatePassword(eq(memberUpdatePasswordReq), eq(member.getUsername()))).thenReturn(memberInfoRes);

		//when
		//then
		mockMvc.perform(patch("/api/member/password")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(memberUpdatePasswordReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(memberInfoRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberId").value(memberInfoRes.memberId()))
			.andExpect(jsonPath("$.username").value(memberInfoRes.username()))
			.andExpect(jsonPath("$.realName").value(memberInfoRes.realName()))
			.andExpect(jsonPath("$.nickName").value(memberInfoRes.nickName()))
			.andExpect(jsonPath("$.role").value(memberInfoRes.role()))
			.andDo(print())
			.andDo(document("member/member-update-password",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("originalPassword").description("기존 비밀번호"),
					fieldWithPath("newPassword").description("새 비밀번호")
				),
				responseFields(
					fieldWithPath("memberId").type(NUMBER).description("회원 ID"),
					fieldWithPath("username").type(STRING).description("아이디"),
					fieldWithPath("realName").type(STRING).description("사용자 이름"),
					fieldWithPath("nickName").type(STRING).description("닉네임"),
					fieldWithPath("role").type(STRING).description("사용자 ROLE")
				)
			));
	}

	@DisplayName("회원 비밀번호 수정 실패 테스트 - 기존 비밀번호 불일치")
	@Test
	void updatePasswordFailure_wrongOriginalPassword() throws Exception {
		//given
		String updatedPassword = "updatedPassword11!!";
		MemberUpdatePasswordReq memberUpdatePasswordReq = new MemberUpdatePasswordReq(member.getPassword(), updatedPassword);

		//when
		//then
		doThrow(new BusinessException(ErrorCode.WRONG_PASSWORD)).when(
			memberService).updatePassword(eq(memberUpdatePasswordReq), eq(member.getUsername()));

		mockMvc.perform(patch("/api/member/password")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(memberUpdatePasswordReq)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.code").value("M004"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."))
			.andDo(print())
			.andDo(document("member/member-update-password-failure-wrong-original-password",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				requestFields(
					fieldWithPath("originalPassword").description("기존 비밀번호"),
					fieldWithPath("newPassword").description("새 비밀번호")
				),
				responseFields(
					fieldWithPath("timestamp").type(STRING).description("예외 시간"),
					fieldWithPath("code").type(STRING).description("예외 코드"),
					fieldWithPath("errors[]").type(ARRAY).description("오류 목록"),
					fieldWithPath("message").type(STRING).description("오류 메시지")
				)
			));
	}
}
