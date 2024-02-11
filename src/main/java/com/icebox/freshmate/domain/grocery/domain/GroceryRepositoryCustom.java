package com.icebox.freshmate.domain.grocery.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GroceryRepositoryCustom {

	Slice<Grocery> findAllByStorageIdAndMemberIdOrderBySortCondition(Long storageId, Long memberId, Pageable pageable, String sortBy, String lastPageName, LocalDateTime lastPageUpdatedAt);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortCondition(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable, String sortBy, String lastPageName, LocalDate lastPageExpirationDate, LocalDateTime lastPageUpdatedAt);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortCondition(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable, String sortBy, String lastPageName, LocalDate lastPageExpirationDate, LocalDateTime lastPageUpdatedAt);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderBySortCondition(Long storageId, Long memberId, GroceryType groceryType, Pageable pageable, String sortBy, String lastPageName, LocalDateTime lastPageUpdatedAt);
}
