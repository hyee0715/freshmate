package com.icebox.freshmate.domain.recipebucket.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.icebox.freshmate.domain.recipebucket.domain.RecipeBucket;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;

public record RecipeBucketRes(
	Long recipeBucketId,
	Long recipeId,
	Long writerId,
	String writerNickName,
	String recipeType,
	Long originalRecipeId,
	String recipeTitle,
	String recipeContent,
	List<RecipeGroceryRes> materials,
	Long memberId,
	String memberNickName,
	LocalDateTime createdAt
) {

	public static RecipeBucketRes of(RecipeBucket recipeBucket, List<RecipeGroceryRes> recipeGroceriesRes) {

		return new RecipeBucketRes(
			recipeBucket.getId(),
			recipeBucket.getRecipe().getId(),
			recipeBucket.getRecipe().getWriter().getId(),
			recipeBucket.getRecipe().getWriter().getNickName(),
			recipeBucket.getRecipe().getRecipeType().name(),
			recipeBucket.getRecipe().getOriginalRecipeId(),
			recipeBucket.getRecipe().getTitle(),
			recipeBucket.getRecipe().getContent(),
			recipeGroceriesRes,
			recipeBucket.getMember().getId(),
			recipeBucket.getMember().getNickName(),
			recipeBucket.getCreatedAt()
		);
	}
}
