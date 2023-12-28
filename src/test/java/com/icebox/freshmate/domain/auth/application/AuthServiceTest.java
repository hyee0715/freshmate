package com.icebox.freshmate.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.userdetails.UserDetails;

import com.icebox.freshmate.domain.auth.application.dto.request.MemberLoginReq;
import com.icebox.freshmate.domain.auth.application.dto.request.MemberSignUpAuthReq;
import com.icebox.freshmate.domain.auth.application.dto.response.MemberAuthRes;
import com.icebox.freshmate.domain.member.application.dto.response.MemberInfoRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@Mock
	private SecurityContext securityContext;

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
	void signUp() {
		//given
		MemberSignUpAuthReq memberSignUpAuthReq = new MemberSignUpAuthReq(member.getUsername(), member.getPassword(), member.getRealName(), member.getNickName());

		when(memberRepository.existsByUsername(anyString())).thenReturn(false);
		when(memberRepository.save(any(Member.class))).thenReturn(member);

		//when
		MemberInfoRes memberInfoRes = authService.signUp(memberSignUpAuthReq);

		//then
		assertThat(memberInfoRes.username()).isEqualTo(member.getUsername());
		assertThat(memberInfoRes.realName()).isEqualTo(member.getRealName());
		assertThat(memberInfoRes.nickName()).isEqualTo(member.getNickName());
		assertThat(memberInfoRes.role()).isEqualTo(member.getRole().name());
	}

	@DisplayName("로그인 테스트")
	@Test
	void login() {
		//given
		String username = "aaaa1111";
		String password = "aaaa1111!";

		String mockAccessToken = "MockAccessToken";
		String mockRefreshToken = "MockRefreshToken";

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(member));
		when(jwtService.createAccessToken(anyString())).thenReturn(mockAccessToken);
		when(jwtService.createRefreshToken()).thenReturn(mockRefreshToken);
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		MemberLoginReq memberLoginReq = new MemberLoginReq(username, password);

		//when
		MemberAuthRes memberAuthRes = authService.login(memberLoginReq);

		//then
		assertThat(memberAuthRes.accessToken()).isEqualTo(mockAccessToken);
		assertThat(memberAuthRes.refreshToken()).isEqualTo(mockRefreshToken);
	}

	@DisplayName("회원 탈퇴 테스트")
	@Test
	void withdraw() {
		//given
		String username = "aaaa1111";
		String password = "aaaa1111!";

		UserDetails userDetails = User.withUsername(username)
			.password(password)
			.roles(Role.USER.name())
			.build();

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(userDetails);

		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(member));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		doNothing().when(memberRepository).delete(any(Member.class));

		//when
		authService.withdraw(password);

		//then
		verify(memberRepository, times(1))
			.delete(member);
	}

	@DisplayName("로그아웃 테스트")
	@Test
	void logout() {
		//given
		String username = "aaaa1111";
		String password = "aaaa1111!";

		UserDetails userDetails = User.withUsername(username)
			.password(password)
			.roles(Role.USER.name())
			.build();

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(userDetails);

		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(member));

		//when
		authService.logout();

		//then
		assertThat(member.getRefreshToken()).isNull();
	}
}
