package com.icebox.freshmate.domain.post.application.dto.request;

import org.hibernate.validator.constraints.Length;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.recipe.domain.Recipe;

import jakarta.validation.constraints.NotBlank;

public record PostReq(
	@NotBlank(message = "게시글 제목을 입력해주세요.")
	@Length(min = 1, max = 200, message = "게시글 제목은 1자 이상 200자 이하로 등록 가능합니다.")
	String title,

	@NotBlank(message = "게시글 내용을 입력해주세요.")
	@Length(min = 1, message = "게시글 내용은 1자 이상부터 등록 가능합니다.")
	String content,

	Long recipeId
) {

	public static Post toPost(PostReq postReq, Member member, Recipe recipe) {

		return Post.builder()
			.member(member)
			.recipe(recipe)
			.title(postReq.title())
			.content(postReq.content())
			.build();
	}
}
