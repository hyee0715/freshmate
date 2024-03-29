package com.icebox.freshmate.domain.recipegrocery.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeGroceryRepository extends JpaRepository<RecipeGrocery, Long> {

	boolean existsByRecipeIdAndGroceryId(Long recipeId, Long groceryId);

	List<RecipeGrocery> findAllByRecipeId(Long recipeId);

	Slice<RecipeGrocery> findAllByGroceryId(Long groceryId, Pageable pageable);
}
