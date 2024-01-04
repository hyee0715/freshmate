package com.icebox.freshmate.domain.refrigerator.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.refrigerator.application.dto.request.RefrigeratorReq;
import com.icebox.freshmate.domain.refrigerator.application.dto.response.RefrigeratorRes;
import com.icebox.freshmate.domain.refrigerator.application.dto.response.RefrigeratorsRes;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.refrigerator.domain.RefrigeratorRepository;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefrigeratorService {

	private final RefrigeratorRepository refrigeratorRepository;
	private final MemberRepository memberRepository;

	public RefrigeratorRes create(RefrigeratorReq refrigeratorReq, String username) {
		Member member = getMemberByUsername(username);

		Refrigerator refrigerator = RefrigeratorReq.toRefrigerator(refrigeratorReq, member);
		Refrigerator savedRefrigerator = refrigeratorRepository.save(refrigerator);

		return RefrigeratorRes.from(savedRefrigerator);
	}

	@Transactional(readOnly = true)
	public RefrigeratorRes findById(Long id) {
		Refrigerator refrigerator = refrigeratorRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_REFRIGERATOR));

		return RefrigeratorRes.from(refrigerator);
	}

	@Transactional(readOnly = true)
	public RefrigeratorsRes findAll(String username) {
		Member member = getMemberByUsername(username);

		List<Refrigerator> refrigerators = refrigeratorRepository.findAllByMemberId(member.getId());

		return RefrigeratorsRes.from(refrigerators);
	}

	public RefrigeratorRes update(Long id, RefrigeratorReq refrigeratorReq, String username) {
		Member member = getMemberByUsername(username);

		Refrigerator refrigerator = refrigeratorRepository.findByIdAndMemberId(id, member.getId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_REFRIGERATOR));

		Refrigerator updatedRefrigerator = RefrigeratorReq.toRefrigerator(refrigeratorReq, member);

		refrigerator.update(updatedRefrigerator);

		return RefrigeratorRes.from(refrigerator);
	}

	public void delete(Long id, String username) {
		Member member = getMemberByUsername(username);

		Refrigerator refrigerator = refrigeratorRepository.findByIdAndMemberId(id, member.getId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_REFRIGERATOR));

		refrigeratorRepository.delete(refrigerator);
	}

	private Member getMemberByUsername(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORE_BY_MEMBER_USERNAME : {}", username);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}
}
