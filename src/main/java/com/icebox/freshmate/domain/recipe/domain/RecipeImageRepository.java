package com.icebox.freshmate.domain.recipe.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeImageRepository extends JpaRepository<RecipeImage, Long> {

	List<RecipeImage> findAllByRecipeId(Long recipeId);
}
