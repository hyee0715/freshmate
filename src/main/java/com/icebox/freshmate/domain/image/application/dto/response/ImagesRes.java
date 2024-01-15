package com.icebox.freshmate.domain.image.application.dto.response;

import java.util.List;

public record ImagesRes(
	List<String> fileNames
) {

	public static ImagesRes from(List<String> fileNames) {
		return new ImagesRes(fileNames);
	}

}
