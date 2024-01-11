package com.icebox.freshmate.domain.post.application.dto.response;

import java.util.List;

import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;

public record PostsRes(
	List<PostRes> posts
) {

	public static PostsRes from(List<Post> posts) {
		List<PostRes> postsRes = posts.stream()
			.map(post -> {
				List<RecipeGroceryRes> recipeGroceryRes = getRecipeGroceryResList(post.getRecipe());
				return PostRes.of(post, recipeGroceryRes);
			})
			.toList();

		return new PostsRes(postsRes);
	}

	private static List<RecipeGroceryRes> getRecipeGroceryResList(Recipe recipe) {

		return recipe.getRecipeGroceries().stream()
			.map(RecipeGroceryRes::from)
			.toList();
	}
}
