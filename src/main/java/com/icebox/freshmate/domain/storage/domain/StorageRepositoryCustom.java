package com.icebox.freshmate.domain.storage.domain;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StorageRepositoryCustom {

	Slice<Storage> findAllByRefrigeratorIdOrderBySortCondition(Long refrigeratorId, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt, String sortBy);

	Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderBySortCondition(Long refrigeratorId, StorageType storageType, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt, String sortBy);
}
