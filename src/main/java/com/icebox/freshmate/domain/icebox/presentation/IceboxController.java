package com.icebox.freshmate.domain.icebox.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.icebox.application.IceboxService;
import com.icebox.freshmate.domain.icebox.application.dto.request.IceboxReq;
import com.icebox.freshmate.domain.icebox.application.dto.response.IceboxRes;

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
}
