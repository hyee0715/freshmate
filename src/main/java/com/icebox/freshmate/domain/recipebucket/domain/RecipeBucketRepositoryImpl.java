package com.icebox.freshmate.domain.recipebucket.domain;

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
public class RecipeBucketRepositoryImpl implements RecipeBucketRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QRecipeBucket recipeBucket = QRecipeBucket.recipeBucket;
	private final QMember member = QMember.member;

	@Override
	public Slice<RecipeBucket> findAllByMemberId(Long memberId, Pageable pageable, String sortBy) {
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<RecipeBucket> recipeBuckets = queryFactory.select(recipeBucket)
			.from(recipeBucket)
			.join(recipeBucket.member, member).fetchJoin()
			.where(recipeBucket.member.id.eq(memberId))
			.orderBy(orderSpecifier)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, recipeBuckets);
	}

	private OrderSpecifier<?>[] getOrderSpecifier(String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		return switch (sortType) {
			case TITLE_ASC -> createOrderSpecifier(recipeBucket.recipe.title.asc(), recipeBucket.updatedAt.desc());
			case TITLE_DESC -> createOrderSpecifier(recipeBucket.recipe.title.desc(), recipeBucket.updatedAt.desc());
			case UPDATED_AT_ASC -> createOrderSpecifier(null, recipeBucket.updatedAt.asc());
			default -> createOrderSpecifier(null, recipeBucket.updatedAt.desc());
		};
	}

	private OrderSpecifier<?>[] createOrderSpecifier(OrderSpecifier<String> titleOrderSpecifier, OrderSpecifier<LocalDateTime> updatedAtOrderSpecifier) {

		return Optional.ofNullable(titleOrderSpecifier)
			.map(titleSpecifier -> new OrderSpecifier<?>[]{titleSpecifier, updatedAtOrderSpecifier})
			.orElse(new OrderSpecifier<?>[]{updatedAtOrderSpecifier});
	}

	private Slice<RecipeBucket> checkLastPage(Pageable pageable, List<RecipeBucket> recipeBuckets) {
		boolean hasNext = false;

		if (recipeBuckets.size() > pageable.getPageSize()) {
			recipeBuckets.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(recipeBuckets, pageable, hasNext);
	}
}
