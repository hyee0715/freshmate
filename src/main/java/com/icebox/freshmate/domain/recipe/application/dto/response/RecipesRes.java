package com.icebox.freshmate.domain.recipe.application.dto.response;

import java.util.List;

import com.icebox.freshmate.domain.recipe.domain.Recipe;

public record RecipesRes(
	List<RecipeRes> recipes
) {

	public static RecipesRes from(List<Recipe> recipes) {
		List<RecipeRes> recipeRes = recipes.stream()
			.map(RecipeRes::from)
			.toList();

		return new RecipesRes(recipeRes);
	}
}
