package com.icebox.freshmate.domain.storage.domain;

import java.util.Arrays;

import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum StorageType {

	FRIDGE, FREEZER;

	public static StorageType findStorageType(String storageType) {

		return Arrays.stream(StorageType.values())
			.filter(type -> type.name().equalsIgnoreCase(storageType))
			.findAny()
			.orElseThrow(() -> {
				log.error("INVALID_STORAGE_TYPE : {}", storageType);
				return new BusinessException(ErrorCode.INVALID_STORAGE_TYPE);
			});
	}
}
