package com.icebox.freshmate.domain.recipegrocery.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeGroceryRepository extends JpaRepository<RecipeGrocery, Long> {

	boolean existsByRecipeIdAndGroceryId(Long recipeId, Long groceryId);
}
