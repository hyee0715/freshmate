package com.icebox.freshmate.domain.recipe.domain;

import java.util.Arrays;

import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.InvalidValueException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum RecipeType {

	SCRAPED("Scraped Recipe"),
	WRITTEN("Written Recipe");

	private final String displayName;

	RecipeType(String displayName) {
		this.displayName = displayName;
	}

	public static RecipeType findRecipeType(String recipeType) {

		return Arrays.stream(RecipeType.values())
			.filter(type -> type.name().equalsIgnoreCase(recipeType))
			.findAny()
			.orElseThrow(() -> {
				log.error("INVALID_RECIPE_TYPE : {}", recipeType);

				return new InvalidValueException(ErrorCode.INVALID_RECIPE_TYPE);
			});
	}
}
