package com.icebox.freshmate.domain.refrigerator.application.dto.response;

import java.util.List;

import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;

public record RefrigeratorsRes(
	List<RefrigeratorRes> refrigerators
) {

	public static RefrigeratorsRes from(List<Refrigerator> refrigerators) {

		List<RefrigeratorRes> refrigeratorsRes = refrigerators.stream()
			.map(RefrigeratorRes::from)
			.toList();

		return new RefrigeratorsRes(refrigeratorsRes);
	}
}
