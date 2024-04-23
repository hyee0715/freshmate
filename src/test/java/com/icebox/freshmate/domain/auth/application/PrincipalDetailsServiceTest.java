package com.icebox.freshmate.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.security.core.userdetails.UserDetails;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;

@ExtendWith(MockitoExtension.class)
class PrincipalDetailsServiceTest {

	@InjectMocks
	private PrincipalDetailsService principalDetailsService;

	@Mock
	private MemberRepository memberRepository;

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

	@DisplayName("username으로 회원 정보 찾기 테스트")
	@Test
	void loadUserByUsername() {
		//given
		String username = "aaaa1111";

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(member));

		//when
		UserDetails userDetails = principalDetailsService.loadUserByUsername(username);

		//then
		assertThat(userDetails.getUsername()).isEqualTo(member.getUsername());
		assertThat(userDetails.getPassword()).isEqualTo(member.getPassword());
	}
}
