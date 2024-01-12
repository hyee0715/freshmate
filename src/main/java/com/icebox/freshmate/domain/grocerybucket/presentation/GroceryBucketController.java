package com.icebox.freshmate.domain.grocerybucket.presentation;

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
import com.icebox.freshmate.domain.grocerybucket.application.GroceryBucketService;
import com.icebox.freshmate.domain.grocerybucket.application.dto.request.GroceryBucketReq;
import com.icebox.freshmate.domain.grocerybucket.application.dto.response.GroceryBucketRes;
import com.icebox.freshmate.domain.grocerybucket.application.dto.response.GroceryBucketsRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/grocery-buckets")
@RestController
public class GroceryBucketController {

	private final GroceryBucketService groceryBucketService;

	@PostMapping
	public ResponseEntity<GroceryBucketRes> create(@Validated @RequestBody GroceryBucketReq groceryBucketReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		GroceryBucketRes groceryBucketRes = groceryBucketService.create(groceryBucketReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(groceryBucketRes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<GroceryBucketRes> findById(@PathVariable Long id) {
		GroceryBucketRes groceryBucketRes = groceryBucketService.findById(id);

		return ResponseEntity.ok(groceryBucketRes);
	}

	@GetMapping
	public ResponseEntity<GroceryBucketsRes> findAll(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		GroceryBucketsRes groceryBucketsRes = groceryBucketService.findAll(principalDetails.getUsername());

		return ResponseEntity.ok(groceryBucketsRes);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<GroceryBucketRes> update(@PathVariable Long id, @Validated @RequestBody GroceryBucketReq groceryBucketReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		GroceryBucketRes groceryBucketRes = groceryBucketService.update(id, groceryBucketReq, principalDetails.getUsername());

		return ResponseEntity.ok(groceryBucketRes);
	}
}
