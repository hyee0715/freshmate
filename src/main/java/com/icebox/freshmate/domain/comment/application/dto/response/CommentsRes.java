package com.icebox.freshmate.domain.comment.application.dto.response;

import java.util.List;

import com.icebox.freshmate.domain.comment.domain.Comment;

public record CommentsRes(
	List<CommentRes> comments
) {

	public static CommentsRes from(List<Comment> comments) {
		List<CommentRes> commentsRes = comments.stream()
			.map(CommentRes::from)
			.toList();

		return new CommentsRes(commentsRes);
	}
}
