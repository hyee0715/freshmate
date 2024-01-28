package com.icebox.freshmate.domain.post.application.dto.response;

import java.util.List;
import java.util.Optional;

import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;

public record PostRes(
	Long postId,
	Long memberId,
	String postTitle,
	String postContent,
	Long recipeId,
	Long recipeWriterId,
	String recipeWriterNickName,
	String recipeTitle,
	String recipeContent,

	List<RecipeGroceryRes> recipeMaterials,
	List<ImageRes> images
) {

	public static PostRes of(Post post, List<RecipeGroceryRes> recipeGroceriesRes, List<ImageRes> imagesRes) {
		Long recipeId = getRecipeId(post.getRecipe());
		Long recipeWriterId = getRecipeWriterId(post.getRecipe());
		String recipeWriterNickname = getRecipeWriterNickName(post.getRecipe());
		String recipeTitle = getRecipeTitle(post.getRecipe());
		String recipeContent = getRecipeContent(post.getRecipe());

		return new PostRes(
			post.getId(),
			post.getMember().getId(),
			post.getTitle(),
			post.getContent(),
			recipeId,
			recipeWriterId,
			recipeWriterNickname,
			recipeTitle,
			recipeContent,
			recipeGroceriesRes,
			imagesRes
		);
	}

	private static String getRecipeContent(Recipe recipe) {

		return Optional.ofNullable(recipe)
			.map(Recipe::getContent)
			.orElse(null);
	}

	private static String getRecipeTitle(Recipe recipe) {

		return Optional.ofNullable(recipe)
			.map(Recipe::getTitle)
			.orElse(null);
	}

	private static String getRecipeWriterNickName(Recipe recipe) {

		return Optional.ofNullable(recipe)
			.map(r -> r.getWriter().getNickName())
			.orElse(null);
	}

	private static Long getRecipeWriterId(Recipe recipe) {

		return Optional.ofNullable(recipe)
			.map(r -> r.getWriter().getId())
			.orElse(null);
	}

	private static Long getRecipeId(Recipe recipe) {

		return Optional.ofNullable(recipe)
			.map(Recipe::getId)
			.orElse(null);
	}
}
