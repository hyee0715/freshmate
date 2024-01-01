package com.icebox.freshmate.domain.recipe.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_RECIPE;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeReq;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipeRes;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipesRes;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeRepository;
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
		Member member = getMember(username);

		Recipe recipe = RecipeReq.toRecipe(recipeReq, member);
		Recipe savedRecipe = recipeRepository.save(recipe);

		return RecipeRes.from(savedRecipe);
	}

	@Transactional(readOnly = true)
	public RecipeRes findById(Long id) {
		Recipe recipe = getRecipeById(id);

		return RecipeRes.from(recipe);
	}

	@Transactional(readOnly = true)
	public RecipesRes findAllByWriterId(String username) {
		Member member = getMember(username);

		List<Recipe> recipes = recipeRepository.findAllByWriterId(member.getId());

		return RecipesRes.from(recipes);
	}

	@Transactional(readOnly = true)
	public RecipesRes findAllByOwnerId(String username) {
		Member member = getMember(username);

		List<Recipe> recipes = recipeRepository.findAllByOwnerId(member.getId());

		return RecipesRes.from(recipes);
	}

	@Transactional(readOnly = true)
	public RecipesRes findAllByMemberId(String username) {
		Member member = getMember(username);

		List<Recipe> recipes = recipeRepository.findAllByMemberId(member.getId());

		return RecipesRes.from(recipes);
	}

	public RecipeRes update(Long id, RecipeReq recipeReq, String username) {
		Member writer = getMember(username);

		Recipe recipe = getRecipeByIdAndWriterId(id, writer.getId());

		Recipe updateRecipe = RecipeReq.toRecipe(recipeReq, writer);
		recipe.update(updateRecipe);

		return RecipeRes.from(recipe);
	}

	public void delete(Long id, String username) {
		Member writer = getMember(username);
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

	private Recipe getRecipeByIdAndWriterId(Long recipeId, Long writerId) {
		return recipeRepository.findByIdAndWriterId(recipeId, writerId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_RECIPE_BY_ID_AND_WRITER_ID : recipeId = {}, writerId = {}", recipeId, writerId);
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

	private Member getMember(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORE_BY_MEMBER_USERNAME : {}", username);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}
}
