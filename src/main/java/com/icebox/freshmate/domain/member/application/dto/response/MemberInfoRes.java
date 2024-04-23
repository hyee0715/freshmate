package com.icebox.freshmate.domain.member.application.dto.response;

import com.icebox.freshmate.domain.member.domain.Member;

public record MemberInfoRes(
	Long memberId,
	String username,
	String realName,
	String nickName,
	String role
) {

	public static MemberInfoRes from(Member member) {

		return new MemberInfoRes(
			member.getId(),
			member.getUsername(),
			member.getRealName(),
			member.getNickName(),
			member.getRole().name()
		);
	}
}
