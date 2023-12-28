package com.icebox.freshmate.domain.icebox.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<IceboxRes> create(@Validated @RequestBody IceboxReq iceboxReq) {
		IceboxRes iceboxRes = iceboxService.create(iceboxReq);

		return ResponseEntity.ok(iceboxRes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<IceboxRes> findById(@PathVariable Long id) {
		IceboxRes iceboxRes = iceboxService.findById(id);

		return ResponseEntity.ok(iceboxRes);
	}

	@GetMapping
	public ResponseEntity<IceboxesRes> findAll() {
		IceboxesRes iceboxesRes = iceboxService.findAll();

		return ResponseEntity.ok(iceboxesRes);
	}
}
