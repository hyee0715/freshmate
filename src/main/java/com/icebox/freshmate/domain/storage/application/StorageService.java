package com.icebox.freshmate.domain.storage.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_REFRIGERATOR;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.refrigerator.domain.RefrigeratorRepository;
import com.icebox.freshmate.domain.storage.application.dto.request.StorageReq;
import com.icebox.freshmate.domain.storage.application.dto.response.StorageRes;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageRepository;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StorageService {

	private final StorageRepository storageRepository;
	private final RefrigeratorRepository refrigeratorRepository;
	private final MemberRepository memberRepository;

	public StorageRes create(StorageReq storageReq, String memberUsername) {
		Member member = getMember(memberUsername);

		Refrigerator refrigerator = getRefrigerator(storageReq.refrigeratorId(), member.getId());

		Storage storage = StorageReq.toStorage(storageReq, refrigerator);
		Storage savedStorage = storageRepository.save(storage);

		return StorageRes.from(savedStorage);
	}

	private Refrigerator getRefrigerator(Long refrigeratorId, Long memberId) {
		return refrigeratorRepository.findByIdAndMemberId(refrigeratorId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_REFRIGERATOR_BY_ID_AND_MEMBER_ID : refrigeratorId = {}, memberId = {}", refrigeratorId, memberId);
				return new EntityNotFoundException(NOT_FOUND_REFRIGERATOR);
			});
	}

	private Member getMember(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORE_BY_MEMBER_USERNAME : {}", username);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}
}
