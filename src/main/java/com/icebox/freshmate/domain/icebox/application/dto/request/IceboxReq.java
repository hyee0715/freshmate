package com.icebox.freshmate.domain.icebox.application.dto.request;

import org.hibernate.validator.constraints.Length;

import com.icebox.freshmate.domain.icebox.domain.Icebox;
import com.icebox.freshmate.domain.member.domain.Member;

import jakarta.validation.constraints.NotBlank;

public record IceboxReq(
	@NotBlank(message = "냉장고 이름을 입력하세요.")
	@Length(min = 1, max = 50, message = "냉장고 이름은 1자 이상 50자 이하로 등록 가능합니다.")
	String name
) {

	public static Icebox toIcebox(IceboxReq iceboxReq, Member member) {

		return Icebox.builder()
			.member(member)
			.name(iceboxReq.name)
			.build();
	}
}
