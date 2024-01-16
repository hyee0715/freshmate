package com.icebox.freshmate.domain.recipe.application.dto.response;

import java.util.List;

import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;

public record RecipeRes(
	Long recipeId,
	Long writerId,
	String writerNickName,
	Long ownerId,
	String ownerNickName,
	String recipeType,
	Long originalRecipeId,
	String title,
	String content,

	List<RecipeGroceryRes> materials,
	List<ImageRes> images
) {

	public static RecipeRes of(Recipe recipe, List<RecipeGroceryRes> recipeGroceriesRes, List<ImageRes> imagesRes) {

		return new RecipeRes(
			recipe.getId(),
			recipe.getWriter().getId(),
			recipe.getWriter().getNickName(),
			recipe.getOwner().getId(),
			recipe.getOwner().getNickName(),
			recipe.getRecipeType().name(),
			recipe.getOriginalRecipeId(),
			recipe.getTitle(),
			recipe.getContent(),
			recipeGroceriesRes,
			imagesRes
		);
	}
}
