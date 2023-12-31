package com.icebox.freshmate.domain.member.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.member.application.dto.request.MemberUpdateInfoReq;
import com.icebox.freshmate.domain.member.application.dto.request.MemberUpdatePasswordReq;
import com.icebox.freshmate.domain.member.application.dto.response.MemberInfoRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public MemberInfoRes findInfoById(Long id) {
		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return MemberInfoRes.from(member);
	}

	@Transactional(readOnly = true)
	public MemberInfoRes findInfo(String username) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return MemberInfoRes.from(member);
	}

	public MemberInfoRes updateInfo(MemberUpdateInfoReq memberUpdateInfoReq, String username) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		Member memberFromRequest = memberUpdateInfoReq.toMember();

		member.updateInfo(memberFromRequest);

		return MemberInfoRes.from(member);
	}

	public MemberInfoRes updatePassword(MemberUpdatePasswordReq memberUpdatePasswordReq, String username) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		if (!member.matchPassword(passwordEncoder, memberUpdatePasswordReq.originalPassword()) ) {
			throw new BusinessException(ErrorCode.WRONG_PASSWORD);
		}

		member.updatePassword(passwordEncoder, memberUpdatePasswordReq.newPassword());

		return MemberInfoRes.from(member);
	}
}
