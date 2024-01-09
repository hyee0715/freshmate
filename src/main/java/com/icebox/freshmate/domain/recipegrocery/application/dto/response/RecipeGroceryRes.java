package com.icebox.freshmate.domain.recipegrocery.application.dto.response;

import java.util.List;
import java.util.Optional;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;

public record RecipeGroceryRes(
	Long recipeGroceryId,

	Long recipeId,
	String recipeTitle,

	Long groceryId,
	String groceryName
) {

	public static List<RecipeGroceryRes> from(List<RecipeGrocery> recipeGroceries) {

		return recipeGroceries.stream()
			.map(RecipeGroceryRes::from)
			.toList();
	}

	public static RecipeGroceryRes from(RecipeGrocery recipeGrocery) {
		Long groceryId = getGroceryId(recipeGrocery);

		String groceryName = getGroceryName(recipeGrocery);

		return new RecipeGroceryRes(
			recipeGrocery.getId(),
			recipeGrocery.getRecipe().getId(),
			recipeGrocery.getRecipe().getTitle(),
			groceryId,
			groceryName
		);
	}

	private static Long getGroceryId(RecipeGrocery recipeGrocery) {

		return Optional.ofNullable(recipeGrocery.getGrocery())
			.map(Grocery::getId)
			.orElse(null);
	}

	private static String getGroceryName(RecipeGrocery recipeGrocery) {

		return Optional.ofNullable(recipeGrocery.getGrocery())
			.map(Grocery::getName)
			.orElse(recipeGrocery.getGroceryName());
	}
}
