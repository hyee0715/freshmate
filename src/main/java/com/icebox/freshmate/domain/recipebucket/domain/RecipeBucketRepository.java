package com.icebox.freshmate.domain.recipebucket.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeBucketRepository extends JpaRepository<RecipeBucket, Long> {

	boolean existsByRecipeId(Long recipeId);

	List<RecipeBucket> findAllByMemberId(Long memberId);
}
