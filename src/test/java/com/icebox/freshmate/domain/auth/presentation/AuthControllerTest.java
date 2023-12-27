package com.icebox.freshmate.domain.auth.presentation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebox.freshmate.domain.auth.application.AuthService;
import com.icebox.freshmate.domain.auth.application.JwtService;
import com.icebox.freshmate.domain.auth.application.dto.request.MemberLoginReq;
import com.icebox.freshmate.domain.auth.application.dto.request.MemberSignUpAuthReq;
import com.icebox.freshmate.domain.auth.application.dto.request.MemberWithdrawReq;
import com.icebox.freshmate.domain.auth.application.dto.response.MemberAuthRes;
import com.icebox.freshmate.domain.member.application.dto.response.MemberInfoRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.Role;

@WebMvcTest(value = {AuthController.class}, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureRestDocs
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AuthService authService;

	@MockBean
	private JwtService jwtService;

	private Member member;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.realName("성이름")
			.username("aaaa1111")
			.password("aaaa1111!")
			.nickName("닉네임닉네임")
			.role(Role.USER)
			.build();
	}

	@DisplayName("회원 가입 테스트")
	@Test
	void signUp() throws Exception {
		//given
		Long memberId = 1L;
		MemberSignUpAuthReq mockSignUpRequest = new MemberSignUpAuthReq(member.getUsername(), member.getPassword(), member.getRealName(), member.getNickName());

		MemberInfoRes mockMemberInfoRes = new MemberInfoRes(memberId, member.getUsername(), member.getRealName(), member.getNickName(), member.getRole().name());

		when(authService.signUp(mockSignUpRequest)).thenReturn(mockMemberInfoRes);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockSignUpRequest)))
			.andExpect(content().json(objectMapper.writeValueAsString(mockMemberInfoRes)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.username").value(member.getUsername()))
			.andExpect(jsonPath("$.realName").value(member.getRealName()))
			.andExpect(jsonPath("$.nickName").value(member.getNickName()))
			.andExpect(jsonPath("$.role").value(member.getRole().name()))
			.andDo(print())
			.andDo(document("auth/auth-sign-up",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("username").description("아이디"),
					fieldWithPath("password").description("비밀번호"),
					fieldWithPath("realName").description("사용자 이름"),
					fieldWithPath("nickName").description("닉네임")
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

	@DisplayName("로그인 테스트")
	@Test
	void login() throws Exception {
		//given
		String accessToken = "adfsdklsfadlkjrm-$454j345ewjkekjdsmdfklsdfksdfkjsdfkjn";
		String refreshToken = "dfsdklasddkaerklerlk3#$%^$fdsklsdfksdffasddkdsksjkfakjfgkjds";

		MemberLoginReq mockLoginRequest = new MemberLoginReq(member.getUsername(), member.getPassword());
		MemberAuthRes mockMemberAuthRes = new MemberAuthRes(accessToken, refreshToken);

		when(authService.login(mockLoginRequest)).thenReturn(mockMemberAuthRes);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockLoginRequest)))
			.andExpect(content().json(objectMapper.writeValueAsString(mockMemberAuthRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").value(accessToken))
			.andExpect(jsonPath("$.refreshToken").value(refreshToken))
			.andDo(print())
			.andDo(document("auth/auth-login",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("username").description("아이디"),
					fieldWithPath("password").description("비밀번호")
				),
				responseFields(
					fieldWithPath("accessToken").type(STRING).description("Access Token"),
					fieldWithPath("refreshToken").type(STRING).description("Refresh Token")
				)
			));

	}

	@DisplayName("회원 탈퇴 테스트")
	@Test
	void withdraw() throws Exception {
		//given
		String password = "password1111!";

		MemberWithdrawReq mockWithdrawRequest = new MemberWithdrawReq(password);

		doNothing().when(authService).withdraw(anyString());

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/auth/withdraw")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.content(objectMapper.writeValueAsString(mockWithdrawRequest)))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andDo(document("auth/auth-withdraw",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("password").description("비밀번호")
				)
			));
	}

	@DisplayName("Access Token 토큰 재발급 테스트")
	@Test
	void reissueToken() throws Exception {
		//given
		String mockReissuedAccessToken = "adfsdklsfadlkjrm-$454j345ewjkekjdsmdfklsdfksdfkjsdfkjn";
		String mockRefreshToken = "dfsdklasddkaerklerlk3#$%^$fdsklsdfksdffasddkdsksjkfakjfgkjds";

		String mockRefreshTokenHeader = "Bearer dfsdklasddkaerklerlk3#$%^$fdsklsdfksdffasddkdsksjkfakjfgkjds";

		MemberAuthRes mockMemberAuthRes = new MemberAuthRes(mockReissuedAccessToken, mockRefreshToken);

		when(jwtService.getRefreshToken(mockRefreshTokenHeader)).thenReturn(mockRefreshToken);
		when(jwtService.reissueAccessToken(mockRefreshToken)).thenReturn(mockReissuedAccessToken);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/reissue")
				.header("Authorization-refresh", mockRefreshTokenHeader)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(content().json(objectMapper.writeValueAsString(mockMemberAuthRes)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.accessToken").value(mockReissuedAccessToken))
			.andExpect(jsonPath("$.refreshToken").value(mockRefreshToken))
			.andDo(print())
			.andDo(document("auth/auth-reissue-token",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization-refresh").description("Refresh Token")
				),
				responseFields(
					fieldWithPath("accessToken").type(STRING).description("Access Token"),
					fieldWithPath("refreshToken").type(STRING).description("Refresh Token")
				)
			));
	}

	@DisplayName("로그아웃 테스트")
	@Test
	void logout() throws Exception {
		//given
		doNothing().when(authService).logout();

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/auth/logout")
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andDo(document("auth/auth-logout",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				)
			));


		Mockito.verify(authService, Mockito.times(1)).logout();
	}
}
