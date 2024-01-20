package com.icebox.freshmate.domain.recipe.application;

import static com.icebox.freshmate.global.error.ErrorCode.*;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_RECIPE;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryRepository;
import com.icebox.freshmate.domain.image.application.ImageService;
import com.icebox.freshmate.domain.image.application.dto.request.ImageDeleteReq;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.image.application.dto.response.ImagesRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeCreateReq;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeUpdateReq;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipeRes;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipesRes;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeImage;
import com.icebox.freshmate.domain.recipe.domain.RecipeImageRepository;
import com.icebox.freshmate.domain.recipe.domain.RecipeRepository;
import com.icebox.freshmate.domain.recipe.domain.RecipeType;
import com.icebox.freshmate.domain.recipegrocery.application.dto.request.RecipeGroceryReq;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGroceryRepository;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {

	private final RecipeRepository recipeRepository;
	private final MemberRepository memberRepository;
	private final RecipeGroceryRepository recipeGroceryRepository;
	private final GroceryRepository groceryRepository;
	private final RecipeImageRepository recipeImageRepository;
	private final ImageService imageService;

	public RecipeRes create(RecipeCreateReq recipeCreateReq, ImageUploadReq imageUploadReq, String username) {
		Member member = getMemberByUsername(username);

		Recipe recipe = saveRecipe(recipeCreateReq, member);

		List<RecipeGroceryRes> recipeGroceriesRes = saveMaterials(recipeCreateReq.materials(), recipe, member.getId());

		ImagesRes imagesRes = saveImages(recipe, imageUploadReq);
		List<ImageRes> images = getImagesRes(imagesRes);

		return RecipeRes.of(recipe, recipeGroceriesRes, images);
	}

	public RecipeRes scrap(Long recipeId, String username) {
		Member owner = getMemberByUsername(username);
		Recipe recipe = getRecipeById(recipeId);
		Member writer = recipe.getWriter();
		validateOwnerAndWriterToScrap(owner.getId(), writer.getId());

		Recipe savedRecipe = saveScrapedRecipe(recipe, writer, owner);

		List<RecipeGroceryRes> recipeGroceriesRes = getRecipeGroceriesRes(savedRecipe.getOriginalRecipeId());
		List<ImageRes> recipeImageRes = getRecipeImagesRes(savedRecipe.getOriginalRecipeId());

		return RecipeRes.of(savedRecipe, recipeGroceriesRes, recipeImageRes);
	}

	public RecipeRes addRecipeImage(Long recipeId, ImageUploadReq imageUploadReq, String username) {
		Member member = getMemberByUsername(username);

		Recipe recipe = getRecipeByIdAndOwnerId(recipeId, member.getId());
		validateScrapedRecipe(recipe);

		validateImageListIsEmpty(imageUploadReq.files());
		saveImages(recipe, imageUploadReq);

		List<ImageRes> images = getImagesRes(recipe.getRecipeImages());
		List<RecipeGroceryRes> recipeGroceriesRes = getRecipeGroceriesRes(recipe);

		return RecipeRes.of(recipe, recipeGroceriesRes, images);
	}

	@Transactional(readOnly = true)
	public RecipeRes findById(Long id) {
		Recipe recipe = getRecipeById(id);

		List<RecipeGroceryRes> recipeGroceriesRes = getRecipeGroceriesRes(recipe);
		List<ImageRes> imagesRes = getRecipeImagesRes(recipe);

		return RecipeRes.of(recipe, recipeGroceriesRes, imagesRes);
	}

	@Transactional(readOnly = true)
	public RecipesRes findAllByWriterId(String username) {
		Member member = getMemberByUsername(username);

		List<Recipe> recipes = recipeRepository.findAllByWriterId(member.getId());

		return RecipesRes.from(recipes);
	}

	@Transactional(readOnly = true)
	public RecipesRes findAllByOwnerId(String username) {
		Member member = getMemberByUsername(username);

		List<Recipe> recipes = recipeRepository.findAllByOwnerId(member.getId());

		return RecipesRes.from(recipes);
	}

	@Transactional(readOnly = true)
	public RecipesRes findAllByGroceryId(Long groceryId) {
		Grocery grocery = getGroceryById(groceryId);

		List<RecipeGrocery> recipeGroceries = recipeGroceryRepository.findAllByGroceryId(grocery.getId());

		List<Recipe> recipes = getRecipesFromRecipeGroceries(recipeGroceries);

		return RecipesRes.from(recipes);
	}

	public RecipeRes update(Long id, RecipeUpdateReq recipeUpdateReq, String username) {
		Member owner = getMemberByUsername(username);
		Recipe recipe = getRecipeByIdAndOwnerId(id, owner.getId());
		validateScrapedRecipe(recipe);

		Recipe updateRecipe = RecipeUpdateReq.toRecipe(recipeUpdateReq, owner);
		recipe.update(updateRecipe);

		List<RecipeGroceryRes> recipeGroceriesRes = getRecipeGroceriesRes(recipe);
		List<ImageRes> imagesRes = getRecipeImagesRes(recipe);

		return RecipeRes.of(recipe, recipeGroceriesRes, imagesRes);
	}

	public RecipeRes addRecipeGrocery(Long recipeId, RecipeGroceryReq recipeGroceryReq, String username) {
		Member member = getMemberByUsername(username);

		Recipe recipe = getRecipeByIdAndOwnerId(recipeId, member.getId());
		validateScrapedRecipe(recipe);

		saveMaterials(List.of(recipeGroceryReq), recipe, member.getId());

		List<RecipeGroceryRes> recipeGroceriesRes = getRecipeGroceriesRes(recipe);
		List<ImageRes> imagesRes = getRecipeImagesRes(recipe);

		return RecipeRes.of(recipe, recipeGroceriesRes, imagesRes);
	}

	public void delete(Long id, String username) {
		Member writer = getMemberByUsername(username);
		Recipe recipe = getRecipeByIdAndOwnerId(id, writer.getId());

		recipeRepository.delete(recipe);
	}

	public RecipeRes removeRecipeGrocery(Long recipeGroceryId, String username) {
		Member member = getMemberByUsername(username);
		RecipeGrocery recipeGrocery = getRecipeGroceryById(recipeGroceryId);

		Recipe recipe = getRecipeByIdAndOwnerId(recipeGrocery.getRecipe().getId(), member.getId());
		validateScrapedRecipe(recipe);

		deleteRecipeGrocery(recipeGrocery);

		List<RecipeGroceryRes> recipeGroceriesRes = getRecipeGroceriesRes(recipe);
		List<ImageRes> imagesRes = getRecipeImagesRes(recipe);

		return RecipeRes.of(recipe, recipeGroceriesRes, imagesRes);
	}

	public RecipeRes removeRecipeImage(Long recipeId, ImageDeleteReq imageDeleteReq, String username) {
		Member member = getMemberByUsername(username);

		Recipe recipe = getRecipeByIdAndOwnerId(recipeId, member.getId());
		validateScrapedRecipe(recipe);
		validateDeleteImageCount(imageDeleteReq.filePaths());

		String imagePath = imageDeleteReq.filePaths().get(0);
		RecipeImage recipeImage = getRecipeImageByRecipeIdAndPath(recipe.getOriginalRecipeId(), imagePath);

		deleteRecipeImage(recipeImage, imageDeleteReq);

		List<RecipeGroceryRes> recipeGroceriesRes = getRecipeGroceriesRes(recipe);
		List<ImageRes> imagesRes = getRecipeImagesRes(recipe);

		return RecipeRes.of(recipe, recipeGroceriesRes, imagesRes);
	}

	private void deleteRecipeImage(RecipeImage recipeImage, ImageDeleteReq imageDeleteReq) {
		recipeImage.getRecipe().removeRecipeImage(recipeImage);
		recipeImageRepository.delete(recipeImage);
		imageService.delete(imageDeleteReq);
	}

	private RecipeImage getRecipeImageByRecipeIdAndPath(Long recipeId, String imagePath) {

		return recipeImageRepository.findByRecipeIdAndPath(recipeId, imagePath)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_RECIPE_IMAGE_BY_RECIPE_ID_AND_PATH : recipeId = {}, imagePath = {}", recipeId, imagePath);

				return new EntityNotFoundException(NOT_FOUND_IMAGE);
			});
	}

	private void validateDeleteImageCount(List<String> imagePaths) {
		if (imagePaths.size() != 1) {
			log.warn("DELETE:WRITE:EXCESSIVE_DELETE_IMAGE_COUNT : requested image path count = {}", imagePaths.size());

			throw new BusinessException(EXCESSIVE_DELETE_IMAGE_COUNT);
		}
	}

	private Recipe saveRecipe(RecipeCreateReq recipeCreateReq, Member member) {
		Recipe recipe = RecipeCreateReq.toRecipe(recipeCreateReq, member);
		Recipe savedRecipe = recipeRepository.save(recipe);
		savedRecipe.updateOriginalRecipeId(savedRecipe.getId());

		return savedRecipe;
	}

	private Recipe saveScrapedRecipe(Recipe recipe, Member writer, Member owner) {
		Recipe originalRecipe = toScrappedRecipe(recipe, writer, owner);
		Recipe savedRecipe = recipeRepository.save(originalRecipe);
		savedRecipe.updateOriginalRecipeId(recipe.getId());

		return savedRecipe;
	}

	private void deleteRecipeGrocery(RecipeGrocery recipeGrocery) {
		recipeGrocery.getRecipe().removeRecipeGrocery(recipeGrocery);
		recipeGrocery.getGrocery().removeRecipeGrocery(recipeGrocery);
		recipeGroceryRepository.delete(recipeGrocery);
	}

	private Recipe getRecipeByIdAndOwnerId(Long recipeId, Long ownerId) {
		return recipeRepository.findByIdAndOwnerId(recipeId, ownerId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_RECIPE_BY_ID_AND_OWNER_ID : recipeId = {}, ownerId = {}", recipeId, ownerId);

				return new EntityNotFoundException(NOT_FOUND_RECIPE);
			});
	}

	private Recipe getRecipeById(Long recipeId) {
		return recipeRepository.findById(recipeId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_RECIPE_BY_ID : {}", recipeId);

				return new EntityNotFoundException(NOT_FOUND_RECIPE);
			});
	}

	private Member getMemberByUsername(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);

				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}

	private void validateOwnerAndWriterToScrap(Long ownerId, Long writerId) {
		if (Objects.equals(ownerId, writerId)) {
			log.warn("POST:WRITE:INVALID_SCRAP_ATTEMPT_TO_OWN_RECIPE : ownerId = {}, writerId = {}", ownerId, writerId);

			throw new BusinessException(INVALID_SCRAP_ATTEMPT_TO_OWN_RECIPE);
		}
	}

	private void validateScrapedRecipe(Recipe recipe) {
		if (recipe.getRecipeType().equals(RecipeType.SCRAPED)) {
			log.warn("PATCH:WRITE:INVALID_UPDATE_ATTEMPT_TO_SCRAPED_RECIPE : recipeId = {}", recipe.getId());

			throw new BusinessException(INVALID_UPDATE_ATTEMPT_TO_SCRAPED_RECIPE);
		}
	}

	private Recipe toScrappedRecipe(Recipe recipe, Member writer, Member owner) {

		return Recipe.builder()
			.writer(writer)
			.owner(owner)
			.recipeType(RecipeType.SCRAPED)
			.title(recipe.getTitle())
			.content(recipe.getContent())
			.build();
	}

	private List<RecipeGroceryRes> saveMaterials(List<RecipeGroceryReq> materials, Recipe recipe, Long memberId) {
		List<RecipeGrocery> recipeGroceries = materials.stream()
			.map(material -> {
				RecipeGrocery recipeGrocery = getRecipeGrocery(material, memberId, recipe);
				saveRecipeGrocery(recipe, recipeGrocery.getGrocery(), recipeGrocery);

				return recipeGrocery;
			})
			.toList();

		return RecipeGroceryRes.from(recipeGroceries);
	}

	private void saveRecipeGrocery(Recipe recipe, Grocery grocery, RecipeGrocery recipeGrocery) {
		recipe.addRecipeGrocery(recipeGrocery);

		if (grocery != null) {
			grocery.addRecipeGrocery(recipeGrocery);
		}

		recipeGroceryRepository.save(recipeGrocery);
	}

	private RecipeGrocery getRecipeGrocery(RecipeGroceryReq recipeGroceryReq, Long memberId, Recipe recipe) {
		Grocery grocery = Optional.ofNullable(recipeGroceryReq.groceryId())
			.map(groceryId -> {
				Grocery foundGrocery = getGroceryByIdAndMemberId(groceryId, memberId);
				validateRecipeGroceryName(foundGrocery, recipeGroceryReq.groceryName());
				validateDuplicatedRecipeGrocery(recipe.getId(), groceryId);

				return foundGrocery;
			})
			.orElse(null);

		return buildRecipeGrocery(recipe, grocery, recipeGroceryReq.groceryName(), recipeGroceryReq.groceryQuantity());
	}

	private Grocery getGroceryByIdAndMemberId(Long groceryId, Long memberId) {

		return groceryRepository.findByIdAndMemberId(groceryId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORAGE_BY_ID_AND_MEMBER_ID : groceryId = {}, memberId = {}", groceryId, memberId);

				return new EntityNotFoundException(NOT_FOUND_GROCERY);
			});
	}

	private Grocery getGroceryById(Long groceryId) {

		return groceryRepository.findById(groceryId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORAGE_BY_ID : groceryId = {}", groceryId);

				return new EntityNotFoundException(NOT_FOUND_GROCERY);
			});
	}

	private void validateRecipeGroceryName(Grocery grocery, String requestedGroceryName) {
		if (!grocery.getName().equals(requestedGroceryName)) {
			log.warn("INVALID_RECIPE_GROCERY_NAME : groceryId = {}, groceryName = {}, requestGroceryName = {}", grocery.getId(), grocery.getName(), requestedGroceryName);

			throw new BusinessException(ErrorCode.INVALID_RECIPE_GROCERY_NAME);
		}
	}

	private void validateDuplicatedRecipeGrocery(Long recipeId, Long groceryId) {
		if (recipeGroceryRepository.existsByRecipeIdAndGroceryId(recipeId, groceryId)) {
			log.warn("DUPLICATED_RECIPE_GROCERY : recipeId = {}, groceryId = {}", recipeId, groceryId);

			throw new BusinessException(ErrorCode.DUPLICATED_RECIPE_GROCERY);
		}
	}

	private RecipeGrocery buildRecipeGrocery(Recipe recipe, Grocery grocery, String groceryName, String groceryQuantity) {

		return RecipeGrocery.builder()
			.recipe(recipe)
			.grocery(grocery)
			.groceryName(groceryName)
			.groceryQuantity(groceryQuantity)
			.build();
	}

	private RecipeGrocery getRecipeGroceryById(Long recipeGroceryId) {

		return recipeGroceryRepository.findById(recipeGroceryId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_RECIPE_GROCERY_BY_ID : recipeGroceryId = {}", recipeGroceryId);

				return new EntityNotFoundException(NOT_FOUND_RECIPE_GROCERY);
			});
	}

	private List<ImageRes> getImagesRes(ImagesRes imagesRes) {

		return Optional.ofNullable(imagesRes)
			.map(ImagesRes::images)
			.orElse(null);
	}

	private List<ImageRes> getImagesRes(List<RecipeImage> recipeImages) {

		return recipeImages.stream()
			.map(recipeImage -> ImageRes.of(recipeImage.getFileName(), recipeImage.getPath()))
			.toList();
	}

	private ImagesRes saveImages(Recipe recipe, ImageUploadReq imageUploadReq) {

		return Optional.ofNullable(imageUploadReq.files())
			.map(files -> imageService.store(imageUploadReq))
			.map(imagesRes -> {
				List<RecipeImage> recipeImages = saveImages(recipe, imagesRes);
				recipe.addRecipeImages(recipeImages);

				return imagesRes;
			})
			.orElse(null);
	}

	private List<RecipeImage> saveImages(Recipe recipe, ImagesRes imagesRes) {

		return imagesRes.images().stream()
			.map(imageRes -> buildRecipeImage(imageRes, recipe))
			.peek(recipeImageRepository::save)
			.toList();
	}

	private RecipeImage buildRecipeImage(ImageRes imageRes, Recipe recipe) {

		return RecipeImage.builder()
			.fileName(imageRes.fileName())
			.path(imageRes.path())
			.recipe(recipe)
			.build();
	}

	private List<ImageRes> getRecipeImagesRes(Recipe recipe) {
		List<RecipeImage> recipeImages = recipe.getRecipeImages();

		return getImagesRes(recipeImages);
	}

	private List<ImageRes> getRecipeImagesRes(Long recipeId) {
		List<RecipeImage> recipeImages = recipeImageRepository.findAllByRecipeId(recipeId);

		return getImagesRes(recipeImages);
	}

	private List<RecipeGroceryRes> getRecipeGroceriesRes(Recipe recipe) {
		List<RecipeGrocery> recipeGroceries = recipe.getRecipeGroceries();

		return RecipeGroceryRes.from(recipeGroceries);
	}

	private List<RecipeGroceryRes> getRecipeGroceriesRes(Long recipeId) {
		List<RecipeGrocery> recipeGroceries = recipeGroceryRepository.findAllByRecipeId(recipeId);

		return RecipeGroceryRes.from(recipeGroceries);
	}

	private List<Recipe> getRecipesFromRecipeGroceries(List<RecipeGrocery> recipeGroceries) {

		return recipeGroceries.stream()
			.map(recipeGrocery -> recipeGrocery.getRecipe().getId())
			.map(this::getRecipeById)
			.toList();
	}

	private void validateImageListIsEmpty(List<MultipartFile> images) {
		if (images.size() == 1 && Objects.equals(images.get(0).getOriginalFilename(), "")) {
			log.warn("PATCH:WRITE:EMPTY_IMAGE");

			throw new BusinessException(EMPTY_IMAGE);
		}
	}
}
