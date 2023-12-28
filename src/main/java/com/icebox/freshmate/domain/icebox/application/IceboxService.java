package com.icebox.freshmate.domain.icebox.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.icebox.application.dto.request.IceboxReq;
import com.icebox.freshmate.domain.icebox.application.dto.response.IceboxRes;
import com.icebox.freshmate.domain.icebox.domain.Icebox;
import com.icebox.freshmate.domain.icebox.domain.IceboxRepository;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;
import com.icebox.freshmate.global.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IceboxService {

	private final IceboxRepository iceboxRepository;
	private final MemberRepository memberRepository;

	public IceboxRes create(IceboxReq iceboxReq) {
		String memberUsername = SecurityUtil.getLoginUsername();
		Member member = getMember(memberUsername);

		Icebox icebox = IceboxReq.toIcebox(iceboxReq, member);
		Icebox savedIcebox = iceboxRepository.save(icebox);

		return IceboxRes.from(savedIcebox);
	}

	private Member getMember(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORE_BY_MEMBER_username : {}", username);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}
}
