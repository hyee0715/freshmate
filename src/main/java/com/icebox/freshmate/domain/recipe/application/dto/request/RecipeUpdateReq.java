package com.icebox.freshmate.domain.recipe.application.dto.request;

import org.hibernate.validator.constraints.Length;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeType;

import jakarta.validation.constraints.NotBlank;

public record RecipeUpdateReq(
	@NotBlank(message = "레시피 제목을 입력해주세요.")
	@Length(min = 1, max = 200, message = "레시피 제목은 1자 이상 200자 이하로 등록 가능합니다.")
	String title,

	@NotBlank(message = "레시피 내용을 입력해주세요.")
	@Length(min = 1, message = "레시피 내용은 1자 이상부터 등록 가능합니다.")
	String content
) {

	public static Recipe toRecipe(RecipeUpdateReq recipeUpdateReq, Member writer) {

		return Recipe.builder()
			.writer(writer)
			.owner(writer)
			.recipeType(RecipeType.WRITTEN)
			.title(recipeUpdateReq.title())
			.content(recipeUpdateReq.content())
			.build();
	}
}
