package com.icebox.freshmate.domain.refrigerator.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long>, RefrigeratorRepositoryCustom {

	Optional<Refrigerator> findByIdAndMemberId(Long id, Long MemberId);
}
