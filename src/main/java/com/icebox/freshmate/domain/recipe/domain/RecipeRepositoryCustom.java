package com.icebox.freshmate.domain.recipe.domain;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RecipeRepositoryCustom {

	Slice<Recipe> findAllByMemberIdAndRecipeType(Long writerId, String searchType, String keyword, Pageable pageable, String sortBy, RecipeType recipeType, String lastPageTitle, LocalDateTime lastPageUpdatedAt);
}
