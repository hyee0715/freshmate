package com.icebox.freshmate.domain.grocery.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.icebox.freshmate.domain.member.domain.QMember;
import com.icebox.freshmate.domain.refrigerator.domain.QRefrigerator;
import com.icebox.freshmate.domain.storage.domain.QStorage;
import com.icebox.freshmate.global.util.SortTypeUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroceryRepositoryImpl implements GroceryRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QGrocery grocery = QGrocery.grocery;
	private final QStorage storage = QStorage.storage;
	private final QRefrigerator refrigerator = QRefrigerator.refrigerator;
	private final QMember member = QMember.member;

	@Override
	public Slice<Grocery> findAllByWhereConditionsAndOrderBySortConditions(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable, String sortBy, String lastPageName, LocalDate lastPageExpirationDate, LocalDateTime lastPageUpdatedAt) {
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);
		BooleanExpression[] booleanExpressions = getBooleanExpressionsByConditions(storageId, memberId, groceryType, groceryExpirationType, lastPageName, lastPageExpirationDate, lastPageUpdatedAt, sortBy);

		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(booleanExpressions)
			.orderBy(orderSpecifier)
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	private Slice<Grocery> checkLastPage(Pageable pageable, List<Grocery> groceries) {
		boolean hasNext = false;

		if (groceries.size() > pageable.getPageSize()) {
			groceries.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(groceries, pageable, hasNext);
	}

	private BooleanExpression[] getBooleanExpressionsByConditions(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, String lastPageName, LocalDate lastPageExpirationDate, LocalDateTime lastPageUpdatedAt, String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		return switch (sortType) {
			case NAME_ASC -> createBooleanExpressions(storageId, memberId, groceryType, groceryExpirationType, gtGroceryNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt));
			case NAME_DESC -> createBooleanExpressions(storageId, memberId, groceryType, groceryExpirationType, ltGroceryNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt));
			case UPDATED_AT_ASC -> createBooleanExpressions(storageId, memberId, groceryType, groceryExpirationType, gtGroceryUpdatedAt(lastPageUpdatedAt));
			case UPDATED_AT_DESC -> createBooleanExpressions(storageId, memberId, groceryType, groceryExpirationType, ltGroceryUpdatedAt(lastPageUpdatedAt));
			case EXPIRATION_DATE_ASC -> createBooleanExpressions(storageId, memberId, groceryType, groceryExpirationType, gtGroceryExpirationDateAndLtUpdatedAt(lastPageExpirationDate, lastPageUpdatedAt));
			default -> createBooleanExpressions(storageId, memberId, groceryType, groceryExpirationType, ltGroceryExpirationDateAndLtUpdatedAt(lastPageExpirationDate, lastPageUpdatedAt));
		};
	}

	private BooleanExpression[] createBooleanExpressions(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, BooleanExpression booleanExpression) {

		return Optional.ofNullable(groceryType)
			.map(type -> Optional.ofNullable(groceryExpirationType)
				.map(expirationType -> new BooleanExpression[]{storage.id.eq(storageId), member.id.eq(memberId), grocery.groceryType.eq(type), grocery.groceryExpirationType.eq(expirationType), booleanExpression })
				.orElse(new BooleanExpression[]{storage.id.eq(storageId), member.id.eq(memberId), grocery.groceryType.eq(type), booleanExpression})
			)
			.orElseGet(() -> Optional.ofNullable(groceryExpirationType)
				.map(expirationType -> new BooleanExpression[]{storage.id.eq(storageId), member.id.eq(memberId), grocery.groceryExpirationType.eq(expirationType), booleanExpression})
				.orElse(new BooleanExpression[]{storage.id.eq(storageId), member.id.eq(memberId), booleanExpression})
			);
	}

	private OrderSpecifier<?>[] getOrderSpecifier(String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		return switch (sortType) {
			case NAME_ASC -> createOrderSpecifier(grocery.name.asc(), grocery.updatedAt.desc(), null);
			case NAME_DESC -> createOrderSpecifier(grocery.name.desc(), grocery.updatedAt.desc(), null);
			case UPDATED_AT_ASC -> createOrderSpecifier(null, grocery.updatedAt.asc(), null);
			case UPDATED_AT_DESC -> createOrderSpecifier(null, grocery.updatedAt.desc(), null);
			case EXPIRATION_DATE_ASC -> createOrderSpecifier(null, grocery.updatedAt.desc(), grocery.expirationDate.asc());
			default -> createOrderSpecifier(null, grocery.updatedAt.desc(), grocery.expirationDate.desc());
		};
	}

	private OrderSpecifier<?>[] createOrderSpecifier(OrderSpecifier<String> nameOrderSpecifier, OrderSpecifier<LocalDateTime> updatedAtOrderSpecifier, OrderSpecifier<LocalDate> expirationDateOrderSpecifier) {

		return Optional.ofNullable(nameOrderSpecifier)
			.map(name -> Optional.ofNullable(expirationDateOrderSpecifier)
				.map(expiration -> new OrderSpecifier<?>[]{updatedAtOrderSpecifier})
				.orElse(new OrderSpecifier<?>[]{nameOrderSpecifier, updatedAtOrderSpecifier}))
			.orElseGet(() -> Optional.ofNullable(expirationDateOrderSpecifier)
				.map(expiration -> new OrderSpecifier<?>[]{expiration, updatedAtOrderSpecifier})
				.orElse(new OrderSpecifier<?>[]{updatedAtOrderSpecifier}));
	}

	private BooleanExpression gtGroceryNameAndLtUpdatedAt(String groceryName, LocalDateTime updatedAt) {
		if (groceryName == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = grocery.name.gt(groceryName);

		predicate = predicate.or(
			grocery.name.eq(groceryName)
				.and(grocery.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression ltGroceryNameAndLtUpdatedAt(String groceryName, LocalDateTime updatedAt) {
		if (groceryName == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = grocery.name.lt(groceryName);

		predicate = predicate.or(
			grocery.name.eq(groceryName)
				.and(grocery.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression gtGroceryExpirationDateAndLtUpdatedAt(LocalDate groceryExpirationDate, LocalDateTime updatedAt) {
		if (groceryExpirationDate == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = grocery.expirationDate.gt(groceryExpirationDate);

		predicate = predicate.or(
			grocery.expirationDate.eq(groceryExpirationDate)
				.and(grocery.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression ltGroceryExpirationDateAndLtUpdatedAt(LocalDate groceryExpirationDate, LocalDateTime updatedAt) {
		if (groceryExpirationDate == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = grocery.expirationDate.lt(groceryExpirationDate);

		predicate = predicate.or(
			grocery.expirationDate.eq(groceryExpirationDate)
				.and(grocery.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression gtGroceryUpdatedAt(LocalDateTime updatedAt) {

		return Optional.ofNullable(updatedAt)
			.map(grocery.updatedAt::gt)
			.orElse(null);
	}

	private BooleanExpression ltGroceryUpdatedAt(LocalDateTime updatedAt) {

		return Optional.ofNullable(updatedAt)
			.map(grocery.updatedAt::lt)
			.orElse(null);
	}
}
