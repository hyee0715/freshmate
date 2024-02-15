package com.icebox.freshmate.domain.grocerybucket.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GroceryBucketRepositoryCustom {

	Slice<GroceryBucket> findAllByMemberId(Long memberId, Pageable pageable, String sortBy);
}
