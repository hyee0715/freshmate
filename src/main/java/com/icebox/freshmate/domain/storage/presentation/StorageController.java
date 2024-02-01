package com.icebox.freshmate.domain.storage.presentation;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	private static final String DEFAULT_PAGE_SIZE = "5";

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
	public ResponseEntity<StoragesRes> findAllByRefrigeratorId(@PathVariable Long refrigeratorId,
															   @RequestParam(value = "sort-by", required = false, defaultValue = "updatedAtDesc") String sortBy,
															   @RequestParam(value = "type", required = false, defaultValue = "all") String storageType,
															   @RequestParam(required = false, defaultValue = "0") int page,
															   @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
															   @AuthenticationPrincipal PrincipalDetails principalDetails) {
		page = Math.max(page - 1, 0);
		PageRequest pageable = PageRequest.of(page, size);

		StoragesRes storagesRes = storageService.findAllByRefrigeratorId(refrigeratorId, sortBy, storageType, pageable, principalDetails.getUsername());

		return ResponseEntity.ok(storagesRes);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<StorageRes> update(@PathVariable Long id, @Validated @RequestBody StorageUpdateReq storageUpdateReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		StorageRes storageRes = storageService.update(id, storageUpdateReq, principalDetails.getUsername());

		return ResponseEntity.ok(storageRes);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		storageService.delete(id, principalDetails.getUsername());

		return ResponseEntity.noContent()
			.build();
	}
}
