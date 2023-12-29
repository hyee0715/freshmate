package com.icebox.freshmate.domain.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.icebox.freshmate.domain.member.application.dto.request.MemberUpdateInfoReq;
import com.icebox.freshmate.domain.member.application.dto.request.MemberUpdatePasswordReq;
import com.icebox.freshmate.domain.member.application.dto.response.MemberInfoRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberService memberService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

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

	@DisplayName("회원 ID로 회원 정보 조회 테스트")
	@Test
	void findInfoById() {
		//given
		Long memberId = 1L;

		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

		//when
		MemberInfoRes memberInfoRes = memberService.findInfoById(memberId);

		//then
		assertThat(memberInfoRes.username()).isEqualTo(member.getUsername());
		assertThat(memberInfoRes.nickName()).isEqualTo(member.getNickName());
		assertThat(memberInfoRes.realName()).isEqualTo(member.getRealName());
		assertThat(memberInfoRes.role()).isEqualTo(member.getRole().name());
	}

	@DisplayName("접속중인 회원 정보 조회 테스트")
	@Test
	void findInfo() {
		//given
		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));

		//when
		MemberInfoRes memberInfoRes = memberService.findInfo(member.getUsername());

		//then
		assertThat(memberInfoRes.username()).isEqualTo(member.getUsername());
		assertThat(memberInfoRes.nickName()).isEqualTo(member.getNickName());
		assertThat(memberInfoRes.realName()).isEqualTo(member.getRealName());
		assertThat(memberInfoRes.role()).isEqualTo(member.getRole().name());
	}

	@DisplayName("회원 정보 수정 테스트")
	@Test
	void updateInfo() {
		//given
		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));

		MemberUpdateInfoReq memberUpdateInfoReq = new MemberUpdateInfoReq("본명수정", "닉네임수정");

		//when
		MemberInfoRes memberInfoRes = memberService.updateInfo(memberUpdateInfoReq, member.getUsername());

		//then
		assertThat(memberInfoRes.realName()).isEqualTo(memberUpdateInfoReq.realName());
		assertThat(memberInfoRes.nickName()).isEqualTo(memberUpdateInfoReq.nickName());
	}

	@DisplayName("회원 비밀번호 수정 테스트")
	@Test
	void updatePassword() {
		//given
		String updatedPassword = "updatedPassword11!!";

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		MemberUpdatePasswordReq memberUpdatePasswordReq = new MemberUpdatePasswordReq(member.getPassword(), updatedPassword);

		//when
		MemberInfoRes memberInfoRes = memberService.updatePassword(memberUpdatePasswordReq, member.getUsername());

		//then
		assertThat(memberInfoRes.username()).isEqualTo(member.getUsername());
		assertThat(memberInfoRes.nickName()).isEqualTo(member.getNickName());
		assertThat(memberInfoRes.realName()).isEqualTo(member.getRealName());
		assertThat(memberInfoRes.role()).isEqualTo(member.getRole().name());
	}

	@DisplayName("회원 비밀번호 수정 실패 테스트 - 기존 비밀번호 불일치")
	@Test
	void updatePasswordFailure_originalPasswordMismatch() {
		//given
		String updatedPassword = "updatedPassword11!!";

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		MemberUpdatePasswordReq memberUpdatePasswordReq = new MemberUpdatePasswordReq("mismatch11!", updatedPassword);

		//when
		//then
		assertThrows(BusinessException.class, () -> memberService.updatePassword(memberUpdatePasswordReq, member.getUsername()));
	}
}
