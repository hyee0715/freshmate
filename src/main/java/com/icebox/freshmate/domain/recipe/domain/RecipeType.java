package com.icebox.freshmate.domain.recipe.domain;

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
}
