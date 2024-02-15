package com.icebox.freshmate.domain.grocerybucket.domain;

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
public class GroceryBucketRepositoryImpl implements GroceryBucketRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QGroceryBucket groceryBucket = QGroceryBucket.groceryBucket;
	private final QMember member = QMember.member;

	@Override
	public Slice<GroceryBucket> findAllByMemberId(Long memberId, Pageable pageable, String sortBy, String lastPageName, LocalDateTime lastPageUpdatedAt) {
		BooleanExpression[] booleanExpressions = getBooleanExpressionByMemberId(memberId, lastPageName, lastPageUpdatedAt, sortBy);
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<GroceryBucket> groceryBuckets = queryFactory.select(groceryBucket)
			.from(groceryBucket)
			.join(groceryBucket.member, member).fetchJoin()
			.where(booleanExpressions)
			.orderBy(orderSpecifier)
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceryBuckets);
	}

	private BooleanExpression[] getBooleanExpressionByMemberId(Long memberId, String lastPageTitle, LocalDateTime lastPageUpdatedAt, String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		return switch (sortType) {
			case NAME_ASC ->
				createBooleanExpressions(memberId, gtGroceryNameAndLtUpdatedAt(lastPageTitle, lastPageUpdatedAt));
			case NAME_DESC ->
				createBooleanExpressions(memberId, ltGroceryNameAndLtUpdatedAt(lastPageTitle, lastPageUpdatedAt));
			case UPDATED_AT_ASC -> createBooleanExpressions(memberId, gtGroceryBucketUpdatedAt(lastPageUpdatedAt));
			default -> createBooleanExpressions(memberId, ltGroceryBucketUpdatedAt(lastPageUpdatedAt));
		};
	}

	private BooleanExpression[] createBooleanExpressions(Long memberId, BooleanExpression booleanExpression) {

		return new BooleanExpression[]{groceryBucket.member.id.eq(memberId), booleanExpression};
	}

	private OrderSpecifier<?>[] getOrderSpecifier(String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		return switch (sortType) {
			case NAME_ASC -> createOrderSpecifier(groceryBucket.groceryName.asc(), groceryBucket.updatedAt.desc());
			case NAME_DESC -> createOrderSpecifier(groceryBucket.groceryName.desc(), groceryBucket.updatedAt.desc());
			case UPDATED_AT_ASC -> createOrderSpecifier(null, groceryBucket.updatedAt.asc());
			default -> createOrderSpecifier(null, groceryBucket.updatedAt.desc());
		};
	}

	private OrderSpecifier<?>[] createOrderSpecifier(OrderSpecifier<String> nameOrderSpecifier, OrderSpecifier<LocalDateTime> updatedAtOrderSpecifier) {

		return Optional.ofNullable(nameOrderSpecifier)
			.map(nameSpecifier -> new OrderSpecifier<?>[]{nameSpecifier, updatedAtOrderSpecifier})
			.orElse(new OrderSpecifier<?>[]{updatedAtOrderSpecifier});
	}

	private Slice<GroceryBucket> checkLastPage(Pageable pageable, List<GroceryBucket> groceryBucket) {
		boolean hasNext = false;

		if (groceryBucket.size() > pageable.getPageSize()) {
			groceryBucket.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(groceryBucket, pageable, hasNext);
	}

	private BooleanExpression gtGroceryNameAndLtUpdatedAt(String groceryName, LocalDateTime updatedAt) {
		if (groceryName == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = groceryBucket.groceryName.gt(groceryName);

		predicate = predicate.or(
			groceryBucket.groceryName.eq(groceryName)
				.and(groceryBucket.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression ltGroceryNameAndLtUpdatedAt(String groceryName, LocalDateTime updatedAt) {
		if (groceryName == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = groceryBucket.groceryName.lt(groceryName);

		predicate = predicate.or(
			groceryBucket.groceryName.eq(groceryName)
				.and(groceryBucket.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression gtGroceryBucketUpdatedAt(LocalDateTime updatedAt) {

		return Optional.ofNullable(updatedAt)
			.map(groceryBucket.updatedAt::gt)
			.orElse(null);
	}

	private BooleanExpression ltGroceryBucketUpdatedAt(LocalDateTime updatedAt) {

		return Optional.ofNullable(updatedAt)
			.map(groceryBucket.updatedAt::lt)
			.orElse(null);
	}
}
