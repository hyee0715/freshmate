package com.icebox.freshmate.domain.icebox.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IceboxRepository extends JpaRepository<Icebox, Long> {

	List<Icebox> findAllByMemberId(Long memberId);

	Optional<Icebox> findByIdAndMemberId(Long id, Long MemberId);
}
