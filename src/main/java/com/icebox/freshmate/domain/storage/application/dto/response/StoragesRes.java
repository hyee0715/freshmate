package com.icebox.freshmate.domain.storage.application.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.icebox.freshmate.domain.storage.domain.Storage;

public record StoragesRes(
	List<StorageRes> storages,
	boolean hasNext
) {

	public static StoragesRes from(Slice<Storage> storages) {
		List<StorageRes> storagesRes = storages.stream()
			.map(StorageRes::from)
			.toList();

		return new StoragesRes(storagesRes, storages.hasNext());
	}
}
