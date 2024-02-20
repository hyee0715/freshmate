package com.icebox.freshmate.domain.post.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

	Optional<Post> findByIdAndMemberId(Long id, Long memberId);
}
