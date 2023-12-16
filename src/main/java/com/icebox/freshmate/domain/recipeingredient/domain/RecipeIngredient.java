package com.icebox.freshmate.domain.recipeingredient.domain;

import com.icebox.freshmate.domain.ingredient.domain.Ingredient;
import com.icebox.freshmate.domain.recipe.domain.Recipe;

import jakarta.persistence.Entity;
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
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "recipe_ingredients")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class RecipeIngredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "recipe_id")
	private Recipe recipe;

	@ManyToOne
	@JoinColumn(name = "ingredient_id")
	private Ingredient ingredient;

	@Builder
	public RecipeIngredient(Recipe recipe, Ingredient ingredient) {
		this.recipe = recipe;
		this.ingredient = ingredient;
	}
}
