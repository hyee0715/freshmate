package com.icebox.freshmate.domain.icebox.application;

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

import com.icebox.freshmate.domain.icebox.application.dto.request.IceboxReq;
import com.icebox.freshmate.domain.icebox.application.dto.response.IceboxRes;
import com.icebox.freshmate.domain.icebox.application.dto.response.IceboxesRes;
import com.icebox.freshmate.domain.icebox.domain.Icebox;
import com.icebox.freshmate.domain.icebox.domain.IceboxRepository;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;

@ExtendWith(MockitoExtension.class)
class IceboxServiceTest {

	@InjectMocks
	private IceboxService iceboxService;

	@Mock
	private IceboxRepository iceboxRepository;

	@Mock
	private MemberRepository memberRepository;

	private Member member;
	private Icebox icebox;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.realName("성이름")
			.username("aaaa1111")
			.password("aaaa1111!")
			.nickName("닉네임닉네임")
			.role(Role.USER)
			.build();

		icebox = Icebox.builder()
			.name("우리 집 냉장고")
			.build();
	}

	@DisplayName("냉장고 생성 테스트")
	@Test
	void create() {
		//given
		IceboxReq iceboxReq = new IceboxReq(icebox.getName());

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(iceboxRepository.save(any(Icebox.class))).thenReturn(icebox);

		//when
		IceboxRes iceboxRes = iceboxService.create(iceboxReq, member.getUsername());

		//then
		assertThat(iceboxRes.name()).isEqualTo(icebox.getName());
	}

	@DisplayName("냉장고 단건 조회 테스트")
	@Test
	void findById() {
		//given
		Long iceboxId = 1L;
		when(iceboxRepository.findById(anyLong())).thenReturn(Optional.of(icebox));

		//when
		IceboxRes iceboxRes = iceboxService.findById(iceboxId);

		//then
		assertThat(iceboxRes.name()).isEqualTo(icebox.getName());
	}

	@DisplayName("회원의 모든 냉장고 조회 테스트")
	@Test
	void findAll() {
		//given
		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(iceboxRepository.findAllByMemberId(any())).thenReturn(List.of(icebox));

		//when
		IceboxesRes iceboxesRes = iceboxService.findAll(member.getUsername());

		//then
		assertThat(iceboxesRes.iceboxes()).hasSize(1);
		assertThat(iceboxesRes.iceboxes().get(0).name()).isEqualTo(icebox.getName());
	}

	@DisplayName("냉장고 수정 테스트")
	@Test
	void update() {
		//given
		Long iceboxId = 1L;
		String updatedIceboxName = "냉장고 이름 수정";
		IceboxReq iceboxReq = new IceboxReq(updatedIceboxName);

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(iceboxRepository.findByIdAndMemberId(anyLong(), any())).thenReturn(Optional.of(icebox));

		//when
		IceboxRes iceboxRes = iceboxService.update(iceboxId, iceboxReq, member.getUsername());

		//then
		assertThat(iceboxRes.name()).isEqualTo(updatedIceboxName);
	}
}
