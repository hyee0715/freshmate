package com.icebox.freshmate.domain.refrigerator.presentation;

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

	private final RefrigeratorService refrigeratorService;

	@PostMapping
	public ResponseEntity<RefrigeratorRes> create(@Validated @RequestBody RefrigeratorReq refrigeratorReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		RefrigeratorRes refrigeratorRes = refrigeratorService.create(refrigeratorReq, principalDetails.getUsername());

		return ResponseEntity.ok(refrigeratorRes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RefrigeratorRes> findById(@PathVariable Long id) {
		RefrigeratorRes refrigeratorRes = refrigeratorService.findById(id);

		return ResponseEntity.ok(refrigeratorRes);
	}

	@GetMapping
	public ResponseEntity<RefrigeratorsRes> findAll(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		RefrigeratorsRes refrigeratorsRes = refrigeratorService.findAll(principalDetails.getUsername());

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
