package com.icebox.freshmate.domain.member.application.dto.request;

import com.icebox.freshmate.domain.member.domain.Member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberUpdateInfoReq(
	@NotBlank(message = "이름을 입력해주세요.")
	@Size(min = 2, max = 10, message = "사용자 이름은 2 ~ 10자 내외로 입력해주세요.")
	@Pattern(regexp = "^[A-Za-z가-힣]+$", message = "사용자 이름은 한글 또는 알파벳만 입력해주세요.")
	String realName,

	@NotBlank(message = "닉네임을 입력해주세요.")
	@Size(min = 2, max = 10, message = "닉네임은 2 ~ 10자 내외로 입력해주세요.")
	String nickName
) {

	public Member toMember() {

		return Member
			.builder()
			.realName(realName)
			.nickName(nickName)
			.build();
	}
}
