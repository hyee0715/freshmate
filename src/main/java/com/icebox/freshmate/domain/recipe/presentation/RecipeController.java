package com.icebox.freshmate.domain.recipe.presentation;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.image.application.dto.request.ImageDeleteReq;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.recipe.application.RecipeService;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeCreateReq;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeUpdateReq;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipeRes;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipesRes;
import com.icebox.freshmate.domain.recipegrocery.application.dto.request.RecipeGroceryReq;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/recipes")
@RestController
public class RecipeController {

	private static final String DEFAULT_PAGE_SIZE = "5";

	private final RecipeService recipeService;

	@PostMapping
	public ResponseEntity<RecipeRes> create(@Validated @RequestPart RecipeCreateReq recipeCreateReq, @RequestPart(required = false) List<MultipartFile> imageFiles, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		ImageUploadReq imageUploadReq = new ImageUploadReq(imageFiles);

		RecipeRes recipeRes = recipeService.create(recipeCreateReq, imageUploadReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(recipeRes);
	}

	@PostMapping("/scrap")
	public ResponseEntity<RecipeRes> scrap(@RequestParam("recipe-id") Long recipeId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		RecipeRes recipeRes = recipeService.scrap(recipeId, principalDetails.getUsername());

		return ResponseEntity.ok(recipeRes);
	}

	@PostMapping("/recipe-images/{recipeId}")
	public ResponseEntity<RecipeRes> addRecipeImage(@PathVariable Long recipeId, MultipartFile imageFile, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		ImageUploadReq imageUploadReq = new ImageUploadReq(List.of(imageFile));

		RecipeRes recipeRes = recipeService.addRecipeImage(recipeId, imageUploadReq, principalDetails.getUsername());

		return ResponseEntity.ok(recipeRes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RecipeRes> findById(@PathVariable Long id) {
		RecipeRes recipeRes = recipeService.findById(id);

		return ResponseEntity.ok(recipeRes);
	}

	@GetMapping
	public ResponseEntity<RecipesRes> findAllByMemberIdAndRecipeType(@RequestParam(value = "search-type", required = false, defaultValue = "all") String searchType,
																	 @RequestParam(required = false, defaultValue = "") String keyword,
																	 @RequestParam(value = "sort-by", required = false, defaultValue = "updatedAtDesc") String sortBy,
																	 @RequestParam(value = "recipe-type", required = false, defaultValue = "all") String recipeType,
																	 @RequestParam(value = "last-page-title", required = false) String lastPageTitle,
																	 @RequestParam(value = "last-page-updated-at", required = false) String lastPageUpdatedAt,
																	 @RequestParam(required = false, defaultValue = "0") int page,
																	 @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
																	 @AuthenticationPrincipal PrincipalDetails principalDetails) {
		page = Math.max(page - 1, 0);
		PageRequest pageable = PageRequest.of(page, size);

		RecipesRes recipesRes = recipeService.findAllByMemberIdAndRecipeType(searchType, keyword, sortBy, recipeType, pageable, lastPageTitle, lastPageUpdatedAt, principalDetails.getUsername());

		return ResponseEntity.ok(recipesRes);
	}

	@GetMapping("/grocery")
	public ResponseEntity<RecipesRes> findAllByGroceryId(@RequestParam("grocery-id") Long groceryId,
														 @RequestParam(required = false, defaultValue = "0") int page,
														 @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size) {
		page = Math.max(page - 1, 0);
		PageRequest pageable = PageRequest.of(page, size);

		RecipesRes recipesRes = recipeService.findAllByGroceryId(groceryId, pageable);

		return ResponseEntity.ok(recipesRes);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<RecipeRes> update(@PathVariable Long id, @Validated @RequestBody RecipeUpdateReq recipeUpdateReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		RecipeRes recipeRes = recipeService.update(id, recipeUpdateReq, principalDetails.getUsername());

		return ResponseEntity.ok(recipeRes);
	}

	@PatchMapping("/recipe-groceries/{recipeId}")
	public ResponseEntity<RecipeRes> addRecipeGrocery(@PathVariable Long recipeId, @Validated @RequestBody RecipeGroceryReq recipeGroceryReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		RecipeRes recipeRes = recipeService.addRecipeGrocery(recipeId, recipeGroceryReq, principalDetails.getUsername());

		return ResponseEntity.ok(recipeRes);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		recipeService.delete(id, principalDetails.getUsername());

		return ResponseEntity.noContent()
			.build();
	}

	@DeleteMapping
	public ResponseEntity<RecipeRes> removeRecipeGrocery(@RequestParam("recipe-groceries-id") Long recipeGroceryId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		RecipeRes recipeRes = recipeService.removeRecipeGrocery(recipeGroceryId, principalDetails.getUsername());

		return ResponseEntity.ok(recipeRes);
	}

	@DeleteMapping("/recipe-images/{recipeId}")
	public ResponseEntity<RecipeRes> removeRecipeImage(@PathVariable Long recipeId, @RequestBody ImageDeleteReq imageDeleteReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		RecipeRes recipeRes = recipeService.removeRecipeImage(recipeId, imageDeleteReq, principalDetails.getUsername());

		return ResponseEntity.ok(recipeRes);
	}
}
