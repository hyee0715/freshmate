package com.icebox.freshmate.domain.recipe.domain;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

	Slice<Recipe> findAllByWriterId(Long writerId, Pageable pageable);

	Slice<Recipe> findAllByOwnerId(Long ownerId, Pageable pageable);

	Optional<Recipe> findByIdAndOwnerId(Long id, Long ownerId);
}
