package com.icebox.freshmate.domain.storage.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StorageRepositoryImpl implements StorageRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QStorage storage = QStorage.storage;

	@Override
	public Slice<Storage> findAllByRefrigeratorIdOrderByNameAsc(Long refrigeratorId, Pageable pageable) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId))
			.orderBy(storage.name.asc(), storage.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdOrderByNameDesc(Long refrigeratorId, Pageable pageable) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId))
			.orderBy(storage.name.desc(), storage.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdOrderByUpdatedAtAsc(Long refrigeratorId, Pageable pageable) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId))
			.orderBy(storage.updatedAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdOrderByUpdatedAtDesc(Long refrigeratorId, Pageable pageable) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId))
			.orderBy(storage.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByNameAsc(Long refrigeratorId, StorageType storageType, Pageable pageable) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId), storage.storageType.eq(storageType))
			.orderBy(storage.name.asc(), storage.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByNameDesc(Long refrigeratorId, StorageType storageType, Pageable pageable) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId), storage.storageType.eq(storageType))
			.orderBy(storage.name.desc(), storage.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByUpdatedAtAsc(Long refrigeratorId, StorageType storageType, Pageable pageable) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId), storage.storageType.eq(storageType))
			.orderBy(storage.updatedAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, storages);
	}

	@Override
	public Slice<Storage> findAllByRefrigeratorIdAndStorageTypeOrderByUpdatedAtDesc(Long refrigeratorId, StorageType storageType, Pageable pageable) {
		List<Storage> storages = queryFactory.select(storage)
			.from(storage)
			.where(storage.refrigerator.id.eq(refrigeratorId), storage.storageType.eq(storageType))
			.orderBy(storage.updatedAt.desc())
			.offset(pageable.getOffset())
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
}
