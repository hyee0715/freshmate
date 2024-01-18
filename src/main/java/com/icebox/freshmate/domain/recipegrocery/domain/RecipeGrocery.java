package com.icebox.freshmate.domain.recipegrocery.domain;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.global.BaseEntity;

import jakarta.persistence.Column;
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
@Table(name = "recipe_groceries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class RecipeGrocery extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "recipe_id")
	private Recipe recipe;

	@ManyToOne
	@JoinColumn(name = "grocery_id")
	private Grocery grocery;

	@Column(length = 100)
	private String groceryName;

	@Column(length = 100)
	private String groceryQuantity;

	@Builder
	public RecipeGrocery(Recipe recipe, Grocery grocery, String groceryName, String groceryQuantity) {
		this.recipe = recipe;
		this.grocery = grocery;
		this.groceryName = groceryName;
		this.groceryQuantity = groceryQuantity;
	}

	public void addRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	public void addGrocery(Grocery grocery) {
		this.grocery = grocery;
	}

	public void removeGrocery() {
		this.grocery = null;
	}
}
