package com.icebox.freshmate.domain.image.application.dto.request;

import java.util.List;

public record ImageDeleteReq(
	List<String> filePaths
) {
}
