package com.icebox.freshmate.domain.recipebucket.application;

import static com.icebox.freshmate.global.error.ErrorCode.DUPLICATED_RECIPE_BUCKET;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_RECIPE;
import static com.icebox.freshmate.global.error.ErrorCode.RECIPE_OWNER_MISMATCH_TO_CREATE_RECIPE_BUCKET;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeRepository;
import com.icebox.freshmate.domain.recipebucket.application.dto.request.RecipeBucketReq;
import com.icebox.freshmate.domain.recipebucket.application.dto.response.RecipeBucketRes;
import com.icebox.freshmate.domain.recipebucket.domain.RecipeBucket;
import com.icebox.freshmate.domain.recipebucket.domain.RecipeBucketRepository;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;
import com.icebox.freshmate.global.error.exception.BusinessException;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecipeBucketService {

	private final MemberRepository memberRepository;
	private final RecipeRepository recipeRepository;
	private final RecipeBucketRepository recipeBucketRepository;

	public RecipeBucketRes create(RecipeBucketReq recipeBucketReq, String username) {
		Member member = getMemberByUsername(username);
		Recipe recipe = getRecipeById(recipeBucketReq.recipeId());

		validateDuplicatedRecipeBucket(recipe);
		validateRecipeOwner(recipe, member);

		RecipeBucket recipeBucket = toRecipeBucket(recipe, member);
		RecipeBucket savedRecipeBucket = recipeBucketRepository.save(recipeBucket);

		List<RecipeGrocery> recipeGroceries = recipe.getRecipeGroceries();
		List<RecipeGroceryRes> recipeGroceriesRes = RecipeGroceryRes.from(recipeGroceries);

		return RecipeBucketRes.of(savedRecipeBucket, recipeGroceriesRes);
	}

	private Member getMemberByUsername(String username) {

		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}

	private Recipe getRecipeById(Long recipeId) {

		return recipeRepository.findById(recipeId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_RECIPE_BY_ID : {}", recipeId);
				return new EntityNotFoundException(NOT_FOUND_RECIPE);
			});
	}

	private RecipeBucket toRecipeBucket(Recipe recipe, Member member) {

		return RecipeBucket.builder()
			.recipe(recipe)
			.member(member)
			.build();
	}

	private void validateRecipeOwner(Recipe recipe, Member member) {
		if (!Objects.equals(recipe.getOwner().getId(), member.getId())) {
			log.warn("RECIPE_OWNER_MISMATCH_TO_CREATE_RECIPE_BUCKET : recipeId = {}, recipeOwnerId = {}, memberId = {}", recipe.getId(), recipe.getOwner().getId(), member.getId());
			throw new BusinessException(RECIPE_OWNER_MISMATCH_TO_CREATE_RECIPE_BUCKET);
		}
	}

	private void validateDuplicatedRecipeBucket(Recipe recipe) {
		if (recipeBucketRepository.existsByRecipeId(recipe.getId())) {
			log.warn("DUPLICATED_RECIPE_BUCKET : recipeId = {}, recipeOwnerId = {}", recipe.getId(), recipe.getOwner().getId());
			throw new BusinessException(DUPLICATED_RECIPE_BUCKET);
		}
	}
}
