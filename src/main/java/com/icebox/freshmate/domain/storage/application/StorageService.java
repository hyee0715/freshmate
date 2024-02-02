package com.icebox.freshmate.domain.storage.application;

import static com.icebox.freshmate.global.error.ErrorCode.INVALID_LAST_PAGE_UPDATED_AT_FORMAT;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_REFRIGERATOR;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_STORAGE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.refrigerator.domain.RefrigeratorRepository;
import com.icebox.freshmate.domain.storage.application.dto.request.StorageCreateReq;
import com.icebox.freshmate.domain.storage.application.dto.request.StorageUpdateReq;
import com.icebox.freshmate.domain.storage.application.dto.response.StorageRes;
import com.icebox.freshmate.domain.storage.application.dto.response.StoragesRes;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageRepository;
import com.icebox.freshmate.domain.storage.domain.StorageType;
import com.icebox.freshmate.global.error.exception.BusinessException;
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

	public StorageRes create(StorageCreateReq storageCreateReq, String username) {
		Member member = getMemberByUsername(username);

		Refrigerator refrigerator = getRefrigeratorByIdAndMemberId(storageCreateReq.refrigeratorId(), member.getId());

		Storage storage = StorageCreateReq.toStorage(storageCreateReq, refrigerator);
		Storage savedStorage = storageRepository.save(storage);

		return StorageRes.from(savedStorage);
	}

	@Transactional(readOnly = true)
	public StorageRes findById(Long id) {
		Storage storage = getStorageById(id);

		return StorageRes.from(storage);
	}

	@Transactional(readOnly = true)
	public StoragesRes findAllByRefrigeratorId(Long refrigeratorId, String sortBy, String storageType, Pageable pageable, String lastPageName, String lastPageUpdatedAt, String username) {
		Member member = getMemberByUsername(username);
		Refrigerator refrigerator = getRefrigeratorByIdAndMemberId(refrigeratorId, member.getId());

		LocalDateTime lastUpdatedAt = getLastPageUpdatedAt(lastPageUpdatedAt);

		StorageType type = findStorageType(storageType);

		Slice<Storage> storages = findStorages(type, refrigerator, pageable, lastPageName, lastUpdatedAt, sortBy);

		return StoragesRes.from(storages);
	}

	public StorageRes update(Long storageId, StorageUpdateReq storageUpdateReq, String username) {
		Member member = getMemberByUsername(username);
		Storage storage = getStorageByIdAndMemberId(storageId, member.getId());

		Storage updateStorage = StorageUpdateReq.toStorage(storageUpdateReq, storage.getRefrigerator());
		storage.update(updateStorage);

		return StorageRes.from(storage);
	}

	public void delete(Long storageId, String username) {
		Member member = getMemberByUsername(username);
		Storage storage = getStorageByIdAndMemberId(storageId, member.getId());

		storageRepository.delete(storage);
	}

	private Storage getStorageByIdAndMemberId(Long storageId, Long memberId) {

		return storageRepository.findByIdAndMemberId(storageId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORAGE_BY_ID_AND_MEMBER_ID : storageId = {}, memberId = {}", storageId, memberId);

				return new EntityNotFoundException(NOT_FOUND_STORAGE);
			});
	}

	private Storage getStorageById(Long storageId) {

		return storageRepository.findById(storageId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORAGE_BY_ID : {}", storageId);

				return new EntityNotFoundException(NOT_FOUND_STORAGE);
			});
	}

	private Refrigerator getRefrigeratorByIdAndMemberId(Long refrigeratorId, Long memberId) {

		return refrigeratorRepository.findByIdAndMemberId(refrigeratorId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_REFRIGERATOR_BY_ID_AND_MEMBER_ID : refrigeratorId = {}, memberId = {}", refrigeratorId, memberId);

				return new EntityNotFoundException(NOT_FOUND_REFRIGERATOR);
			});
	}

	private Member getMemberByUsername(String username) {

		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);

				return new EntityNotFoundException(NOT_FOUND_MEMBER);
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

	private StorageType findStorageType(String storageType) {
		try {
			return StorageType.findStorageType(storageType);
		} catch (BusinessException e) {
			return null;
		}
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

	private Slice<Storage> findStorages(StorageType storageType, Refrigerator refrigerator, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt, String sortBy) {

		return Optional.ofNullable(storageType)
			.map(validStorageType -> storageRepository.findAllByRefrigeratorIdAndStorageTypeOrderBySortCondition(refrigerator.getId(), validStorageType, pageable, lastPageName, lastPageUpdatedAt, sortBy))
			.orElse(storageRepository.findAllByRefrigeratorIdOrderBySortCondition(refrigerator.getId(), pageable, lastPageName, lastPageUpdatedAt, sortBy));
	}
}
