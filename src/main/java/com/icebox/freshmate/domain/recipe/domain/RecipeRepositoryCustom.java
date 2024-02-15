package com.icebox.freshmate.domain.recipe.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RecipeRepositoryCustom {

	Slice<Recipe> findAllByWriterIdAndRecipeType(Long writerId, Pageable pageable, String sortBy, RecipeType recipeType);
}
