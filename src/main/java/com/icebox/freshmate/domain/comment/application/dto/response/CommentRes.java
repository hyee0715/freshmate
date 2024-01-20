package com.icebox.freshmate.domain.comment.application.dto.response;

import java.util.List;

import com.icebox.freshmate.domain.comment.domain.Comment;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;

public record CommentRes(
	Long commentId,
	Long postId,
	Long memberId,
	String memberNickName,
	String content,
	List<ImageRes> images
) {

	public static CommentRes of(Comment comment, List<ImageRes> imagesRes) {

		return new CommentRes(
			comment.getId(),
			comment.getPost().getId(),
			comment.getMember().getId(),
			comment.getMember().getNickName(),
			comment.getContent(),
			imagesRes
		);
	}
}
