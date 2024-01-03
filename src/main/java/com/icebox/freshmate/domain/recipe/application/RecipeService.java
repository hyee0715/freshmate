package com.icebox.freshmate.domain.recipe.application;

import static com.icebox.freshmate.global.error.ErrorCode.*;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_RECIPE;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeReq;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipeRes;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipesRes;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeRepository;
import com.icebox.freshmate.domain.recipe.domain.RecipeType;
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

	public RecipeRes create(RecipeReq recipeReq, String username) {
		Member member = getMemberByUsername(username);

		Recipe recipe = RecipeReq.toRecipe(recipeReq, member);
		Recipe savedRecipe = recipeRepository.save(recipe);

		savedRecipe.updateOriginalRecipeId(savedRecipe.getId());

		return RecipeRes.from(savedRecipe);
	}

	public RecipeRes scrap(Long recipeId, String username) {
		Member owner = getMemberByUsername(username);
		Recipe recipe = getRecipeById(recipeId);
		Member writer = recipe.getWriter();

		validateOwnerAndWriterToScrap(owner.getId(), writer.getId());

		Recipe originalRecipe = toScrappedRecipe(recipe, writer, owner);

		Recipe savedRecipe = recipeRepository.save(originalRecipe);
		savedRecipe.updateOriginalRecipeId(recipe.getId());

		return RecipeRes.from(savedRecipe);
	}

	@Transactional(readOnly = true)
	public RecipeRes findById(Long id) {
		Recipe recipe = getRecipeById(id);

		return RecipeRes.from(recipe);
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

	public RecipeRes update(Long id, RecipeReq recipeReq, String username) {
		Member owner = getMemberByUsername(username);
		Recipe recipe = getRecipeByIdAndOwnerId(id, owner.getId());

		validateScrapedRecipe(recipe);

		Recipe updateRecipe = RecipeReq.toRecipe(recipeReq, owner);
		recipe.update(updateRecipe);

		return RecipeRes.from(recipe);
	}

	public void delete(Long id, String username) {
		Member writer = getMemberByUsername(username);
		Recipe recipe = getRecipeByIdAndOwnerId(id, writer.getId());

		recipeRepository.delete(recipe);
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
			.material(recipe.getMaterial())
			.content(recipe.getContent())
			.build();
	}
}
