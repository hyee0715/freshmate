package com.icebox.freshmate.domain.ingredientbucket.domain;

import com.icebox.freshmate.domain.ingredient.domain.Ingredient;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "ingredient_buckets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class IngredientBucket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ingredient_id")
	private Ingredient ingredient;

	@Builder
	public IngredientBucket(Ingredient ingredient) {
		this.ingredient = ingredient;
	}
}
