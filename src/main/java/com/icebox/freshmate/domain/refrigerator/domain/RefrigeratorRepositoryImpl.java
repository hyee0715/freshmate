package com.icebox.freshmate.domain.refrigerator.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefrigeratorRepositoryImpl implements RefrigeratorRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private QRefrigerator refrigerator = QRefrigerator.refrigerator;

	@Override
	public Slice<Refrigerator> findAllByMemberIdOrderByNameAsc(Long memberId, Pageable pageable) {
		List<Refrigerator> refrigerators = queryFactory.select(refrigerator)
			.from(refrigerator)
			.where(refrigerator.member.id.eq(memberId))
			.orderBy(refrigerator.name.asc(), refrigerator.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, refrigerators);
	}

	@Override
	public Slice<Refrigerator> findAllByMemberIdOrderByNameDesc(Long memberId, Pageable pageable) {
		List<Refrigerator> refrigerators = queryFactory.select(refrigerator)
			.from(refrigerator)
			.where(refrigerator.member.id.eq(memberId))
			.orderBy(refrigerator.name.desc(), refrigerator.updatedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return checkLastPage(pageable, refrigerators);
	}

	@Override
	public Slice<Refrigerator> findAllByMemberIdOrderByUpdatedAtAsc(Long memberId, Pageable pageable) {
		List<Refrigerator> refrigerators = queryFactory.select(refrigerator)
			.from(refrigerator)
			.where(refrigerator.member.id.eq(memberId))
			.orderBy(refrigerator.updatedAt.asc())
			.offset(pageable.getOffset())
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
}
