package com.icebox.freshmate.domain.storage.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StorageRepositoryCustom {

	Slice<Storage> findAllByRefrigeratorIdOrderByNameAsc(Long refrigeratorId, Pageable pageable);

	Slice<Storage> findAllByRefrigeratorIdOrderByNameDesc(Long refrigeratorId, Pageable pageable);

	Slice<Storage> findAllByRefrigeratorIdOrderByUpdatedAtAsc(Long refrigeratorId, Pageable pageable);

	Slice<Storage> findAllByRefrigeratorIdOrderByUpdatedAtDesc(Long refrigeratorId, Pageable pageable);

	Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByNameAsc(Long refrigeratorId, StorageType storageType, Pageable pageable);

	Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByNameDesc(Long refrigeratorId, StorageType storageType, Pageable pageable);

	Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByUpdatedAtAsc(Long refrigeratorId, StorageType storageType, Pageable pageable);

	Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByUpdatedAtDesc(Long refrigeratorId, StorageType storageType, Pageable pageable);
}
