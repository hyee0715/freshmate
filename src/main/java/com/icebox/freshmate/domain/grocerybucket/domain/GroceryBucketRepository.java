package com.icebox.freshmate.domain.grocerybucket.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroceryBucketRepository extends JpaRepository<GroceryBucket, Long> {

	List<GroceryBucket> findAllByMemberId(Long memberId);
}
