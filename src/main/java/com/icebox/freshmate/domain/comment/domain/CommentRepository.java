package com.icebox.freshmate.domain.comment.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findAllByPostId(Long postId);

	Optional<Comment> findByIdAndMemberId(Long id, Long memberId);
}
