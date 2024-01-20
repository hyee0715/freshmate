package com.icebox.freshmate.domain.comment.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentImageRepository extends JpaRepository<CommentImage, Long> {

	Optional<CommentImage> findByCommentIdAndPath(Long commentId, String path);
}
