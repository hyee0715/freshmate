package com.icebox.freshmate.domain.post.application.dto.response;

import java.util.List;

import com.icebox.freshmate.domain.post.domain.Post;

public record PostsRes(
	List<PostRes> posts
) {

	public static PostsRes from(List<Post> posts) {
		List<PostRes> postsRes = posts.stream()
			.map(PostRes::from)
			.toList();

		return new PostsRes(postsRes);
	}
}
