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
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroceryBucketRepositoryImpl implements GroceryBucketRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QGroceryBucket groceryBucket = QGroceryBucket.groceryBucket;
	private final QMember member = QMember.member;

	@Override
	public Slice<GroceryBucket> findAllByMemberId(Long memberId, Pageable pageable, String sortBy) {
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<GroceryBucket> groceryBuckets = queryFactory.select(groceryBucket)
			.from(groceryBucket)
			.join(groceryBucket.member, member).fetchJoin()
			.where(groceryBucket.member.id.eq(memberId))
			.orderBy(orderSpecifier)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, groceryBuckets);
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
}
