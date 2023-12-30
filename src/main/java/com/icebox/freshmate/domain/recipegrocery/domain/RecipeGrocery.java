package com.icebox.freshmate.domain.recipegrocery.domain;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
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
@Table(name = "recipe_grocery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class RecipeGrocery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "recipe_id")
	private Recipe recipe;

	@ManyToOne
	@JoinColumn(name = "grocery_id")
	private Grocery grocery;

	@Builder
	public RecipeGrocery(Recipe recipe, Grocery grocery) {
		this.recipe = recipe;
		this.grocery = grocery;
	}
}
