package com.icebox.freshmate.domain.post.domain;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

	Slice<Post> findAllByMemberId(Long memberId, Pageable pageable);

	Optional<Post> findByIdAndMemberId(Long id, Long memberId);
}
