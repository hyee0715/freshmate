package com.icebox.freshmate.domain.storage.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.storage.application.StorageService;
import com.icebox.freshmate.domain.storage.application.dto.request.StorageCreateReq;
import com.icebox.freshmate.domain.storage.application.dto.request.StorageUpdateReq;
import com.icebox.freshmate.domain.storage.application.dto.response.StorageRes;
import com.icebox.freshmate.domain.storage.application.dto.response.StoragesRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/storages")
@RestController
public class StorageController {

	private final StorageService storageService;

	@PostMapping
	public ResponseEntity<StorageRes> create(@Validated @RequestBody StorageCreateReq storageCreateReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		StorageRes storageRes = storageService.create(storageCreateReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(storageRes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<StorageRes> findById(@PathVariable Long id) {
		StorageRes storageRes = storageService.findById(id);

		return ResponseEntity.ok(storageRes);
	}

	@GetMapping("/refrigerators/{refrigeratorId}")
	public ResponseEntity<StoragesRes> findAllByRefrigeratorId(@PathVariable Long refrigeratorId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		StoragesRes storagesRes = storageService.findAllByRefrigeratorId(refrigeratorId, principalDetails.getUsername());

		return ResponseEntity.ok(storagesRes);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<StorageRes> update(@PathVariable Long id, @Validated @RequestBody StorageUpdateReq storageUpdateReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		StorageRes storageRes = storageService.update(id, storageUpdateReq, principalDetails.getUsername());

		return ResponseEntity.ok(storageRes);
	}
}
