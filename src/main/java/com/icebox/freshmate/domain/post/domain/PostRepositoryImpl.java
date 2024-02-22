package com.icebox.freshmate.domain.post.domain;

import static com.icebox.freshmate.global.util.SortTypeUtils.ID_ASC;

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
	public Slice<Post> findAllByCondition(Member writer, String searchType, String keyword, Pageable pageable, String sortBy, Long lastPageId) {
		BooleanExpression[] booleanExpressions = getBooleanExpressionByMember(writer, searchType, keyword, sortBy, lastPageId);
		OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(sortBy);

		List<Post> posts = queryFactory.select(post)
			.from(post)
			.join(post.member, member).fetchJoin()
			.where(booleanExpressions)
			.orderBy(orderSpecifier)
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, posts);
	}

	private BooleanExpression[] getBooleanExpressionByMember(Member member, String searchType, String keyword, String sortBy, Long lastPageId) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		if (sortType.equals(ID_ASC)) {
			return createBooleanExpressions(member, getSearchBooleanExpression(searchType, keyword), gtPostId(lastPageId));
		}

		return createBooleanExpressions(member, getSearchBooleanExpression(searchType, keyword), ltPostId(lastPageId));
	}

	private BooleanExpression[] createBooleanExpressions(Member writer, BooleanExpression searchBooleanExpression, BooleanExpression cursorBooleanExpression) {

		return Optional.ofNullable(writer)
			.map(member -> new BooleanExpression[]{post.member.id.eq(member.getId()), searchBooleanExpression, cursorBooleanExpression})
			.orElse(new BooleanExpression[]{searchBooleanExpression, cursorBooleanExpression});
	}

	private OrderSpecifier<?>[] getOrderSpecifier(String sortBy) {
		SortTypeUtils sortType = SortTypeUtils.findSortType(sortBy);

		if (sortType.equals(ID_ASC)) {
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

	private BooleanExpression getSearchBooleanExpression(String searchType, String keyword) {

		return switch (searchType) {
			case "title" -> post.title.contains(keyword);
			case "content" -> post.content.contains(keyword);
			case "writer" -> post.member.nickName.contains(keyword);
			default -> post.title.contains(keyword)
				.or(post.content.contains(keyword)
					.or(post.member.nickName.contains(keyword))
				);
		};
	}

	private BooleanExpression gtPostId(Long postId) {

		return Optional.ofNullable(postId)
			.map(post.id::gt)
			.orElse(null);
	}

	private BooleanExpression ltPostId(Long postId) {

		return Optional.ofNullable(postId)
			.map(post.id::lt)
			.orElse(null);
	}
}
