package com.icebox.freshmate.domain.refrigerator.application.dto.response;

import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;

public record RefrigeratorRes(
	Long refrigeratorId,
	String refrigeratorName,
	Long memberId,
	String memberUsername,
	String memberNickName
) {

	public static RefrigeratorRes from(Refrigerator refrigerator) {

		return new RefrigeratorRes(
			refrigerator.getId(),
			refrigerator.getName(),
			refrigerator.getMember().getId(),
			refrigerator.getMember().getUsername(),
			refrigerator.getMember().getNickName()
		);
	}
}
