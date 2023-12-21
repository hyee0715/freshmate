package com.icebox.freshmate.domain.auth.application;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.icebox.freshmate.domain.auth.application.dto.request.MemberLoginReq;
import com.icebox.freshmate.domain.auth.application.dto.request.MemberSignUpAuthReq;
import com.icebox.freshmate.domain.auth.application.dto.response.MemberAuthRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Override
	public UserDetails loadUserByUsername(String username) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return User.builder().username(member.getUsername())
			.password(member.getPassword())
			.roles(member.getRole().name())
			.build();
	}

	public MemberAuthRes signUp(MemberSignUpAuthReq memberSignUpAuthReq) {
		Member member = memberSignUpAuthReq.toMember();
		member.addUserAuthority();
		member.encodePassword(passwordEncoder);

		checkDuplicatedUsername(memberSignUpAuthReq.username());

		Member savedMember = memberRepository.save(member);

		return createMemberAuthResWithTokens(savedMember);
	}

	public MemberAuthRes login(MemberLoginReq memberLoginReq) {
		Member member = memberRepository.findByUsername(memberLoginReq.username())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		checkLoginPassword(member, memberLoginReq.password());

		Member loginRequestMember = MemberLoginReq.toMember(memberLoginReq);

		return createMemberAuthResWithTokens(loginRequestMember);
	}

	private void checkLoginPassword(Member member, String password) {
		if (!member.matchPassword(passwordEncoder, password)) {
			throw new BusinessException(ErrorCode.LOGIN_FAILURE);
		}
	}

	private MemberAuthRes createMemberAuthResWithTokens(Member member) {
		String accessToken = jwtService.createAccessToken(member.getUsername());
		String refreshToken = jwtService.createRefreshToken();
		member.updateRefreshToken(refreshToken);

		return new MemberAuthRes(accessToken, refreshToken);
	}

	private void checkDuplicatedUsername(String username) {
		if(memberRepository.existsByUsername(username)) {
			throw new BusinessException(ErrorCode.ALREADY_EXIST_USERNAME);
		}
	}
}
