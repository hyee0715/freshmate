package com.icebox.freshmate.domain.refrigerator.domain;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RefrigeratorRepositoryCustom {

	Slice<Refrigerator> findAllByMemberIdOrderBySortCondition(Long memberId, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt, String sortBy);
}
