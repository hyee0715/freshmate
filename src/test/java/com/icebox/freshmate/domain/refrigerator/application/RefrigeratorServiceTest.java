package com.icebox.freshmate.domain.refrigerator.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.icebox.freshmate.domain.refrigerator.application.dto.request.RefrigeratorReq;
import com.icebox.freshmate.domain.refrigerator.application.dto.response.RefrigeratorRes;
import com.icebox.freshmate.domain.refrigerator.application.dto.response.RefrigeratorsRes;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.refrigerator.domain.RefrigeratorRepository;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;

@ExtendWith(MockitoExtension.class)
class RefrigeratorServiceTest {

	@InjectMocks
	private RefrigeratorService refrigeratorService;

	@Mock
	private RefrigeratorRepository refrigeratorRepository;

	@Mock
	private MemberRepository memberRepository;

	private Member member;
	private Refrigerator refrigerator;

	@BeforeEach
	void setUp() {
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
	}

	@DisplayName("냉장고 생성 테스트")
	@Test
	void create() {
		//given
		RefrigeratorReq refrigeratorReq = new RefrigeratorReq(refrigerator.getName());

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(refrigeratorRepository.save(any(Refrigerator.class))).thenReturn(refrigerator);

		//when
		RefrigeratorRes refrigeratorRes = refrigeratorService.create(refrigeratorReq, member.getUsername());

		//then
		assertThat(refrigeratorRes.refrigeratorName()).isEqualTo(refrigerator.getName());
	}

	@DisplayName("냉장고 단건 조회 테스트")
	@Test
	void findById() {
		//given
		Long refrigeratorId = 1L;
		when(refrigeratorRepository.findById(anyLong())).thenReturn(Optional.of(refrigerator));

		//when
		RefrigeratorRes refrigeratorRes = refrigeratorService.findById(refrigeratorId);

		//then
		assertThat(refrigeratorRes.refrigeratorName()).isEqualTo(refrigerator.getName());
	}

	@DisplayName("회원의 모든 냉장고 조회 테스트")
	@Test
	void findAll() {
		//given
		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(refrigeratorRepository.findAllByMemberId(any())).thenReturn(List.of(refrigerator));

		//when
		RefrigeratorsRes refrigeratorsRes = refrigeratorService.findAll(member.getUsername());

		//then
		assertThat(refrigeratorsRes.refrigerators()).hasSize(1);
		assertThat(refrigeratorsRes.refrigerators().get(0).refrigeratorName()).isEqualTo(refrigerator.getName());
	}

	@DisplayName("냉장고 수정 테스트")
	@Test
	void update() {
		//given
		Long refrigeratorId = 1L;
		String updatedRefrigeratorName = "냉장고 이름 수정";
		RefrigeratorReq refrigeratorReq = new RefrigeratorReq(updatedRefrigeratorName);

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(refrigeratorRepository.findByIdAndMemberId(anyLong(), any())).thenReturn(Optional.of(refrigerator));

		//when
		RefrigeratorRes refrigeratorRes = refrigeratorService.update(refrigeratorId, refrigeratorReq, member.getUsername());

		//then
		assertThat(refrigeratorRes.refrigeratorName()).isEqualTo(updatedRefrigeratorName);
	}
}
