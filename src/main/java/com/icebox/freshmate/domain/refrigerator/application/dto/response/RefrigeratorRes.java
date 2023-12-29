package com.icebox.freshmate.domain.refrigerator.application.dto.response;

import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;

public record RefrigeratorRes(
	Long id,
	String name
) {

	public static RefrigeratorRes from(Refrigerator refrigerator) {

		return new RefrigeratorRes(
			refrigerator.getId(),
			refrigerator.getName()
		);
	}
}
