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
	public Slice<Grocery> findAllByStorageIdAndMemberIdOrderBySortCondition(Long storageId, Long memberId, Pageable pageable, String sortBy) {
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId))
			.orderBy(orderSpecifier)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortCondition(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable, String sortBy) {
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(orderSpecifier)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);

	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortCondition(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable, String sortBy) {
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryType.eq(groceryType),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(orderSpecifier)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderBySortCondition(Long storageId, Long memberId, GroceryType groceryType, Pageable pageable, String sortBy) {
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryType.eq(groceryType))
			.orderBy(orderSpecifier)
			.offset(pageable.getOffset())
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
}
