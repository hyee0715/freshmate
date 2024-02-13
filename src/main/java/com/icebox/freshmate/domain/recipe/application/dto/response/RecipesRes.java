package com.icebox.freshmate.domain.recipe.application.dto.response;

import java.util.List;
import org.springframework.data.domain.Slice;

import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeImage;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;

public record RecipesRes(
	List<RecipeRes> recipes,
	boolean hasNext
) {

	public static RecipesRes from(Slice<Recipe> recipes) {
		List<RecipeRes> recipesRes = recipes.stream()
			.map(recipe -> {
				List<RecipeGroceryRes> recipeGroceryRes = getRecipeGroceryResList(recipe);
				List<ImageRes> recipeImagesRes = getRecipeImagesRes(recipe);

				return RecipeRes.of(recipe, recipeGroceryRes, recipeImagesRes);
			})
			.toList();

		return new RecipesRes(recipesRes, recipes.hasNext());
	}

	public static RecipesRes fromRecipeGroceries(Slice<RecipeGrocery> recipeGroceries) {
		List<RecipeRes> recipesRes = recipeGroceries.stream()
			.map(RecipeGrocery::getRecipe)
			.map(recipe -> {
				List<RecipeGroceryRes> recipeGroceryRes = getRecipeGroceryResList(recipe);
				List<ImageRes> recipeImagesRes = getRecipeImagesRes(recipe);

				return RecipeRes.of(recipe, recipeGroceryRes, recipeImagesRes);
			})
			.toList();

		return new RecipesRes(recipesRes, recipeGroceries.hasNext());
	}

	private static List<RecipeGroceryRes> getRecipeGroceryResList(Recipe recipe) {

		return recipe.getRecipeGroceries().stream()
			.map(RecipeGroceryRes::from)
			.toList();
	}

	private static List<ImageRes> getRecipeImagesRes(Recipe recipe) {
		List<RecipeImage> recipeImages = recipe.getRecipeImages();

		return recipeImages.stream()
			.map(recipeImage -> ImageRes.of(recipeImage.getFileName(), recipeImage.getPath()))
			.toList();
	}
}
