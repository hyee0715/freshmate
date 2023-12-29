package com.icebox.freshmate.domain.icebox.presentation;

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
import com.icebox.freshmate.domain.icebox.application.IceboxService;
import com.icebox.freshmate.domain.icebox.application.dto.request.IceboxReq;
import com.icebox.freshmate.domain.icebox.application.dto.response.IceboxRes;
import com.icebox.freshmate.domain.icebox.application.dto.response.IceboxesRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/iceboxes")
@RestController
public class IceboxController {

	private final IceboxService iceboxService;

	@PostMapping
	public ResponseEntity<IceboxRes> create(@Validated @RequestBody IceboxReq iceboxReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		IceboxRes iceboxRes = iceboxService.create(iceboxReq, principalDetails.getUsername());

		return ResponseEntity.ok(iceboxRes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<IceboxRes> findById(@PathVariable Long id) {
		IceboxRes iceboxRes = iceboxService.findById(id);

		return ResponseEntity.ok(iceboxRes);
	}

	@GetMapping
	public ResponseEntity<IceboxesRes> findAll(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		IceboxesRes iceboxesRes = iceboxService.findAll(principalDetails.getUsername());

		return ResponseEntity.ok(iceboxesRes);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<IceboxRes> update(@PathVariable Long id, @Validated @RequestBody IceboxReq iceboxReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		IceboxRes iceboxRes = iceboxService.update(id, iceboxReq, principalDetails.getUsername());

		return ResponseEntity.ok(iceboxRes);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		iceboxService.delete(id, principalDetails.getUsername());

		return ResponseEntity.noContent()
			.build();
	}
}
