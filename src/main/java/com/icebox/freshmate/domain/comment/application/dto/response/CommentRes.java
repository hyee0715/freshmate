package com.icebox.freshmate.domain.comment.application.dto.response;

import com.icebox.freshmate.domain.comment.domain.Comment;

public record CommentRes(
	Long commentId,
	Long postId,
	Long memberId,
	String memberNickName,
	String content
) {

	public static CommentRes from(Comment comment) {

		return new CommentRes(
			comment.getId(),
			comment.getPost().getId(),
			comment.getMember().getId(),
			comment.getMember().getNickName(),
			comment.getContent()
		);
	}
}
