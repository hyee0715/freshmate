package com.icebox.freshmate.domain.storage.application.dto.request;

import org.hibernate.validator.constraints.Length;

import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageType;

import jakarta.validation.constraints.NotBlank;

public record StorageCreateReq(
	@NotBlank(message = "냉장고 저장소 이름을 입력하세요.")
	@Length(min = 1, max = 50, message = "냉장고 저장소 이름은 1자 이상 50자 이하로 등록 가능합니다.")
	String name,

	String storageType,

	Long refrigeratorId
) {

	public static Storage toStorage(StorageCreateReq storageCreateReq, Refrigerator refrigerator) {
		StorageType foundStorageType = StorageType.findStorageType(storageCreateReq.storageType());

		return Storage.builder()
			.refrigerator(refrigerator)
			.name(storageCreateReq.name())
			.storageType(foundStorageType)
			.build();
	}
}
