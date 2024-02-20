package com.icebox.freshmate.domain.comment.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.icebox.freshmate.domain.post.domain.QPost;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QComment comment = QComment.comment;
	private final QPost post = QPost.post;

	@Override
	public Slice<Comment> findAllByPostId(Long postId, Pageable pageable, Long lastPageId) {
		BooleanExpression[] booleanExpressions = getBooleanExpressionByPostId(postId, lastPageId);

		List<Comment> comments = queryFactory.select(comment)
			.from(comment)
			.join(comment.post, post).fetchJoin()
			.where(booleanExpressions)
			.orderBy(comment.id.asc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, comments);
	}

	private BooleanExpression[] getBooleanExpressionByPostId(Long postId, Long lastPageId) {

		return new BooleanExpression[]{comment.post.id.eq(postId), gtCommentId(lastPageId)};
	}

	private Slice<Comment> checkLastPage(Pageable pageable, List<Comment> comments) {
		boolean hasNext = false;

		if (comments.size() > pageable.getPageSize()) {
			comments.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(comments, pageable, hasNext);
	}

	private BooleanExpression gtCommentId(Long commentId) {

		return Optional.ofNullable(commentId)
			.map(comment.id::gt)
			.orElse(null);
	}
}
