package com.icebox.freshmate.domain.recipe.domain;

import java.util.ArrayList;
import java.util.List;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "recipes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "writer_id")
	private Member writer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private Member owner;

	@OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RecipeGrocery> recipeGroceries = new ArrayList<>();

	@OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RecipeImage> recipeImages = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private RecipeType recipeType;

	private Long originalRecipeId;

	@Column(length = 200)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Builder
	public Recipe(Member writer, Member owner, RecipeType recipeType, String title, String content) {
		this.writer = writer;
		this.owner = owner;
		this.recipeType = recipeType;
		this.title = title;
		this.content = content;
	}

	public void update(Recipe recipe) {
		this.title = recipe.title;
		this.content = recipe.content;
	}

	public void updateOriginalRecipeId(Long originalRecipeId) {
		this.originalRecipeId = originalRecipeId;
	}

	public void addRecipeGrocery(RecipeGrocery recipeGrocery) {
		recipeGrocery.addRecipe(this);
		this.getRecipeGroceries().add(recipeGrocery);
	}

	public void removeRecipeGrocery(RecipeGrocery recipeGrocery) {
		this.getRecipeGroceries().remove(recipeGrocery);
	}

	public void addRecipeImages(List<RecipeImage> recipeImages) {
		recipeImages.forEach(this::addRecipeImage);
	}

	public void addRecipeImage(RecipeImage recipeImage) {
		recipeImage.addRecipe(this);
		this.recipeImages.add(recipeImage);
	}
}
