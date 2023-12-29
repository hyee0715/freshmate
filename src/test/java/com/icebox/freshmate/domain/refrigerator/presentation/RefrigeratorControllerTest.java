package com.icebox.freshmate.domain.refrigerator.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
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
import com.icebox.freshmate.domain.refrigerator.application.RefrigeratorService;
import com.icebox.freshmate.domain.refrigerator.application.dto.request.RefrigeratorReq;
import com.icebox.freshmate.domain.refrigerator.application.dto.response.RefrigeratorRes;
import com.icebox.freshmate.domain.refrigerator.application.dto.response.RefrigeratorsRes;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.global.TestPrincipalDetailsService;

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

	private Refrigerator refrigerator;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
		mockMvc = MockMvcBuilders
		.webAppContextSetup(context)
			.apply(documentationConfiguration(restDocumentationContextProvider))
		.apply(springSecurity())
		.alwaysDo(print()).build();

		principalDetails = (PrincipalDetails) testUserDetailsService.loadUserByUsername(TestPrincipalDetailsService.USERNAME);

		refrigerator = Refrigerator.builder()
			.name("우리 집 냉장고")
			.build();
	}

	@DisplayName("냉장고 생성 테스트")
	@Test
	void create() throws Exception {
		//given
		Long refrigeratorId = 1L;

		RefrigeratorReq refrigeratorReq = new RefrigeratorReq(refrigerator.getName());
		RefrigeratorRes refrigeratorRes = new RefrigeratorRes(refrigeratorId, refrigerator.getName());

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
			.andExpect(jsonPath("$.id").value(refrigeratorId))
			.andExpect(jsonPath("$.name").value(refrigerator.getName()))
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
					fieldWithPath("id").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("name").type(STRING).description("냉장고 이름")
				)
			));
	}

	@DisplayName("냉장고 단건 조회 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long refrigeratorId = 1L;

		RefrigeratorRes refrigeratorRes = new RefrigeratorRes(refrigeratorId, refrigerator.getName());

		when(refrigeratorService.findById(anyLong())).thenReturn(refrigeratorRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/refrigerators/{id}", refrigeratorId)
				.contentType(MediaType.APPLICATION_JSON)
			.with(user(principalDetails))
			.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(refrigeratorRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(refrigeratorId))
			.andExpect(jsonPath("$.name").value(refrigerator.getName()))
			.andDo(print())
			.andDo(document("refrigerator/refrigerator-find-by-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(parameterWithName("id").description("냉장고 ID")),
				responseFields(
					fieldWithPath("id").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("name").type(STRING).description("냉장고 이름")
				)
			));
	}

	@DisplayName("회원의 모든 냉장고 조회 테스트")
	@Test
	void findAll() throws Exception {
		//given
		Refrigerator refrigerator2 = Refrigerator.builder()
			.name("우리 집 냉장고2")
			.build();

		RefrigeratorRes refrigeratorRes1 = new RefrigeratorRes(1L, refrigerator.getName());
		RefrigeratorRes refrigeratorRes2 = new RefrigeratorRes(2L, refrigerator2.getName());
		RefrigeratorsRes refrigeratorsRes = new RefrigeratorsRes(List.of(refrigeratorRes1, refrigeratorRes2));

		when(refrigeratorService.findAll(any(String.class))).thenReturn(refrigeratorsRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/refrigerators")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.refrigerators", hasSize(2)))
			.andExpect(jsonPath("$.refrigerators[0].id").value(1))
			.andExpect(jsonPath("$.refrigerators[0].name").value(refrigerator.getName()))
			.andExpect(jsonPath("$.refrigerators[1].id").value(2))
			.andExpect(jsonPath("$.refrigerators[1].name").value(refrigerator2.getName()))
			.andDo(print())
			.andDo(document("refrigerator/refrigerator-find-all",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				responseFields(
					fieldWithPath("refrigerators").description("냉장고 배열"),
					fieldWithPath("refrigerators[].id").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("refrigerators[].name").type(STRING).description("냉장고 이름")
				)
			));
	}

	@DisplayName("냉장고 수정 테스트")
	@Test
	void update() throws Exception {
		//given
		Long refrigeratorId = 1L;
		RefrigeratorReq refrigeratorReq = new RefrigeratorReq(refrigerator.getName());
		RefrigeratorRes refrigeratorRes = new RefrigeratorRes(refrigeratorId, refrigerator.getName());

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
			.andExpect(jsonPath("$.id").value(refrigeratorId))
			.andExpect(jsonPath("$.name").value(refrigerator.getName()))
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
					fieldWithPath("id").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("name").type(STRING).description("냉장고 이름")
				)
			));
	}

	@DisplayName("냉장고 삭제 테스트")
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
}
