package com.icebox.freshmate.domain.refrigerator.presentation;

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
import com.icebox.freshmate.domain.refrigerator.application.RefrigeratorService;
import com.icebox.freshmate.domain.refrigerator.application.dto.request.RefrigeratorReq;
import com.icebox.freshmate.domain.refrigerator.application.dto.response.RefrigeratorRes;
import com.icebox.freshmate.domain.refrigerator.application.dto.response.RefrigeratorsRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/refrigerators")
@RestController
public class RefrigeratorController {

	private static final String DEFAULT_PAGE_SIZE = "5";

	private final RefrigeratorService refrigeratorService;

	@PostMapping
	public ResponseEntity<RefrigeratorRes> create(@Validated @RequestBody RefrigeratorReq refrigeratorReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		RefrigeratorRes refrigeratorRes = refrigeratorService.create(refrigeratorReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(refrigeratorRes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RefrigeratorRes> findById(@PathVariable Long id) {
		RefrigeratorRes refrigeratorRes = refrigeratorService.findById(id);

		return ResponseEntity.ok(refrigeratorRes);
	}

	@GetMapping
	public ResponseEntity<RefrigeratorsRes> findAll(@RequestParam(value = "sort-by", required = false, defaultValue = "updatedAtDesc") String sortBy,
													@RequestParam(value= "last-page-name", required = false) String lastPageName,
													@RequestParam(value = "last-page-updated-at", required = false) String lastPageUpdatedAt,
													@RequestParam(required = false, defaultValue = "0") int page,
													@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
													@AuthenticationPrincipal PrincipalDetails principalDetails) {
		page = Math.max(page - 1, 0);
		PageRequest pageable = PageRequest.of(page, size);

		RefrigeratorsRes refrigeratorsRes = refrigeratorService.findAll(sortBy, pageable, lastPageName, lastPageUpdatedAt, principalDetails.getUsername());

		return ResponseEntity.ok(refrigeratorsRes);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<RefrigeratorRes> update(@PathVariable Long id, @Validated @RequestBody RefrigeratorReq refrigeratorReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		RefrigeratorRes refrigeratorRes = refrigeratorService.update(id, refrigeratorReq, principalDetails.getUsername());

		return ResponseEntity.ok(refrigeratorRes);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		refrigeratorService.delete(id, principalDetails.getUsername());

		return ResponseEntity.noContent()
			.build();
	}
}
