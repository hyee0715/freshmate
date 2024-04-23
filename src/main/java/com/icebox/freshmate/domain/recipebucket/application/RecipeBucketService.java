package com.icebox.freshmate.domain.recipebucket.application;

import static com.icebox.freshmate.global.error.ErrorCode.DUPLICATED_RECIPE_BUCKET;
import static com.icebox.freshmate.global.error.ErrorCode.INVALID_LAST_PAGE_CREATED_AT_FORMAT;
import static com.icebox.freshmate.global.error.ErrorCode.INVALID_RECIPE_BUCKET_SORT_TYPE;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_RECIPE;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_RECIPE_BUCKET;
import static com.icebox.freshmate.global.error.ErrorCode.RECIPE_OWNER_MISMATCH_TO_CREATE_RECIPE_BUCKET;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeRepository;
import com.icebox.freshmate.domain.recipebucket.application.dto.request.RecipeBucketReq;
import com.icebox.freshmate.domain.recipebucket.application.dto.response.RecipeBucketRes;
import com.icebox.freshmate.domain.recipebucket.application.dto.response.RecipeBucketsRes;
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

	@Transactional(readOnly = true)
	public RecipeBucketRes findById(Long id) {
		RecipeBucket recipeBucket = getRecipeBucketById(id);

		List<RecipeGrocery> recipeGroceries = recipeBucket.getRecipe().getRecipeGroceries();
		List<RecipeGroceryRes> recipeGroceriesRes = RecipeGroceryRes.from(recipeGroceries);

		return RecipeBucketRes.of(recipeBucket, recipeGroceriesRes);
	}

	@Transactional(readOnly = true)
	public RecipeBucketsRes findAllByMemberId(String sortBy, Pageable pageable, String lastPageTitle, String lastPageCreatedAt, String username) {
		Member member = getMemberByUsername(username);

		LocalDateTime lastCreatedAt = getLastPageCreatedAt(lastPageCreatedAt);
		validateRecipeBucketSortType(sortBy);
		Slice<RecipeBucket> recipeBuckets = recipeBucketRepository.findAllByMemberId(member.getId(), pageable, sortBy, lastPageTitle, lastCreatedAt);

		return RecipeBucketsRes.from(recipeBuckets);
	}

	public void delete(Long id, String username) {
		Member member = getMemberByUsername(username);

		RecipeBucket recipeBucket = getRecipeBucketByIdAndMemberId(id, member.getId());

		recipeBucketRepository.delete(recipeBucket);
	}

	private RecipeBucket getRecipeBucketByIdAndMemberId(Long recipeBucketId, Long memberId) {

		return recipeBucketRepository.findByIdAndMemberId(recipeBucketId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_RECIPE_BUCKET_BY_ID_AND_MEMBER_ID : recipeBucketId = {}, memberId = {}", recipeBucketId, memberId);

				return new EntityNotFoundException(NOT_FOUND_RECIPE_BUCKET);
			});
	}

	private RecipeBucket getRecipeBucketById(Long recipeBucketId) {

		return recipeBucketRepository.findById(recipeBucketId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_RECIPE_BUCKET_BY_ID : {}", recipeBucketId);

				return new EntityNotFoundException(NOT_FOUND_RECIPE_BUCKET);
			});
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

	private void validateRecipeBucketSortType(String sortBy) {
		if (!sortBy.equalsIgnoreCase("titleAsc") && !sortBy.equalsIgnoreCase("titleDesc") && !sortBy.equalsIgnoreCase("createdAtAsc") && !sortBy.equalsIgnoreCase("createdAtDesc")) {
			log.warn("GET:READ:INVALID_RECIPE_BUCKET_SORT_TYPE : {}", sortBy);

			throw new BusinessException(INVALID_RECIPE_BUCKET_SORT_TYPE);
		}
	}

	private LocalDateTime getLastPageCreatedAt(String lastPageCreatedAt) {
		return Optional.ofNullable(lastPageCreatedAt)
			.map(date -> {
				if (checkLocalDateTimeFormat(date)) {
					date += "0";
				}
				return date;
			})
			.map(date -> {
				validateLastPageCreatedAtFormat(date);
				return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
			})
			.orElse(null);
	}

	private boolean checkLocalDateTimeFormat(String date) {
		String pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{5}";

		return Pattern.matches(pattern, date);
	}

	private void validateLastPageCreatedAtFormat(String lastPageCreatedAt) {
		String pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}";

		if (!Pattern.matches(pattern, lastPageCreatedAt)) {
			log.warn("GET:READ:INVALID_LAST_PAGE_CREATED_AT_FORMAT : {}", lastPageCreatedAt);

			throw new BusinessException(INVALID_LAST_PAGE_CREATED_AT_FORMAT);
		}
	}
}
