package com.icebox.freshmate.domain.recipebucket.domain;

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
public class RecipeBucketRepositoryImpl implements RecipeBucketRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QRecipeBucket recipeBucket = QRecipeBucket.recipeBucket;
	private final QMember member = QMember.member;

	@Override
	public Slice<RecipeBucket> findAllByMemberId(Long memberId, Pageable pageable, String sortBy, String lastPageTitle, LocalDateTime lastPageCreatedAt) {
		BooleanExpression[] booleanExpressions = getBooleanExpressionByMemberId(memberId, lastPageTitle, lastPageCreatedAt, sortBy);
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<RecipeBucket> recipeBuckets = queryFactory.select(recipeBucket)
			.from(recipeBucket)
			.join(recipeBucket.member, member).fetchJoin()
			.where(booleanExpressions)
			.orderBy(orderSpecifier)
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, recipeBuckets);
	}

	private BooleanExpression[] getBooleanExpressionByMemberId(Long memberId, String lastPageTitle, LocalDateTime lastPageCreatedAt, String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		return switch (sortType) {
			case TITLE_ASC ->
				createBooleanExpressions(memberId, gtRecipeTitleAndLtCreatedAt(lastPageTitle, lastPageCreatedAt));
			case TITLE_DESC ->
				createBooleanExpressions(memberId, ltRecipeTitleAndLtCreatedAt(lastPageTitle, lastPageCreatedAt));
			case CREATED_AT_ASC -> createBooleanExpressions(memberId, gtRecipeBucketCreatedAt(lastPageCreatedAt));
			default -> createBooleanExpressions(memberId, ltRecipeBucketUpdatedAt(lastPageCreatedAt));
		};
	}

	private BooleanExpression[] createBooleanExpressions(Long memberId, BooleanExpression booleanExpression) {

		return new BooleanExpression[]{recipeBucket.member.id.eq(memberId), booleanExpression};
	}

	private OrderSpecifier<?>[] getOrderSpecifier(String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		return switch (sortType) {
			case TITLE_ASC -> createOrderSpecifier(recipeBucket.recipe.title.asc(), recipeBucket.createdAt.desc());
			case TITLE_DESC -> createOrderSpecifier(recipeBucket.recipe.title.desc(), recipeBucket.createdAt.desc());
			case CREATED_AT_ASC -> createOrderSpecifier(null, recipeBucket.createdAt.asc());
			default -> createOrderSpecifier(null, recipeBucket.createdAt.desc());
		};
	}

	private OrderSpecifier<?>[] createOrderSpecifier(OrderSpecifier<String> titleOrderSpecifier, OrderSpecifier<LocalDateTime> createdAtOrderSpecifier) {

		return Optional.ofNullable(titleOrderSpecifier)
			.map(titleSpecifier -> new OrderSpecifier<?>[]{titleSpecifier, createdAtOrderSpecifier})
			.orElse(new OrderSpecifier<?>[]{createdAtOrderSpecifier});
	}

	private Slice<RecipeBucket> checkLastPage(Pageable pageable, List<RecipeBucket> recipeBuckets) {
		boolean hasNext = false;

		if (recipeBuckets.size() > pageable.getPageSize()) {
			recipeBuckets.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(recipeBuckets, pageable, hasNext);
	}

	private BooleanExpression gtRecipeTitleAndLtCreatedAt(String recipeTitle, LocalDateTime createdAt) {
		if (recipeTitle == null || createdAt == null) {
			return null;
		}

		BooleanExpression predicate = recipeBucket.recipe.title.gt(recipeTitle);

		predicate = predicate.or(
			recipeBucket.recipe.title.eq(recipeTitle)
				.and(recipeBucket.createdAt.lt(createdAt))
		);

		return predicate;
	}

	private BooleanExpression ltRecipeTitleAndLtCreatedAt(String recipeTitle, LocalDateTime createdAt) {
		if (recipeTitle == null || createdAt == null) {
			return null;
		}

		BooleanExpression predicate = recipeBucket.recipe.title.lt(recipeTitle);

		predicate = predicate.or(
			recipeBucket.recipe.title.eq(recipeTitle)
				.and(recipeBucket.createdAt.lt(createdAt))
		);

		return predicate;
	}

	private BooleanExpression gtRecipeBucketCreatedAt(LocalDateTime createdAt) {

		return Optional.ofNullable(createdAt)
			.map(recipeBucket.createdAt::gt)
			.orElse(null);
	}

	private BooleanExpression ltRecipeBucketUpdatedAt(LocalDateTime createdAt) {

		return Optional.ofNullable(createdAt)
			.map(recipeBucket.createdAt::lt)
			.orElse(null);
	}
}
