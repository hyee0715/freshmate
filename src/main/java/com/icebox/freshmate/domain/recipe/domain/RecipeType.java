package com.icebox.freshmate.domain.recipe.domain;

public enum RecipeType {
	SCRAPED("Scraped Recipe"),
	WRITTEN("Written Recipe");

	private final String displayName;

	RecipeType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
