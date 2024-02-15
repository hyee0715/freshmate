package com.icebox.freshmate.domain.recipebucket.domain;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeBucketRepository extends JpaRepository<RecipeBucket, Long> {

	boolean existsByRecipeId(Long recipeId);

	Slice<RecipeBucket> findAllByMemberId(Long memberId, Pageable pageable);

	Optional<RecipeBucket> findByIdAndMemberId(Long id, Long memberId);
}
