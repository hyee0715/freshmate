package com.icebox.freshmate.domain.postrecipe.domain;

import com.icebox.freshmate.domain.post.domain.Post;
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
@Table(name = "post_recipes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class PostRecipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne
	@JoinColumn(name = "recipe_id")
	private Recipe recipe;

	@Builder
	public PostRecipe(Post post, Recipe recipe) {
		this.post = post;
		this.recipe = recipe;
	}
}
