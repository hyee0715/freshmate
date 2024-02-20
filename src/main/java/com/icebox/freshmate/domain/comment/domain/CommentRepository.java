package com.icebox.freshmate.domain.comment.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

	Optional<Comment> findByIdAndMemberId(Long id, Long memberId);
}
