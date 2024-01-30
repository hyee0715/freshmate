package com.icebox.freshmate.domain.refrigerator.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefrigeratorRepositoryImpl implements RefrigeratorRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private QRefrigerator refrigerator = QRefrigerator.refrigerator;

	@Override
	public Slice<Refrigerator> findAllByMemberIdOrderByNameAsc(Long memberId, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt) {
		List<Refrigerator> refrigerators = queryFactory.select(refrigerator)
			.from(refrigerator)
			.where(
				refrigerator.member.id.eq(memberId),
				gtRefrigeratorNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt)
			)
			.orderBy(refrigerator.name.asc(), refrigerator.updatedAt.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, refrigerators);
	}

	@Override
	public Slice<Refrigerator> findAllByMemberIdOrderByNameDesc(Long memberId, Pageable pageable, String lastPageName, LocalDateTime lastPageUpdatedAt) {
		List<Refrigerator> refrigerators = queryFactory.select(refrigerator)
			.from(refrigerator)
			.where(
				refrigerator.member.id.eq(memberId),
				ltRefrigeratorNameAndLtUpdatedAt(lastPageName, lastPageUpdatedAt)
			)
			.orderBy(refrigerator.name.desc(), refrigerator.updatedAt.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, refrigerators);
	}

	@Override
	public Slice<Refrigerator> findAllByMemberIdOrderByUpdatedAtAsc(Long memberId, Pageable pageable, LocalDateTime lastPageUpdatedAt) {
		List<Refrigerator> refrigerators = queryFactory.select(refrigerator)
			.from(refrigerator)
			.where(
				refrigerator.member.id.eq(memberId),
				gtRefrigeratorUpdatedAt(lastPageUpdatedAt)
			)
			.orderBy(refrigerator.updatedAt.asc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, refrigerators);
	}

	@Override
	public Slice<Refrigerator> findAllByMemberIdOrderByUpdatedAtDesc(Long memberId, Pageable pageable) {
		List<Refrigerator> refrigerators = queryFactory.select(refrigerator)
			.from(refrigerator)
			.where(refrigerator.member.id.eq(memberId))
			.orderBy(refrigerator.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, refrigerators);
	}

	private Slice<Refrigerator> checkLastPage(Pageable pageable, List<Refrigerator> refrigerators) {
		boolean hasNext = false;

		if (refrigerators.size() > pageable.getPageSize()) {
			refrigerators.remove(pageable.getPageSize());
			hasNext = true;
		}

		return new SliceImpl<>(refrigerators, pageable, hasNext);
	}

	private BooleanExpression gtRefrigeratorNameAndLtUpdatedAt(String name, LocalDateTime updatedAt) {
		if (name == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = refrigerator.name.gt(name);

		predicate = predicate.or(
			refrigerator.name.eq(name)
				.and(refrigerator.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression ltRefrigeratorNameAndLtUpdatedAt(String name, LocalDateTime updatedAt) {
		if (name == null || updatedAt == null) {
			return null;
		}

		BooleanExpression predicate = refrigerator.name.lt(name);

		predicate = predicate.or(
			refrigerator.name.eq(name)
				.and(refrigerator.updatedAt.lt(updatedAt))
		);

		return predicate;
	}

	private BooleanExpression gtRefrigeratorUpdatedAt(LocalDateTime updatedAt) {
		if (updatedAt == null) {
			return null;
		}

		return refrigerator.updatedAt.gt(updatedAt);
	}
}
