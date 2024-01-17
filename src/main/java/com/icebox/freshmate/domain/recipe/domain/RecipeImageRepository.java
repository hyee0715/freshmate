package com.icebox.freshmate.domain.recipe.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeImageRepository extends JpaRepository<RecipeImage, Long> {

	List<RecipeImage> findAllByRecipeId(Long recipeId);

	Optional<RecipeImage> findByRecipeIdAndPath(Long recipeId, String path);
}
