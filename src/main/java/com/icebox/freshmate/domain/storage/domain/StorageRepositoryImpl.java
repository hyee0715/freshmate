package com.icebox.freshmate.domain.storage.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.icebox.freshmate.global.util.SortTypeUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StorageRepositoryImpl implements StorageRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QStorage storage = QStorage.storage;

	@Override
	public Slice<Storage> findAllByRefrigeratorIdOrderBySortCondition(Long refrigeratorId, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt, String sortBy) {
		BooleanExpression[] booleanExpression = getBooleanExpressionByRefrigeratorId(refrigeratorId, lastPageName, lastPageUpdatedAt, sortBy);
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(booleanExpression)
			.orderBy(orderSpecifier)
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderBySortCondition(Long refrigeratorId, StorageType storageType, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt, String sortBy) {
		BooleanExpression[] booleanExpression = getBooleanExpressionByRefrigeratorIdAndStorageType(refrigeratorId, storageType, lastPageName, lastPageUpdatedAt, sortBy);
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(booleanExpression)
			.orderBy(orderSpecifier)
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	private BooleanExpression[] getBooleanExpressionByRefrigeratorId(Long refrigeratorId, String lastPageName, LocalDateTime lastPageUpdatedAt, String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		return switch (sortType) {
			case NAME_ASC ->
				createBooleanExpressions(refrigeratorId, null, gtStorageNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt));
			case NAME_DESC ->
				createBooleanExpressions(refrigeratorId, null, ltStorageNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt));
			case UPDATED_AT_ASC ->
				createBooleanExpressions(refrigeratorId, null, gtStorageUpdatedAt(lastPageUpdatedAt));
			default -> createBooleanExpressions(refrigeratorId, null, ltStorageUpdatedAt(lastPageUpdatedAt));
		};
	}

	private OrderSpecifier<?>[] getOrderSpecifier(String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		return switch (sortType) {
			case NAME_ASC -> createOrderSpecifier(storage.name.asc(), storage.updatedAt.desc());
			case NAME_DESC -> createOrderSpecifier(storage.name.desc(), storage.updatedAt.desc());
			case UPDATED_AT_ASC -> createOrderSpecifier(null, storage.updatedAt.asc());
			default -> createOrderSpecifier(null, storage.updatedAt.desc());
		};
	}

	private OrderSpecifier<?>[] createOrderSpecifier(OrderSpecifier<String> nameOrderSpecifier, OrderSpecifier<LocalDateTime> updatedAtOrderSpecifier) {

		return Optional.ofNullable(nameOrderSpecifier)
			.map(nameSpecifier -> new OrderSpecifier<?>[]{nameSpecifier, updatedAtOrderSpecifier})
			.orElse(new OrderSpecifier<?>[]{updatedAtOrderSpecifier});
	}

	private BooleanExpression[] createBooleanExpressions(Long refrigeratorId, StorageType storageType, BooleanExpression booleanExpression) {

		return Optional.ofNullable(storageType)
			.map(type -> new BooleanExpression[]{
				storage.refrigerator.id.eq(refrigeratorId),
				storage.storageType.eq(type),
				booleanExpression})
			.orElse(new BooleanExpression[]{
				storage.refrigerator.id.eq(refrigeratorId),
				booleanExpression});
	}

	private BooleanExpression[] getBooleanExpressionByRefrigeratorIdAndStorageType(Long refrigeratorId, StorageType storageType, String lastPageName, LocalDateTime lastPageUpdatedAt, String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		return switch (sortType) {
			case NAME_ASC ->
				createBooleanExpressions(refrigeratorId, storageType, gtStorageNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt));
			case NAME_DESC ->
				createBooleanExpressions(refrigeratorId, storageType, ltStorageNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt));
			case UPDATED_AT_ASC ->
				createBooleanExpressions(refrigeratorId, storageType, gtStorageUpdatedAt(lastPageUpdatedAt));
			default -> createBooleanExpressions(refrigeratorId, storageType, ltStorageUpdatedAt(lastPageUpdatedAt));
		};
	}

	private Slice<Storage> checkLastPage(Pageable pageable, List<Storage> storages) {
		boolean hasNext = false;

		if (storages.size() > pageable.getPageSize()) {
			storages.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(storages, pageable, hasNext);
	}

	private BooleanExpression gtStorageNameAndLtUpdatedAt(String storageName, LocalDateTime updatedAt) {
		if (storageName == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = storage.name.gt(storageName);

		predicate = predicate.or(
			storage.name.eq(storageName)
				.and(storage.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression ltStorageNameAndLtUpdatedAt(String storageName, LocalDateTime updatedAt) {
		if (storageName == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = storage.name.lt(storageName);

		predicate = predicate.or(
			storage.name.eq(storageName)
				.and(storage.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression gtStorageUpdatedAt(LocalDateTime updatedAt) {

		return Optional.ofNullable(updatedAt)
			.map(storage.updatedAt::gt)
			.orElse(null);
	}

	private BooleanExpression ltStorageUpdatedAt(LocalDateTime updatedAt) {

		return Optional.ofNullable(updatedAt)
			.map(storage.updatedAt::lt)
			.orElse(null);
	}
}
