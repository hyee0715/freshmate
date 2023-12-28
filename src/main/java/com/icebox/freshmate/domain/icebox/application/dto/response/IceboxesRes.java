package com.icebox.freshmate.domain.icebox.application.dto.response;

import java.util.List;

import com.icebox.freshmate.domain.icebox.domain.Icebox;

public record IceboxesRes(
	List<IceboxRes> iceboxes
) {

	public static IceboxesRes from(List<Icebox> iceboxes) {

		List<IceboxRes> iceboxesRes = iceboxes.stream()
			.map(IceboxRes::from)
			.toList();

		return new IceboxesRes(iceboxesRes);
	}
}
