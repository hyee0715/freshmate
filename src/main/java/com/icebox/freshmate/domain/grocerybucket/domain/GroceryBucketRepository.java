package com.icebox.freshmate.domain.grocerybucket.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroceryBucketRepository extends JpaRepository<GroceryBucket, Long>, GroceryBucketRepositoryCustom {

	Optional<GroceryBucket> findByIdAndMemberId(Long id, Long memberId);
}
