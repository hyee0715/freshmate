package com.icebox.freshmate.domain.comment.application.dto.request;

import org.hibernate.validator.constraints.Length;

import com.icebox.freshmate.domain.comment.domain.Comment;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.post.domain.Post;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateReq(
	Long postId,

	@NotBlank(message = "댓글 내용을 입력해주세요.")
	@Length(min = 1, message = "댓글 내용은 1자 이상부터 등록 가능합니다.")
	String content
) {

	public static Comment toComment(CommentCreateReq commentCreateReq, Post post, Member member) {

		return Comment.builder()
			.post(post)
			.member(member)
			.content(commentCreateReq.content())
			.build();
	}
}
