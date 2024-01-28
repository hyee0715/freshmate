package com.icebox.freshmate.domain.refrigerator.application.dto.response;

import java.time.LocalDateTime;

import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;

public record RefrigeratorRes(
	Long refrigeratorId,
	String refrigeratorName,
	Long memberId,
	String memberUsername,
	String memberNickName,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public static RefrigeratorRes from(Refrigerator refrigerator) {

		return new RefrigeratorRes(
			refrigerator.getId(),
			refrigerator.getName(),
			refrigerator.getMember().getId(),
			refrigerator.getMember().getUsername(),
			refrigerator.getMember().getNickName(),
			refrigerator.getCreatedAt(),
			refrigerator.getUpdatedAt()
		);
	}
}
