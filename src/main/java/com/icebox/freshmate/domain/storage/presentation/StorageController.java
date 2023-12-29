package com.icebox.freshmate.domain.storage.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.storage.application.StorageService;
import com.icebox.freshmate.domain.storage.application.dto.request.StorageReq;
import com.icebox.freshmate.domain.storage.application.dto.response.StorageRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/storages")
@RestController
public class StorageController {

	private final StorageService storageService;

	@PostMapping
	public ResponseEntity<StorageRes> create(@Validated @RequestBody StorageReq storageReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		StorageRes storageRes = storageService.create(storageReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(storageRes);
	}
}
