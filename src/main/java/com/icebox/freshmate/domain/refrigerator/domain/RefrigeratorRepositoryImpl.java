package com.icebox.freshmate.domain.refrigerator.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefrigeratorRepositoryImpl implements RefrigeratorRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private QRefrigerator refrigerator = QRefrigerator.refrigerator;

	private BooleanExpression[] getBooleanExpression(Long memberId, String lastPageName, LocalDateTime lastPageUpdatedAt, String sortBy) {
		RefrigeratorSortType refrigeratorSortType = RefrigeratorSortType.findRefrigeratorSortType(sortBy);

		switch (refrigeratorSortType) {
			case NAME_ASC -> {
				return new BooleanExpression[]{
					refrigerator.member.id.eq(memberId),
					gtRefrigeratorNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt)
				};
			}

			case NAME_DESC -> {
				return new BooleanExpression[]{
					refrigerator.member.id.eq(memberId),
					ltRefrigeratorNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt)
				};
			}

			case UPDATED_AT_ASC -> {
				return new BooleanExpression[]{
					refrigerator.member.id.eq(memberId),
					gtRefrigeratorUpdatedAt(lastPageUpdatedAt)
				};
			}
		}

		return new BooleanExpression[] {
			refrigerator.member.id.eq(memberId),
			ltRefrigeratorUpdatedAt(lastPageUpdatedAt)
		};
	}

	private OrderSpecifier[] getOrderSpecifier(String sortBy) {
		RefrigeratorSortType refrigeratorSortType = RefrigeratorSortType.findRefrigeratorSortType(sortBy);

		switch (refrigeratorSortType) {
			case NAME_ASC -> {
				return new OrderSpecifier[]{
					refrigerator.name.asc(), refrigerator.updatedAt.desc()
				};
			}

			case NAME_DESC -> {
				return new OrderSpecifier[]{
					refrigerator.name.desc(), refrigerator.updatedAt.desc()
				};
			}

			case UPDATED_AT_ASC -> {
				return new OrderSpecifier[]{
					refrigerator.updatedAt.asc()
				};
			}
		}

		return new OrderSpecifier[]{
			refrigerator.updatedAt.desc()
		};
	}

	@Override
	public Slice<Refrigerator> findAllByMemberIdOrderBySortCondition(Long memberId, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt, String sortBy) {
		BooleanExpression[] booleanExpression = getBooleanExpression(memberId, lastPageName, lastPageUpdatedAt, sortBy);
		OrderSpecifier[] orderSpecifier = getOrderSpecifier(sortBy);

		List<Refrigerator> refrigerators = queryFactory.select(refrigerator)
			.from(refrigerator)
			.where(booleanExpression)
			.orderBy(orderSpecifier)
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, refrigerators);
	}

	private Slice<Refrigerator> checkLastPage(Pageable pageable, List<Refrigerator> refrigerators) {
		boolean hasNext = false;

		if (refrigerators.size() > pageable.getPageSize()) {
			refrigerators.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(refrigerators, pageable, hasNext);
	}

	private BooleanExpression gtRefrigeratorNameAndLtUpdatedAt(String name, LocalDateTime updatedAt) {
		if (name == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = refrigerator.name.gt(name);

		predicate = predicate.or(
			refrigerator.name.eq(name)
				.and(refrigerator.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression ltRefrigeratorNameAndLtUpdatedAt(String name, LocalDateTime updatedAt) {
		if (name == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = refrigerator.name.lt(name);

		predicate = predicate.or(
			refrigerator.name.eq(name)
				.and(refrigerator.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression gtRefrigeratorUpdatedAt(LocalDateTime updatedAt) {
		if (updatedAt == null) {
			return null;
		}

		return refrigerator.updatedAt.gt(updatedAt);
	}

	private BooleanExpression ltRefrigeratorUpdatedAt(LocalDateTime updatedAt) {
		if (updatedAt == null) {
			return null;
		}

		return refrigerator.updatedAt.lt(updatedAt);
	}
}