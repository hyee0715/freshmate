package com.icebox.freshmate.domain.storage.application.dto.response;

import com.icebox.freshmate.domain.storage.domain.Storage;

public record StorageRes(
	Long storageId,
	String storageName,
	String storageType,
	Long refrigeratorId,
	String refrigeratorName
) {

	public static StorageRes from(Storage storage) {

		return new StorageRes(
			storage.getId(),
			storage.getName(),
			storage.getStorageType().name(),
			storage.getRefrigerator().getId(),
			storage.getRefrigerator().getName()
		);
	}
}
