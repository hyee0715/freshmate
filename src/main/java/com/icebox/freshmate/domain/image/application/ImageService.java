package com.icebox.freshmate.domain.image.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.image.application.dto.request.ImageDeleteReq;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.image.application.dto.response.ImagesRes;

@Service
@Transactional
public interface ImageService {

	ImagesRes store(ImageUploadReq request);

	void delete(ImageDeleteReq request);
}
