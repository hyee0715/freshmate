package com.icebox.freshmate.domain.grocery.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.icebox.freshmate.domain.member.domain.QMember;
import com.icebox.freshmate.domain.refrigerator.domain.QRefrigerator;
import com.icebox.freshmate.domain.storage.domain.QStorage;
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
	public Slice<Grocery> findAllByStorageIdAndMemberIdOrderByNameAsc(Long storageId, Long memberId, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId))
			.orderBy(grocery.name.asc(), grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdOrderByNameDesc(Long storageId, Long memberId, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId))
			.orderBy(grocery.name.desc(), grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdOrderByUpdatedAtAsc(Long storageId, Long memberId, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId))
			.orderBy(grocery.updatedAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdOrderByUpdatedAtDesc(Long storageId, Long memberId, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId))
			.orderBy(grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByNameAsc(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(grocery.name.asc(), grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByNameDesc(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(grocery.name.desc(), grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByUpdatedAtAsc(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(grocery.updatedAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByUpdatedAtDesc(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByExpirationDateAsc(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(grocery.expirationDate.asc(), grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByExpirationDateDesc(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(grocery.expirationDate.desc(), grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByNameAsc(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryType.eq(groceryType),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(grocery.name.asc(), grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByNameDesc(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryType.eq(groceryType),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(grocery.name.desc(), grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByUpdatedAtAsc(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryType.eq(groceryType),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(grocery.updatedAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByUpdatedAtDesc(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryType.eq(groceryType),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByExpirationDateAsc(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryType.eq(groceryType),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(grocery.expirationDate.asc(), grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByExpirationDateDesc(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryType.eq(groceryType),
				grocery.groceryExpirationType.eq(groceryExpirationType))
			.orderBy(grocery.expirationDate.desc(), grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderByNameAsc(Long storageId, Long memberId, GroceryType groceryType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryType.eq(groceryType))
			.orderBy(grocery.name.asc(), grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderByNameDesc(Long storageId, Long memberId, GroceryType groceryType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryType.eq(groceryType))
			.orderBy(grocery.name.desc(), grocery.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderByUpdatedAtAsc(Long storageId, Long memberId, GroceryType groceryType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryType.eq(groceryType))
			.orderBy(grocery.updatedAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceries);
	}

	@Override
	public Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderByUpdatedAtDesc(Long storageId, Long memberId, GroceryType groceryType, Pageable pageable) {
		List<Grocery> groceries = queryFactory.select(grocery)
			.from(grocery)
			.join(grocery.storage, storage).fetchJoin()
			.join(storage.refrigerator, refrigerator).fetchJoin()
			.join(refrigerator.member, member).fetchJoin()
			.where(storage.id.eq(storageId),
				member.id.eq(memberId),
				grocery.groceryType.eq(groceryType))
			.orderBy(grocery.updatedAt.desc())
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
}
