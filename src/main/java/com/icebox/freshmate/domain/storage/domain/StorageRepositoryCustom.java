package com.icebox.freshmate.domain.storage.domain;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StorageRepositoryCustom {

	Slice<Storage> findAllByRefrigeratorIdOrderByNameAsc(Long refrigeratorId, Pageable pageable, String lastPageName, LocalDateTime lastUpdatedAt);

	Slice<Storage> findAllByRefrigeratorIdOrderByNameDesc(Long refrigeratorId, Pageable pageable, String lastPageName, LocalDateTime lastUpdatedAt);

	Slice<Storage> findAllByRefrigeratorIdOrderByUpdatedAtAsc(Long refrigeratorId, Pageable pageable, LocalDateTime lastUpdatedAt);

	Slice<Storage> findAllByRefrigeratorIdOrderByUpdatedAtDesc(Long refrigeratorId, Pageable pageable, LocalDateTime lastUpdatedAt);

	Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByNameAsc(Long refrigeratorId, StorageType storageType, Pageable pageable, String lastPageName, LocalDateTime lastUpdatedAt);

	Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByNameDesc(Long refrigeratorId, StorageType storageType, Pageable pageable, String lastPageName, LocalDateTime lastUpdatedAt);

	Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByUpdatedAtAsc(Long refrigeratorId, StorageType storageType, Pageable pageable, LocalDateTime lastUpdatedAt);

	Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByUpdatedAtDesc(Long refrigeratorId, StorageType storageType, Pageable pageable, LocalDateTime lastUpdatedAt);
}
