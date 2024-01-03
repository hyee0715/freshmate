package com.icebox.freshmate.domain.post.application.dto.request;

import org.hibernate.validator.constraints.Length;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.post.domain.Post;

import jakarta.validation.constraints.NotBlank;

public record PostReq(
	@NotBlank(message = "게시글 내용을 입력해주세요.")
	@Length(min = 1, message = "게시글 내용은 1자 이상부터 등록 가능합니다.")
	String content
) {

	public static Post toPost(PostReq postReq, Member member) {

		return Post.builder()
			.member(member)
			.content(postReq.content())
			.build();
	}
}
