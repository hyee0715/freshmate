package com.icebox.freshmate.domain.recipe.application.dto.response;

import com.icebox.freshmate.domain.recipe.domain.Recipe;

public record RecipeRes(
	Long recipeId,
	Long writerId,
	String writerNickName,
	Long ownerId,
	String ownerNickName,
	String recipeType,
	String title,
	String material,
	String content
) {

	public static RecipeRes from(Recipe recipe) {

		return new RecipeRes(
			recipe.getId(),
			recipe.getWriter().getId(),
			recipe.getWriter().getNickName(),
			recipe.getOwner().getId(),
			recipe.getOwner().getNickName(),
			recipe.getRecipeType().name(),
			recipe.getTitle(),
			recipe.getMaterial(),
			recipe.getContent()
		);
	}
}
