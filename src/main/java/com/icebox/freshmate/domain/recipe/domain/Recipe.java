package com.icebox.freshmate.domain.recipe.domain;

import com.icebox.freshmate.domain.member.domain.Member;

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

	@Enumerated(EnumType.STRING)
	private RecipeType recipeType;

	@Column(length = 200)
	private String title;

	@Column(length = 1000)
	private String material;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Builder
	public Recipe(Member writer, String title, String material, String content) {
		this.writer = writer;
		this.owner = writer;
		this.recipeType = RecipeType.WRITTEN;
		this.title = title;
		this.material = material;
		this.content = content;
	}

	public void update(Recipe recipe) {
		this.title = recipe.title;
		this.material = recipe.material;
		this.content = recipe.content;
	}
}
