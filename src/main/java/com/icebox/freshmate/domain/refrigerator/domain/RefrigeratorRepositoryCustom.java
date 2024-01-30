package com.icebox.freshmate.domain.refrigerator.domain;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RefrigeratorRepositoryCustom {

	Slice<Refrigerator> findAllByMemberIdOrderByNameAsc(Long memberId, Pageable pageable, String lastPageName, LocalDateTime lastUpdatedAt);

	Slice<Refrigerator> findAllByMemberIdOrderByNameDesc(Long memberId, Pageable pageable, String lastPageName, LocalDateTime lastUpdatedAt);

	Slice<Refrigerator> findAllByMemberIdOrderByUpdatedAtAsc(Long memberId, Pageable pageable);

	Slice<Refrigerator> findAllByMemberIdOrderByUpdatedAtDesc(Long memberId, Pageable pageable);
}
