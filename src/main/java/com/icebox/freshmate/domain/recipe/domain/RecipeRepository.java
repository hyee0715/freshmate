package com.icebox.freshmate.domain.recipe.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeRepositoryCustom {

	Optional<Recipe> findByIdAndOwnerId(Long id, Long ownerId);
}
