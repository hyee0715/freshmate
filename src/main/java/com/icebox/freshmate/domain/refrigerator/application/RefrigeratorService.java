package com.icebox.freshmate.domain.refrigerator.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.refrigerator.application.dto.request.RefrigeratorReq;
import com.icebox.freshmate.domain.refrigerator.application.dto.response.RefrigeratorRes;
import com.icebox.freshmate.domain.refrigerator.application.dto.response.RefrigeratorsRes;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.refrigerator.domain.RefrigeratorRepository;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.refrigerator.domain.RefrigeratorSortType;
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
		Refrigerator refrigerator = getRefrigeratorById(id);

		return RefrigeratorRes.from(refrigerator);
	}

	@Transactional(readOnly = true)
	public RefrigeratorsRes findAll(String sortBy, Pageable pageable, String lastPageName, String lastPageUpdatedAt, String username) {
		Member member = getMemberByUsername(username);

		LocalDateTime LastUpdatedAt = getLastPageUpdatedAt(lastPageUpdatedAt);

		RefrigeratorSortType refrigeratorSortType = RefrigeratorSortType.findRefrigeratorSortType(sortBy);
		Slice<Refrigerator> refrigerators = null;

		switch (refrigeratorSortType) {
			case NAME_ASC ->
				refrigerators = refrigeratorRepository.findAllByMemberIdOrderByNameAsc(member.getId(), pageable, lastPageName, LastUpdatedAt);
			case NAME_DESC ->
				refrigerators = refrigeratorRepository.findAllByMemberIdOrderByNameDesc(member.getId(), pageable);
			case UPDATED_AT_ASC ->
				refrigerators = refrigeratorRepository.findAllByMemberIdOrderByUpdatedAtAsc(member.getId(), pageable);
			case UPDATED_AT_DESC ->
				refrigerators = refrigeratorRepository.findAllByMemberIdOrderByUpdatedAtDesc(member.getId(), pageable);
		}

		return RefrigeratorsRes.from(refrigerators);
	}

	public RefrigeratorRes update(Long id, RefrigeratorReq refrigeratorReq, String username) {
		Member member = getMemberByUsername(username);

		Refrigerator refrigerator = getRefrigeratorByIdAndMemberId(id, member.getId());

		Refrigerator updatedRefrigerator = RefrigeratorReq.toRefrigerator(refrigeratorReq, member);

		refrigerator.update(updatedRefrigerator);

		return RefrigeratorRes.from(refrigerator);
	}

	public void delete(Long id, String username) {
		Member member = getMemberByUsername(username);

		Refrigerator refrigerator = getRefrigeratorByIdAndMemberId(id, member.getId());

		refrigeratorRepository.delete(refrigerator);
	}

	private Member getMemberByUsername(String username) {

		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);

				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}

	private Refrigerator getRefrigeratorById(Long refrigeratorId) {

		return refrigeratorRepository.findById(refrigeratorId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_REFRIGERATOR_BY_ID: {}", refrigeratorId);

				return new EntityNotFoundException(ErrorCode.NOT_FOUND_REFRIGERATOR);
			});
	}

	private Refrigerator getRefrigeratorByIdAndMemberId(Long refrigeratorId, Long memberId) {

		return refrigeratorRepository.findByIdAndMemberId(refrigeratorId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_REFRIGERATOR_BY_ID_AND_MEMBER_ID: refrigeratorId = {}, memberId = {}", refrigeratorId, memberId);

				return new EntityNotFoundException(ErrorCode.NOT_FOUND_REFRIGERATOR);
			});
	}

	private LocalDateTime getLastPageUpdatedAt(String lastPageUpdatedAt) {

		return Optional.ofNullable(lastPageUpdatedAt)
			.map(date -> LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")))
			.orElse(null);
	}
}
