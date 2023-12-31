package com.icebox.freshmate.domain.grocery.presentation;

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
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.grocery.application.GroceryService;
import com.icebox.freshmate.domain.grocery.application.dto.request.GroceryReq;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceriesRes;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceryRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/groceries")
@RestController
public class GroceryController {

	private final GroceryService groceryService;

	@PostMapping
	public ResponseEntity<GroceryRes> create(@Validated @RequestBody GroceryReq groceryReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		GroceryRes groceryRes = groceryService.create(groceryReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(groceryRes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<GroceryRes> findById(@PathVariable Long id) {
		GroceryRes groceryRes = groceryService.findById(id);

		return ResponseEntity.ok(groceryRes);
	}

	@GetMapping("/storages/{storageId}")
	public ResponseEntity<GroceriesRes> findAllByStorageId(@PathVariable Long storageId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		GroceriesRes groceriesRes = groceryService.findAllByStorageId(storageId, principalDetails.getUsername());

		return ResponseEntity.ok(groceriesRes);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<GroceryRes> update(@PathVariable Long id, @Validated @RequestBody GroceryReq groceryReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		GroceryRes groceryRes = groceryService.update(id, groceryReq, principalDetails.getUsername());

		return ResponseEntity.ok(groceryRes);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		groceryService.delete(id, principalDetails.getUsername());

		return ResponseEntity.noContent()
			.build();
	}
}
