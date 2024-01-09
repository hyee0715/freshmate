package com.icebox.freshmate.domain.recipe.application;

import static com.icebox.freshmate.global.error.ErrorCode.*;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_RECIPE;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryRepository;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeCreateReq;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeUpdateReq;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipeRes;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipesRes;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
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

	public RecipeRes create(RecipeCreateReq recipeCreateReq, String username) {
		Member member = getMemberByUsername(username);

		Recipe recipe = RecipeCreateReq.toRecipe(recipeCreateReq, member);
		Recipe savedRecipe = recipeRepository.save(recipe);
		savedRecipe.updateOriginalRecipeId(savedRecipe.getId());

		List<RecipeGroceryRes> recipeGroceriesRes = saveMaterials(recipeCreateReq.materials(), savedRecipe, member.getId());

		return RecipeRes.of(savedRecipe, recipeGroceriesRes);
	}

	public RecipeRes scrap(Long recipeId, String username) {
		Member owner = getMemberByUsername(username);
		Recipe recipe = getRecipeById(recipeId);
		Member writer = recipe.getWriter();
		validateOwnerAndWriterToScrap(owner.getId(), writer.getId());

		Recipe originalRecipe = toScrappedRecipe(recipe, writer, owner);
		Recipe savedRecipe = recipeRepository.save(originalRecipe);
		savedRecipe.updateOriginalRecipeId(recipe.getId());

		List<RecipeGrocery> recipeGroceries = recipeGroceryRepository.findAllByRecipeId(savedRecipe.getOriginalRecipeId());
		List<RecipeGroceryRes> recipeGroceriesRes = RecipeGroceryRes.from(recipeGroceries);

		return RecipeRes.of(savedRecipe, recipeGroceriesRes);
	}

	@Transactional(readOnly = true)
	public RecipeRes findById(Long id) {
		Recipe recipe = getRecipeById(id);

		List<RecipeGrocery> recipeGroceries = recipeGroceryRepository.findAllByRecipeId(recipe.getId());
		List<RecipeGroceryRes> recipeGroceriesRes = RecipeGroceryRes.from(recipeGroceries);

		return RecipeRes.of(recipe, recipeGroceriesRes);
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
	public RecipesRes findAllByMemberId(String username) {
		Member member = getMemberByUsername(username);

		List<Recipe> recipes = recipeRepository.findAllByMemberId(member.getId());

		return RecipesRes.from(recipes);
	}

	@Transactional(readOnly = true)
	public RecipesRes findAllByGroceryId(Long groceryId) {
		Grocery grocery = getGroceryById(groceryId);

		List<RecipeGrocery> recipeGroceries = recipeGroceryRepository.findAllByGroceryId(grocery.getId());

		List<Recipe> recipes = recipeGroceries.stream()
			.map(x -> x.getRecipe().getId())
			.map(this::getRecipeById)
			.toList();

		return RecipesRes.from(recipes);
	}

	public RecipeRes update(Long id, RecipeUpdateReq recipeUpdateReq, String username) {
		Member owner = getMemberByUsername(username);
		Recipe recipe = getRecipeByIdAndOwnerId(id, owner.getId());
		validateScrapedRecipe(recipe);

		Recipe updateRecipe = RecipeUpdateReq.toRecipe(recipeUpdateReq, owner);
		recipe.update(updateRecipe);

		List<RecipeGrocery> recipeGroceries = recipeGroceryRepository.findAllByRecipeId(recipe.getId());
		List<RecipeGroceryRes> recipeGroceriesRes = RecipeGroceryRes.from(recipeGroceries);

		return RecipeRes.of(recipe, recipeGroceriesRes);
	}

	public RecipeRes addRecipeGrocery(Long recipeId, RecipeGroceryReq recipeGroceryReq, String username) {
		Member member = getMemberByUsername(username);

		Recipe recipe = getRecipeByIdAndOwnerId(recipeId, member.getId());
		validateScrapedRecipe(recipe);

		saveMaterials(List.of(recipeGroceryReq), recipe, member.getId());
		List<RecipeGrocery> recipeGroceries = recipeGroceryRepository.findAllByRecipeId(recipe.getId());

		List<RecipeGroceryRes> recipeGroceriesRes = RecipeGroceryRes.from(recipeGroceries);

		return RecipeRes.of(recipe, recipeGroceriesRes);
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

		recipeGrocery.getRecipe().removeRecipeGrocery(recipeGrocery);
		recipeGroceryRepository.delete(recipeGrocery);

		List<RecipeGrocery> recipeGroceries = recipe.getRecipeGroceries();
		List<RecipeGroceryRes> recipeGroceriesRes = RecipeGroceryRes.from(recipeGroceries);

		return RecipeRes.of(recipe, recipeGroceriesRes);
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
				saveRecipeGrocery(recipe, recipeGrocery);

				return recipeGrocery;
			})
			.toList();

		return RecipeGroceryRes.from(recipeGroceries);
	}

	private void saveRecipeGrocery(Recipe recipe, RecipeGrocery recipeGrocery) {
		recipe.addRecipeGrocery(recipeGrocery);
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

		return buildRecipeGrocery(recipe, grocery, recipeGroceryReq.groceryName());
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
		if(recipeGroceryRepository.existsByRecipeIdAndGroceryId(recipeId, groceryId)) {
			log.warn("DUPLICATED_RECIPE_GROCERY : recipeId = {}, groceryId = {}", recipeId, groceryId);
			throw new BusinessException(ErrorCode.DUPLICATED_RECIPE_GROCERY);
		}
	}

	private RecipeGrocery buildRecipeGrocery(Recipe recipe, Grocery grocery, String groceryName) {

		return RecipeGrocery.builder()
			.recipe(recipe)
			.grocery(grocery)
			.groceryName(groceryName)
			.build();
	}

	private RecipeGrocery getRecipeGroceryById(Long recipeGroceryId) {

		return recipeGroceryRepository.findById(recipeGroceryId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_RECIPE_GROCERY_BY_ID : recipeGroceryId = {}", recipeGroceryId);
				return new EntityNotFoundException(NOT_FOUND_RECIPE_GROCERY);
			});
	}
}
