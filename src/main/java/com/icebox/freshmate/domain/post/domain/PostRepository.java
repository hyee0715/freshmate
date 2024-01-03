package com.icebox.freshmate.domain.post.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findAllByMemberId(Long memberId);

	Optional<Post> findByIdAndMemberId(Long id, Long memberId);
}
