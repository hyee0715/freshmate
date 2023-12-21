package com.icebox.freshmate.domain.auth.application.dto.request;

import com.icebox.freshmate.domain.member.domain.Member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberLoginReq(
	@NotBlank(message = "아이디를 입력해주세요.")
	@Size(min = 7, max = 30, message = "아이디는 7 ~ 30자 내외로 입력해주세요.")
	String username,

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
		message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
	String password
) {

	public static Member toMember(MemberLoginReq memberLoginReq) {
		String username = memberLoginReq.username();
		String password = memberLoginReq.password();

		return Member.builder()
			.username(username)
			.password(password)
			.build();
	}
}
