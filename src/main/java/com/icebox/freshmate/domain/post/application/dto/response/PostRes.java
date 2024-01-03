package com.icebox.freshmate.domain.post.application.dto.response;

import com.icebox.freshmate.domain.post.domain.Post;

public record PostRes(
	Long postId,
	Long memberId,
	String content
) {

	public static PostRes from(Post post) {

		return new PostRes(
			post.getId(),
			post.getMember().getId(),
			post.getContent()
		);
	}
}
