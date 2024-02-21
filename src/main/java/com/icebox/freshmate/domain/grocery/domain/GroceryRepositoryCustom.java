package com.icebox.freshmate.domain.grocery.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GroceryRepositoryCustom {

	Slice<Grocery> findAllByWhereConditionsAndOrderBySortConditions(Long storageId, Long memberId, String keyword, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable, String sortBy, String lastPageName, LocalDate lastPageExpirationDate, LocalDateTime lastPageUpdatedAt);
}
