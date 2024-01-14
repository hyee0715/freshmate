package com.icebox.freshmate.domain.recipebucket.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.recipebucket.application.RecipeBucketService;
import com.icebox.freshmate.domain.recipebucket.application.dto.request.RecipeBucketReq;
import com.icebox.freshmate.domain.recipebucket.application.dto.response.RecipeBucketRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/recipe-buckets")
@RestController
public class RecipeBucketController {

	private final RecipeBucketService recipeBucketService;

	@PostMapping
	public ResponseEntity<RecipeBucketRes> create(@Validated @RequestBody RecipeBucketReq recipeBucketReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		RecipeBucketRes recipeBucketRes = recipeBucketService.create(recipeBucketReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(recipeBucketRes);
	}
}
