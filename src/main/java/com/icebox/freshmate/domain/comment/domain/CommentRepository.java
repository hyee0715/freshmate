package com.icebox.freshmate.domain.comment.domain;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	Slice<Comment> findAllByPostId(Long postId, Pageable pageable);

	Optional<Comment> findByIdAndMemberId(Long id, Long memberId);
}
