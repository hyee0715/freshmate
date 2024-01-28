package com.icebox.freshmate.domain.member.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public MemberInfoRes findInfoById(Long id) {
		Member member = getMemberById(id);

		return MemberInfoRes.from(member);
	}

	@Transactional(readOnly = true)
	public MemberInfoRes findInfo(String username) {
		Member member = getMemberByUsername(username);

		return MemberInfoRes.from(member);
	}

	public MemberInfoRes updateInfo(MemberUpdateInfoReq memberUpdateInfoReq, String username) {
		Member member = getMemberByUsername(username);

		Member memberFromRequest = memberUpdateInfoReq.toMember();

		member.updateInfo(memberFromRequest);

		return MemberInfoRes.from(member);
	}

	public MemberInfoRes updatePassword(MemberUpdatePasswordReq memberUpdatePasswordReq, String username) {
		Member member = getMemberByUsername(username);

		if (!member.matchPassword(passwordEncoder, memberUpdatePasswordReq.originalPassword())) {

			throw new BusinessException(ErrorCode.WRONG_PASSWORD);
		}

		member.updatePassword(passwordEncoder, memberUpdatePasswordReq.newPassword());

		return MemberInfoRes.from(member);
	}

	private Member getMemberByUsername(String username) {

		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);

				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}

	private Member getMemberById(Long userId) {

		return memberRepository.findById(userId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_ID : {}", userId);

				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}
}
