package com.icebox.freshmate.domain.refrigerator.application.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;

public record RefrigeratorsRes(
	List<RefrigeratorRes> refrigerators,
	boolean hasNext
) {

	public static RefrigeratorsRes from(Slice<Refrigerator> refrigerators) {

		List<RefrigeratorRes> refrigeratorsRes = refrigerators.stream()
			.map(RefrigeratorRes::from)
			.toList();

		return new RefrigeratorsRes(refrigeratorsRes, refrigerators.hasNext());
	}
}
