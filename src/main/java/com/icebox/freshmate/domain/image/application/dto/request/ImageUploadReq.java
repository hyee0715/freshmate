package com.icebox.freshmate.domain.image.application.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record ImageUploadReq(
	List<MultipartFile> files
) {
}
