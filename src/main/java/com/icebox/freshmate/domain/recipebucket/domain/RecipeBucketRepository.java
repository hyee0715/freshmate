package com.icebox.freshmate.domain.recipebucket.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeBucketRepository extends JpaRepository<RecipeBucket, Long>, RecipeBucketRepositoryCustom {

	boolean existsByRecipeId(Long recipeId);

	Optional<RecipeBucket> findByIdAndMemberId(Long id, Long memberId);
}
