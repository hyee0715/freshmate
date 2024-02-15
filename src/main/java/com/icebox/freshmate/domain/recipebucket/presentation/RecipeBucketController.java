package com.icebox.freshmate.domain.recipebucket.presentation;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.recipebucket.application.RecipeBucketService;
import com.icebox.freshmate.domain.recipebucket.application.dto.request.RecipeBucketReq;
import com.icebox.freshmate.domain.recipebucket.application.dto.response.RecipeBucketRes;
import com.icebox.freshmate.domain.recipebucket.application.dto.response.RecipeBucketsRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/recipe-buckets")
@RestController
public class RecipeBucketController {

	private static final String DEFAULT_PAGE_SIZE = "5";

	private final RecipeBucketService recipeBucketService;

	@PostMapping
	public ResponseEntity<RecipeBucketRes> create(@Validated @RequestBody RecipeBucketReq recipeBucketReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		RecipeBucketRes recipeBucketRes = recipeBucketService.create(recipeBucketReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(recipeBucketRes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RecipeBucketRes> findById(@PathVariable Long id) {
		RecipeBucketRes recipeBucketRes = recipeBucketService.findById(id);

		return ResponseEntity.ok(recipeBucketRes);
	}

	@GetMapping
	public ResponseEntity<RecipeBucketsRes> findAllByMemberId(@RequestParam(required = false, defaultValue = "0") int page,
															  @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
															  @AuthenticationPrincipal PrincipalDetails principalDetails) {
		page = Math.max(page - 1, 0);
		PageRequest pageable = PageRequest.of(page, size);

		RecipeBucketsRes recipeBucketsRes = recipeBucketService.findAllByMemberId(pageable, principalDetails.getUsername());

		return ResponseEntity.ok(recipeBucketsRes);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		recipeBucketService.delete(id, principalDetails.getUsername());

		return ResponseEntity.noContent()
			.build();
	}
}
