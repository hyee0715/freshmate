package com.icebox.freshmate.domain.refrigerator.application.dto.request;

import org.hibernate.validator.constraints.Length;

import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.member.domain.Member;

import jakarta.validation.constraints.NotBlank;

public record RefrigeratorReq(
	@NotBlank(message = "냉장고 이름을 입력해주세요.")
	@Length(min = 1, max = 50, message = "냉장고 이름은 1자 이상 50자 이하로 등록 가능합니다.")
	String name
) {

	public static Refrigerator toRefrigerator(RefrigeratorReq refrigeratorReq, Member member) {

		return Refrigerator.builder()
			.member(member)
			.name(refrigeratorReq.name)
			.build();
	}
}
