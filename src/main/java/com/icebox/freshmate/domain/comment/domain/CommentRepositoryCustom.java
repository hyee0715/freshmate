package com.icebox.freshmate.domain.comment.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentRepositoryCustom {

	Slice<Comment> findAllByPostId(Long postId, Pageable pageable);
}
