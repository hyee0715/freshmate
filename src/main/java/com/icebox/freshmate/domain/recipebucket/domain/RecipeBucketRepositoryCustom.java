package com.icebox.freshmate.domain.recipebucket.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RecipeBucketRepositoryCustom {

	Slice<RecipeBucket> findAllByMemberId(Long memberId, Pageable pageable, String sortBy);
}
