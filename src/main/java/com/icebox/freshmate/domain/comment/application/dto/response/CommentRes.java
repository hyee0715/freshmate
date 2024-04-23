package com.icebox.freshmate.domain.comment.application.dto.response;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icebox.freshmate.domain.comment.domain.Comment;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;

public record CommentRes(
	Long commentId,
	Long postId,
	Long memberId,
	String memberNickName,
	String content,

	@JsonFormat(shape = STRING, pattern = "YYYY-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime createdAt,

	@JsonFormat(shape = STRING, pattern = "YYYY-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime updatedAt,

	List<ImageRes> images
) {

	public static CommentRes of(Comment comment, List<ImageRes> imagesRes) {

		return new CommentRes(
			comment.getId(),
			comment.getPost().getId(),
			comment.getMember().getId(),
			comment.getMember().getNickName(),
			comment.getContent(),
			comment.getCreatedAt(),
			comment.getUpdatedAt(),
			imagesRes
		);
	}
}
