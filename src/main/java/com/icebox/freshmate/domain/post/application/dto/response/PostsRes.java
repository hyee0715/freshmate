package com.icebox.freshmate.domain.post.application.dto.response;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;

import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.post.domain.PostImage;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;

public record PostsRes(
	List<PostRes> posts,
	boolean hasNext
) {

	public static PostsRes from(Slice<Post> posts) {
		List<PostRes> postsRes = posts.stream()
			.map(post -> {
				List<RecipeGroceryRes> recipeGroceryRes = getRecipeGroceryResList(post.getRecipe());
				List<ImageRes> postImagesRes = getPostImagesRes(post);

				return PostRes.of(post, recipeGroceryRes, postImagesRes);
			})
			.toList();

		return new PostsRes(postsRes, posts.hasNext());
	}

	private static List<RecipeGroceryRes> getRecipeGroceryResList(Recipe recipe) {

		return Optional.ofNullable(recipe)
			.map(Recipe::getRecipeGroceries)
			.orElse(Collections.emptyList())
			.stream()
			.map(RecipeGroceryRes::from)
			.collect(Collectors.toList());
	}

	private static List<ImageRes> getPostImagesRes(Post post) {
		List<PostImage> postImages = post.getPostImages();

		return postImages.stream()
			.map(postImage -> ImageRes.of(postImage.getFileName(), postImage.getPath()))
			.toList();
	}
}
