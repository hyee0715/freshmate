package com.icebox.freshmate.domain.recipe.presentation;

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
import com.icebox.freshmate.domain.recipe.application.RecipeService;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeReq;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipeRes;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipesRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/recipes")
@RestController
public class RecipeController {

	private final RecipeService recipeService;

	@PostMapping
	public ResponseEntity<RecipeRes> create(@Validated @RequestBody RecipeReq recipeReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		RecipeRes recipeRes = recipeService.create(recipeReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(recipeRes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RecipeRes> findById(@PathVariable Long id) {
		RecipeRes recipeRes = recipeService.findById(id);

		return ResponseEntity.ok(recipeRes);
	}

	@GetMapping("/writers")
	public ResponseEntity<RecipesRes> findAllByWriterId(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		RecipesRes recipesRes = recipeService.findAllByWriterId(principalDetails.getUsername());

		return ResponseEntity.ok(recipesRes);
	}

	@GetMapping("/owners")
	public ResponseEntity<RecipesRes> findAllByOwnerId(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		RecipesRes recipesRes = recipeService.findAllByOwnerId(principalDetails.getUsername());

		return ResponseEntity.ok(recipesRes);
	}

	@GetMapping("/members")
	public ResponseEntity<RecipesRes> findAllByMemberId(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		RecipesRes recipesRes = recipeService.findAllByMemberId(principalDetails.getUsername());

		return ResponseEntity.ok(recipesRes);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<RecipeRes> update(@PathVariable Long id, @Validated @RequestBody RecipeReq recipeReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		RecipeRes recipeRes = recipeService.update(id, recipeReq, principalDetails.getUsername());

		return ResponseEntity.ok(recipeRes);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<RecipeRes> delete(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		recipeService.delete(id, principalDetails.getUsername());

		return ResponseEntity.noContent()
			.build();
	}
}
