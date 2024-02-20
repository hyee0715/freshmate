package com.icebox.freshmate.domain.post.domain;

import static com.icebox.freshmate.global.util.SortTypeUtils.CREATED_AT_ASC;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.QMember;
import com.icebox.freshmate.global.util.SortTypeUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QPost post = QPost.post;
	private final QMember member = QMember.member;

	@Override
	public Slice<Post> findAllByCondition(Member writer, Pageable pageable, String sortBy) {
		BooleanExpression[] booleanExpressions = createBooleanExpressions(writer);
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<Post> posts = queryFactory.select(post)
			.from(post)
			.join(post.member, member).fetchJoin()
			.where(booleanExpressions)
			.orderBy(orderSpecifier)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, posts);
	}

	private BooleanExpression[] createBooleanExpressions(Member writer) {

		return Optional.ofNullable(writer)
			.map(member -> new BooleanExpression[]{post.member.id.eq(member.getId())})
			.orElse(new BooleanExpression[]{null});
	}

	private OrderSpecifier<?>[] getOrderSpecifier(String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		if (sortType.equals(CREATED_AT_ASC)) {
			return createOrderSpecifier(post.id.asc());
		}

		return createOrderSpecifier(post.id.desc());
	}

	private OrderSpecifier<?>[] createOrderSpecifier(OrderSpecifier<Long> orderSpecifier) {

		return new OrderSpecifier<?>[]{orderSpecifier};
	}

	private Slice<Post> checkLastPage(Pageable pageable, List<Post> posts) {
		boolean hasNext = false;

		if (posts.size() > pageable.getPageSize()) {
			posts.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(posts, pageable, hasNext);
	}
}
