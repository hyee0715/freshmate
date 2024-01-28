package com.icebox.freshmate.domain.auth.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.auth.application.dto.request.MemberLoginReq;
import com.icebox.freshmate.domain.auth.application.dto.request.MemberSignUpAuthReq;
import com.icebox.freshmate.domain.auth.application.dto.response.MemberAuthRes;
import com.icebox.freshmate.domain.member.application.dto.response.MemberInfoRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AuthService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public MemberInfoRes signUp(MemberSignUpAuthReq memberSignUpAuthReq) {
		Member member = memberSignUpAuthReq.toMember();
		member.addUserAuthority();
		member.encodePassword(passwordEncoder);

		checkDuplicatedUsername(memberSignUpAuthReq.username());

		Member savedMember = memberRepository.save(member);

		return MemberInfoRes.from(savedMember);
	}

	public MemberAuthRes login(MemberLoginReq memberLoginReq) {
		Member member = getMemberByUsername(memberLoginReq.username());

		checkLoginPassword(member, memberLoginReq.password());

		Member loginRequestMember = MemberLoginReq.toMember(memberLoginReq);

		return createMemberAuthResWithTokens(loginRequestMember);
	}

	public void withdraw(String checkPassword, String username) {
		Member member = getMemberByUsername(username);

		if (!member.matchPassword(passwordEncoder, checkPassword)) {

			throw new BusinessException(ErrorCode.WRONG_PASSWORD);
		}

		memberRepository.delete(member);
	}

	public void logout(String username) {
		Member member = getMemberByUsername(username);

		member.destroyRefreshToken();
	}

	private void checkLoginPassword(Member member, String password) {
		if (!member.matchPassword(passwordEncoder, password)) {
			log.warn("LOGIN_FAILURE : memberId = {}", member.getId());

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
		if (memberRepository.existsByUsername(username)) {
			log.warn("ALREADY_EXIST_USERNAME : {}", username);

			throw new BusinessException(ErrorCode.ALREADY_EXIST_USERNAME);
		}
	}

	private Member getMemberByUsername(String username) {

		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);

				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}
}
