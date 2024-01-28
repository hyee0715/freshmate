package com.icebox.freshmate.domain.refrigerator.domain;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {

	@Query("SELECT r FROM Refrigerator r WHERE r.member.id = :memberId ORDER BY r.name ASC, r.updatedAt DESC")
	Slice<Refrigerator> findAllByMemberIdOrderByNameAsc(@Param("memberId") Long memberId, Pageable pageable);

	@Query("SELECT r FROM Refrigerator r WHERE r.member.id = :memberId ORDER BY r.name DESC, r.updatedAt DESC")
	Slice<Refrigerator> findAllByMemberIdOrderByNameDesc(@Param("memberId") Long memberId, Pageable pageable);

	@Query("SELECT r FROM Refrigerator r WHERE r.member.id = :memberId ORDER BY r.updatedAt ASC")
	Slice<Refrigerator> findAllByMemberIdOrderByUpdatedAtAsc(@Param("memberId") Long memberId, Pageable pageable);

	@Query("SELECT r FROM Refrigerator r WHERE r.member.id = :memberId ORDER BY r.updatedAt DESC")
	Slice<Refrigerator> findAllByMemberIdOrderByUpdatedAtDesc(@Param("memberId") Long memberId, Pageable pageable);

	Optional<Refrigerator> findByIdAndMemberId(Long id, Long MemberId);
}
