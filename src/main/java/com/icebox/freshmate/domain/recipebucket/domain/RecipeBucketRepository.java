package com.icebox.freshmate.domain.recipebucket.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeBucketRepository extends JpaRepository<RecipeBucket, Long> {

	boolean existsByRecipeId(Long recipeId);
}
