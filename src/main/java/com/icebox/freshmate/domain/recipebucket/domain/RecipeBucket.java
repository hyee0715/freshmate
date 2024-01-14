package com.icebox.freshmate.domain.recipebucket.domain;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.global.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "recipe_buckets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class RecipeBucket extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recipe_id", nullable = false)
	private Recipe recipe;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Builder
	public RecipeBucket(Recipe recipe, Member member) {
		this.recipe = recipe;
		this.member = member;
	}
}
