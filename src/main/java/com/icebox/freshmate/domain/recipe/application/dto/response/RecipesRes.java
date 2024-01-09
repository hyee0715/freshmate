package com.icebox.freshmate.domain.recipe.application.dto.response;

import java.util.List;

import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;

public record RecipesRes(
	List<RecipeRes> recipes
) {

	public static RecipesRes from(List<Recipe> recipes) {
		List<RecipeRes> recipesRes = recipes.stream()
			.map(recipe -> {
				List<RecipeGroceryRes> recipeGroceryRes = getRecipeGroceryResList(recipe);
				return RecipeRes.of(recipe, recipeGroceryRes);
			})
			.toList();

		return new RecipesRes(recipesRes);
	}

	private static List<RecipeGroceryRes> getRecipeGroceryResList(Recipe recipe) {

		return recipe.getRecipeGroceries().stream()
			.map(RecipeGroceryRes::from)
			.toList();
	}
}
