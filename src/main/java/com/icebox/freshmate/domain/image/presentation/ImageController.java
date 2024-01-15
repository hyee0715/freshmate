package com.icebox.freshmate.domain.image.presentation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.icebox.freshmate.domain.image.application.ImageService;
import com.icebox.freshmate.domain.image.application.dto.request.ImageDeleteReq;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.image.application.dto.response.ImagesRes;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

	private final ImageService imageService;

	@PostMapping
	public ResponseEntity<ImagesRes> create(@RequestPart(required = false) List<MultipartFile> imageFiles) {
		ImageUploadReq imageUploadReq = new ImageUploadReq(imageFiles);

		ImagesRes imagesRes = imageService.store(imageUploadReq);

		return ResponseEntity.ok(imagesRes);
	}

	@DeleteMapping
	public ResponseEntity<Void> delete(@RequestBody ImageDeleteReq imageDeleteReq) {
		imageService.delete(imageDeleteReq);

		return ResponseEntity.noContent()
			.build();
	}
}
