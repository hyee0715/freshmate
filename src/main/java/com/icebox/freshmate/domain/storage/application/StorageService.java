package com.icebox.freshmate.domain.storage.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_REFRIGERATOR;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_STORAGE;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.refrigerator.domain.RefrigeratorRepository;
import com.icebox.freshmate.domain.storage.application.dto.request.StorageReq;
import com.icebox.freshmate.domain.storage.application.dto.response.StorageRes;
import com.icebox.freshmate.domain.storage.application.dto.response.StoragesRes;
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

	@Transactional(readOnly = true)
	public StorageRes findById(Long id) {
		Storage storage = getStorage(id);

		return StorageRes.from(storage);
	}

	@Transactional(readOnly = true)
	public StoragesRes findAllByRefrigeratorId(Long refrigeratorId, String memberUsername) {
		Member member = getMember(memberUsername);
		Refrigerator refrigerator = getRefrigerator(refrigeratorId, member.getId());

		List<Storage> storages = storageRepository.findAllByRefrigeratorId(refrigerator.getId());

		return StoragesRes.from(storages);
	}

	private Storage getStorage(Long storageId) {
		return storageRepository.findById(storageId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORAGE_BY_ID : {}", storageId);
				return new EntityNotFoundException(NOT_FOUND_STORAGE);
			});
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
