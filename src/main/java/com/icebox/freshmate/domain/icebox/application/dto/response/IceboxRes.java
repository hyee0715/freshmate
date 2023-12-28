package com.icebox.freshmate.domain.icebox.application.dto.response;

import com.icebox.freshmate.domain.icebox.domain.Icebox;

public record IceboxRes(
	Long id,
	String name
) {

	public static IceboxRes from(Icebox icebox) {

		return new IceboxRes(
			icebox.getId(),
			icebox.getName()
		);
	}
}
