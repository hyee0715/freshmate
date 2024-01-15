package com.icebox.freshmate.domain.image.application.dto.response;

import java.util.List;

public record ImagesRes(
	List<ImageRes> images
) {

	public static ImagesRes from(List<ImageRes> images) {
		return new ImagesRes(images);
	}

}
