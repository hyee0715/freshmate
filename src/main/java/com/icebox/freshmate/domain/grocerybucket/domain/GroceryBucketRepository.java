package com.icebox.freshmate.domain.grocerybucket.domain;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroceryBucketRepository extends JpaRepository<GroceryBucket, Long> {

	Slice<GroceryBucket> findAllByMemberId(Long memberId, Pageable pageable);

	Optional<GroceryBucket> findByIdAndMemberId(Long id, Long memberId);
}
