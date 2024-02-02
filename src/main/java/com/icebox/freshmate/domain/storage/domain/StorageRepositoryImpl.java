package com.icebox.freshmate.domain.storage.domain;

import java.time.LocalDateTime;
import java.util.List;

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
		BooleanExpression[] booleanExpression = getBooleanExpressionForRefrigeratorId(refrigeratorId, lastPageName, lastPageUpdatedAt, sortBy);
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
		BooleanExpression[] booleanExpression = getBooleanExpressionForRefrigeratorIdAndStorageType(refrigeratorId, storageType, lastPageName, lastPageUpdatedAt, sortBy);
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(booleanExpression)
			.orderBy(orderSpecifier)
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	private BooleanExpression[] getBooleanExpressionForRefrigeratorId(Long refrigeratorId, String lastPageName, LocalDateTime lastPageUpdatedAt, String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		switch (sortType) {
			case NAME_ASC -> {
				return new BooleanExpression[]{
					storage.refrigerator.id.eq(refrigeratorId),
					gtStorageNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt)
				};
			}

			case NAME_DESC -> {
				return new BooleanExpression[]{
					storage.refrigerator.id.eq(refrigeratorId),
					ltStorageNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt)
				};
			}

			case UPDATED_AT_ASC -> {
				return new BooleanExpression[]{
					storage.refrigerator.id.eq(refrigeratorId),
					gtStorageUpdatedAt(lastPageUpdatedAt)
				};
			}
		}

		return new BooleanExpression[] {
			storage.refrigerator.id.eq(refrigeratorId),
			ltStorageUpdatedAt(lastPageUpdatedAt)
		};
	}

	private OrderSpecifier<?>[] getOrderSpecifier(String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		switch (sortType) {
			case NAME_ASC -> {
				return new OrderSpecifier[]{
					storage.name.asc(), storage.updatedAt.desc()
				};
			}

			case NAME_DESC -> {
				return new OrderSpecifier[]{
					storage.name.desc(), storage.updatedAt.desc()
				};
			}

			case UPDATED_AT_ASC -> {
				return new OrderSpecifier[]{
					storage.updatedAt.asc()
				};
			}
		}

		return new OrderSpecifier[]{
			storage.updatedAt.desc()
		};
	}

	private BooleanExpression[] getBooleanExpressionForRefrigeratorIdAndStorageType(Long refrigeratorId, StorageType storageType, String lastPageName, LocalDateTime lastPageUpdatedAt, String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		switch (sortType) {
			case NAME_ASC -> {
				return new BooleanExpression[]{
					storage.refrigerator.id.eq(refrigeratorId),
					storage.storageType.eq(storageType),
					gtStorageNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt)
				};
			}

			case NAME_DESC -> {
				return new BooleanExpression[]{
					storage.refrigerator.id.eq(refrigeratorId),
					storage.storageType.eq(storageType),
					ltStorageNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt)
				};
			}

			case UPDATED_AT_ASC -> {
				return new BooleanExpression[]{
					storage.refrigerator.id.eq(refrigeratorId),
					storage.storageType.eq(storageType),
					gtStorageUpdatedAt(lastPageUpdatedAt)
				};
			}
		}

		return new BooleanExpression[] {
			storage.refrigerator.id.eq(refrigeratorId),
			storage.storageType.eq(storageType),
			ltStorageUpdatedAt(lastPageUpdatedAt)
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
		if (updatedAt == null) {
			return null;
		}

		return storage.updatedAt.gt(updatedAt);
	}

	private BooleanExpression ltStorageUpdatedAt(LocalDateTime updatedAt) {
		if (updatedAt == null) {
			return null;
		}

		return storage.updatedAt.lt(updatedAt);
	}
}
