package com.icebox.freshmate.domain.grocerybucket.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroceryBucketRepository extends JpaRepository<GroceryBucket, Long> {

	List<GroceryBucket> findAllByMemberId(Long memberId);

	Optional<GroceryBucket> findByIdAndMemberId(Long id, Long memberId);
}
