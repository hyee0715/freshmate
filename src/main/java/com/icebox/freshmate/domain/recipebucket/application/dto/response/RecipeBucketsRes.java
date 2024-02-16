package com.icebox.freshmate.domain.recipebucket.application.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipebucket.domain.RecipeBucket;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;

public record RecipeBucketsRes(
	List<RecipeBucketRes> recipeBuckets,
	boolean hasNext
) {

	public static RecipeBucketsRes from(Slice<RecipeBucket> recipeBuckets) {

		List<RecipeBucketRes> recipeBucketsRes = recipeBuckets.stream()
			.map(recipeBucket -> {
				List<RecipeGroceryRes> recipeGroceryRes = getRecipeGroceryResList(recipeBucket.getRecipe());

				return RecipeBucketRes.of(recipeBucket, recipeGroceryRes);
			})
			.toList();

		return new RecipeBucketsRes(recipeBucketsRes, recipeBuckets.hasNext());
	}

	private static List<RecipeGroceryRes> getRecipeGroceryResList(Recipe recipe) {

		return recipe.getRecipeGroceries().stream()
			.map(RecipeGroceryRes::from)
			.toList();
	}
}
