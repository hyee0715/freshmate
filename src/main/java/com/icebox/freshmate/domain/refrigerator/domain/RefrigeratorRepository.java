package com.icebox.freshmate.domain.refrigerator.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {

	List<Refrigerator> findAllByMemberId(Long memberId);

	Optional<Refrigerator> findByIdAndMemberId(Long id, Long MemberId);
}
