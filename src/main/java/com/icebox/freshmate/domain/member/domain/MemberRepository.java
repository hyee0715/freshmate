package com.icebox.freshmate.domain.member.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByUsername(String username);

	boolean existsByUsername(String username);

	Optional<Member> findByRefreshToken(String refreshToken);
}
