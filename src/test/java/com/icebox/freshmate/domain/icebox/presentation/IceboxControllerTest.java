package com.icebox.freshmate.domain.icebox.presentation;

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
import com.icebox.freshmate.domain.icebox.application.IceboxService;
import com.icebox.freshmate.domain.icebox.application.dto.request.IceboxReq;
import com.icebox.freshmate.domain.icebox.application.dto.response.IceboxRes;
import com.icebox.freshmate.domain.icebox.application.dto.response.IceboxesRes;
import com.icebox.freshmate.domain.icebox.domain.Icebox;
import com.icebox.freshmate.global.TestPrincipalDetailsService;

@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(value = {IceboxController.class})
@AutoConfigureRestDocs
class IceboxControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private IceboxService iceboxService;

	private final TestPrincipalDetailsService testUserDetailsService = new TestPrincipalDetailsService();
	private PrincipalDetails principalDetails;

	private Icebox icebox;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
		mockMvc = MockMvcBuilders
		.webAppContextSetup(context)
			.apply(documentationConfiguration(restDocumentationContextProvider))
		.apply(springSecurity())
		.alwaysDo(print()).build();

		principalDetails = (PrincipalDetails) testUserDetailsService.loadUserByUsername(TestPrincipalDetailsService.USERNAME);

		icebox = Icebox.builder()
			.name("우리 집 냉장고")
			.build();
	}

	@DisplayName("냉장고 생성 테스트")
	@Test
	void create() throws Exception {
		//given
		Long iceboxId = 1L;

		IceboxReq iceboxReq = new IceboxReq(icebox.getName());
		IceboxRes iceboxRes = new IceboxRes(iceboxId, icebox.getName());

		when(iceboxService.create(any(IceboxReq.class), any(String.class))).thenReturn(iceboxRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.post("/api/iceboxes")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(iceboxReq)))
			.andExpect(content().json(objectMapper.writeValueAsString(iceboxRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(iceboxId))
			.andExpect(jsonPath("$.name").value(icebox.getName()))
			.andDo(print())
			.andDo(document("icebox/icebox-create",
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
		Long iceboxId = 1L;

		IceboxRes iceboxRes = new IceboxRes(iceboxId, icebox.getName());

		when(iceboxService.findById(anyLong())).thenReturn(iceboxRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/iceboxes/{id}", iceboxId)
				.contentType(MediaType.APPLICATION_JSON)
			.with(user(principalDetails))
			.with(csrf().asHeader()))
			.andExpect(content().json(objectMapper.writeValueAsString(iceboxRes)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(iceboxId))
			.andExpect(jsonPath("$.name").value(icebox.getName()))
			.andDo(print())
			.andDo(document("icebox/icebox-find-by-id",
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
		Icebox icebox2 = Icebox.builder()
			.name("우리 집 냉장고2")
			.build();

		IceboxRes iceboxRes1 = new IceboxRes(1L, icebox.getName());
		IceboxRes iceboxRes2 = new IceboxRes(2L, icebox2.getName());
		IceboxesRes iceboxesRes = new IceboxesRes(List.of(iceboxRes1, iceboxRes2));

		when(iceboxService.findAll(any(String.class))).thenReturn(iceboxesRes);

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.get("/api/iceboxes")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.iceboxes", hasSize(2)))
			.andExpect(jsonPath("$.iceboxes[0].id").value(1))
			.andExpect(jsonPath("$.iceboxes[0].name").value(icebox.getName()))
			.andExpect(jsonPath("$.iceboxes[1].id").value(2))
			.andExpect(jsonPath("$.iceboxes[1].name").value(icebox2.getName()))
			.andDo(print())
			.andDo(document("icebox/icebox-find-all",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("Access Token")
				),
				responseFields(
					fieldWithPath("iceboxes").description("냉장고 배열"),
					fieldWithPath("iceboxes[].id").type(NUMBER).description("냉장고 ID"),
					fieldWithPath("iceboxes[].name").type(STRING).description("냉장고 이름")
				)
			));
	}

	@DisplayName("냉장고 수정 테스트")
	@Test
	void update() throws Exception {
		//given
		Long iceboxId = 1L;
		IceboxReq iceboxReq = new IceboxReq(icebox.getName());
		IceboxRes iceboxRes = new IceboxRes(iceboxId, icebox.getName());

		when(iceboxService.update(any(Long.class), any(IceboxReq.class), any(String.class)))
			.thenReturn(iceboxRes);

		//when
		//then
		mockMvc.perform(patch("/api/iceboxes/{id}", iceboxId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader())
				.content(objectMapper.writeValueAsString(iceboxReq)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(iceboxId))
			.andExpect(jsonPath("$.name").value(icebox.getName()))
			.andDo(print())
			.andDo(document("icebox/icebox-update",
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
		Long iceboxId = 1L;
		doNothing().when(iceboxService).delete(any(Long.class), any(String.class));

		//when
		//then
		mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/iceboxes/{id}", iceboxId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer {ACCESS_TOKEN}")
				.with(user(principalDetails))
				.with(csrf().asHeader()))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andDo(document("icebox/icebox-delete",
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
