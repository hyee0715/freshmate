package com.icebox.freshmate.domain.refrigerator.application;

import static com.icebox.freshmate.global.error.ErrorCode.INVALID_LAST_PAGE_UPDATED_AT_FORMAT;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

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
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;
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

		LocalDateTime lastUpdatedAt = getLastPageUpdatedAt(lastPageUpdatedAt);

		Slice<Refrigerator> refrigerators = refrigeratorRepository.findAllByMemberIdOrderBySortCondition(member.getId(), pageable, lastPageName, lastUpdatedAt, sortBy);

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
			.map(date -> {
				if (checkLocalDateTimeFormat(date)) {
					date += "0";
				}
				return date;
			})
			.map(date -> {
				validateLastPageUpdatedAtFormat(date);
				return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
			})
			.orElse(null);
	}

	private boolean checkLocalDateTimeFormat(String date) {
		String pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{5}";

		return Pattern.matches(pattern, date);
	}

	private void validateLastPageUpdatedAtFormat(String lastPageUpdatedAt) {
		String pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}";

		if (!Pattern.matches(pattern, lastPageUpdatedAt)) {
			log.warn("GET:READ:INVALID_LAST_PAGE_UPDATED_AT_FORMAT : {}", lastPageUpdatedAt);

			throw new BusinessException(INVALID_LAST_PAGE_UPDATED_AT_FORMAT);
		}
	}
}
