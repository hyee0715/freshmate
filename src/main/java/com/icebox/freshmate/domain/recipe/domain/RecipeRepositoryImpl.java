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
	public Slice<Recipe> findAllByWriterIdAndRecipeType(Long writerId, Pageable pageable, String sortBy, RecipeType recipeType) {
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);
		BooleanExpression[] booleanExpressions = createBooleanExpressions(writerId, recipeType);

		List<Recipe> recipes = queryFactory.select(recipe)
			.from(recipe)
			.join(recipe.writer, member).fetchJoin()
			.where(booleanExpressions)
			.orderBy(orderSpecifier)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, recipes);
	}

	private BooleanExpression[] createBooleanExpressions(Long writerId, RecipeType recipeType) {

		return Optional.ofNullable(recipeType)
			.map(type -> new BooleanExpression[]{
				recipe.writer.id.eq(writerId),
				recipe.recipeType.eq(recipeType)
			})
			.orElse(new BooleanExpression[]{
				recipe.writer.id.eq(writerId)
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
}
