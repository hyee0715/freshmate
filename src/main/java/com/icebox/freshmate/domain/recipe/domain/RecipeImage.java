package com.icebox.freshmate.domain.recipe.domain;

import com.icebox.freshmate.domain.image.domain.Image;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "recipe_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeImage extends Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recipe_id")
	private Recipe recipe;

	private String path;

	@Builder
	public RecipeImage(String fileName, Recipe recipe, String path) {
		super(fileName);
		this.recipe = recipe;
		this.path = path;
	}

	public void addRecipe(Recipe recipe) {
		this.recipe = recipe;
	}
}
