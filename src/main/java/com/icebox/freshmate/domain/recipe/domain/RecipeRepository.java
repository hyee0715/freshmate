package com.icebox.freshmate.domain.recipe.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

	List<Recipe> findAllByWriterId(Long writerId);

	List<Recipe> findAllByOwnerId(Long ownerId);

	Optional<Recipe> findByIdAndOwnerId(Long id, Long ownerId);
}
