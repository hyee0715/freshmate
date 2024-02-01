package com.icebox.freshmate.domain.storage.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StorageRepositoryImpl implements StorageRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QStorage storage = QStorage.storage;

	@Override
	public Slice<Storage> findAllByRefrigeratorIdOrderByNameAsc(Long refrigeratorId, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId),
				gtStorageNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt)
			)
			.orderBy(storage.name.asc(), storage.updatedAt.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdOrderByNameDesc(Long refrigeratorId, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId),
				ltStorageNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt))
			.orderBy(storage.name.desc(), storage.updatedAt.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdOrderByUpdatedAtAsc(Long refrigeratorId, Pageable pageable, LocalDateTime lastPageUpdatedAt) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId),
				gtStorageUpdatedAt(lastPageUpdatedAt))
			.orderBy(storage.updatedAt.asc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdOrderByUpdatedAtDesc(Long refrigeratorId, Pageable pageable, LocalDateTime lastPageUpdatedAt) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId),
				ltStorageUpdatedAt(lastPageUpdatedAt))
			.orderBy(storage.updatedAt.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByNameAsc(Long refrigeratorId, StorageType storageType, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId),
				storage.storageType.eq(storageType),
				gtStorageNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt))
			.orderBy(storage.name.asc(), storage.updatedAt.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByNameDesc(Long refrigeratorId, StorageType storageType, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId),
				storage.storageType.eq(storageType),
				ltStorageNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt))
			.orderBy(storage.name.desc(), storage.updatedAt.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByUpdatedAtAsc(Long refrigeratorId, StorageType storageType, Pageable pageable, LocalDateTime lastPageUpdatedAt) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId),
				storage.storageType.eq(storageType),
				gtStorageUpdatedAt(lastPageUpdatedAt))
			.orderBy(storage.updatedAt.asc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByUpdatedAtDesc(Long refrigeratorId, StorageType storageType, Pageable pageable, LocalDateTime lastPageUpdatedAt) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId),
				storage.storageType.eq(storageType),
				ltStorageUpdatedAt(lastPageUpdatedAt))
			.orderBy(storage.updatedAt.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
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
