package com.icebox.freshmate.domain.storage.application.dto.response;

import java.util.List;

import com.icebox.freshmate.domain.storage.domain.Storage;

public record StoragesRes(
	List<StorageRes> storages
) {

	public static StoragesRes from(List<Storage> storages) {

		List<StorageRes> storagesRes = storages.stream()
			.map(StorageRes::from)
			.toList();

		return new StoragesRes(storagesRes);
	}
}
