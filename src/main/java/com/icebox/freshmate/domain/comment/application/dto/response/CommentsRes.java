package com.icebox.freshmate.domain.comment.application.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.icebox.freshmate.domain.comment.domain.Comment;
import com.icebox.freshmate.domain.comment.domain.CommentImage;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;

public record CommentsRes(
	List<CommentRes> comments,
	boolean hasNext
) {

	public static CommentsRes from(Slice<Comment> comments) {
		List<CommentRes> commentsRes = comments.stream()
			.map(comment -> {
				List<ImageRes> commentImagesRes = getCommentImagesRes(comment);

				return CommentRes.of(comment, commentImagesRes);
			})
			.toList();

		return new CommentsRes(commentsRes, comments.hasNext());
	}

	private static List<ImageRes> getCommentImagesRes(Comment comment) {
		List<CommentImage> commentImages = comment.getCommentImages();

		return commentImages.stream()
			.map(commentImage -> ImageRes.of(commentImage.getFileName(), commentImage.getPath()))
			.toList();
	}
}
