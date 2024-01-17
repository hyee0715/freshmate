package com.icebox.freshmate.domain.image.application.dto.response;

public record ImageRes(
	String fileName,
	String path
) {

	public static ImageRes of(String fileName, String path) {

		return new ImageRes(
			fileName,
			path
		);
	}
}
