package com.icebox.freshmate.domain.recipe.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.icebox.freshmate.domain.member.domain.QMember;
import com.icebox.freshmate.global.util.SortTypeUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QRecipe recipe = QRecipe.recipe;
	private final QMember member = QMember.member;

	@Override
	public Slice<Recipe> findAllByMemberIdAndRecipeType(Long memberId, Pageable pageable, String sortBy, RecipeType recipeType, String lastPageTitle, LocalDateTime lastPageUpdatedAt) {
		BooleanExpression[] booleanExpressions = getBooleanExpressionByMemberIdAndRecipeType(memberId, recipeType, lastPageTitle, lastPageUpdatedAt, sortBy);
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<Recipe> recipes = queryFactory.select(recipe)
			.from(recipe)
			.join(recipe.owner, member).fetchJoin()
			.where(booleanExpressions)
			.orderBy(orderSpecifier)
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, recipes);
	}

	private BooleanExpression[] getBooleanExpressionByMemberIdAndRecipeType(Long memberId, RecipeType recipeType, String lastPageTitle, LocalDateTime lastPageUpdatedAt, String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		return switch (sortType) {
			case TITLE_ASC ->
				createBooleanExpressions(memberId, recipeType, gtRecipeTitleAndLtUpdatedAt(lastPageTitle, lastPageUpdatedAt));
			case TITLE_DESC ->
				createBooleanExpressions(memberId, recipeType, ltRecipeTitleAndLtUpdatedAt(lastPageTitle, lastPageUpdatedAt));
			case UPDATED_AT_ASC -> createBooleanExpressions(memberId, recipeType, gtRecipeUpdatedAt(lastPageUpdatedAt));
			default -> createBooleanExpressions(memberId, recipeType, ltRecipeUpdatedAt(lastPageUpdatedAt));
		};
	}

	private BooleanExpression[] createBooleanExpressions(Long memberId, RecipeType recipeType, BooleanExpression booleanExpression) {

		return Optional.ofNullable(recipeType)
			.map(type -> new BooleanExpression[]{
				recipe.owner.id.eq(memberId),
				recipe.recipeType.eq(recipeType),
				booleanExpression
			})
			.orElse(new BooleanExpression[]{
				recipe.owner.id.eq(memberId),
				booleanExpression
			});
	}

	private OrderSpecifier<?>[] getOrderSpecifier(String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		return switch (sortType) {
			case TITLE_ASC -> createOrderSpecifier(recipe.title.asc(), recipe.updatedAt.desc());
			case TITLE_DESC -> createOrderSpecifier(recipe.title.desc(), recipe.updatedAt.desc());
			case UPDATED_AT_ASC -> createOrderSpecifier(null, recipe.updatedAt.asc());
			default -> createOrderSpecifier(null, recipe.updatedAt.desc());
		};
	}

	private OrderSpecifier<?>[] createOrderSpecifier(OrderSpecifier<String> nameOrderSpecifier, OrderSpecifier<LocalDateTime> updatedAtOrderSpecifier) {

		return Optional.ofNullable(nameOrderSpecifier)
			.map(nameSpecifier -> new OrderSpecifier<?>[]{nameSpecifier, updatedAtOrderSpecifier})
			.orElse(new OrderSpecifier<?>[]{updatedAtOrderSpecifier});
	}

	private Slice<Recipe> checkLastPage(Pageable pageable, List<Recipe> recipes) {
		boolean hasNext = false;

		if (recipes.size() > pageable.getPageSize()) {
			recipes.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(recipes, pageable, hasNext);
	}

	private BooleanExpression gtRecipeTitleAndLtUpdatedAt(String recipeTitle, LocalDateTime updatedAt) {
		if (recipeTitle == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = recipe.title.gt(recipeTitle);

		predicate = predicate.or(
			recipe.title.eq(recipeTitle)
				.and(recipe.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression ltRecipeTitleAndLtUpdatedAt(String recipeTitle, LocalDateTime updatedAt) {
		if (recipeTitle == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = recipe.title.lt(recipeTitle);

		predicate = predicate.or(
			recipe.title.eq(recipeTitle)
				.and(recipe.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression gtRecipeUpdatedAt(LocalDateTime updatedAt) {

		return Optional.ofNullable(updatedAt)
			.map(recipe.updatedAt::gt)
			.orElse(null);
	}

	private BooleanExpression ltRecipeUpdatedAt(LocalDateTime updatedAt) {

		return Optional.ofNullable(updatedAt)
			.map(recipe.updatedAt::lt)
			.orElse(null);
	}
}
