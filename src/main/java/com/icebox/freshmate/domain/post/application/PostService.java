package com.icebox.freshmate.domain.post.application;

import static com.icebox.freshmate.global.error.ErrorCode.INVALID_ATTEMPT_TO_POST_RECIPE;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_POST;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.post.application.dto.request.PostReq;
import com.icebox.freshmate.domain.post.application.dto.response.PostRes;
import com.icebox.freshmate.domain.post.application.dto.response.PostsRes;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.post.domain.PostRepository;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeRepository;
import com.icebox.freshmate.domain.recipe.domain.RecipeType;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGroceryRepository;
import com.icebox.freshmate.global.error.exception.BusinessException;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final MemberRepository memberRepository;
	private final PostRepository postRepository;
	private final RecipeRepository recipeRepository;
	private final RecipeGroceryRepository recipeGroceryRepository;

	public PostRes create(PostReq postReq, String username) {
		Member member = getMemberByUsername(username);
		Recipe recipe = getNullableRecipe(postReq.recipeId());

		validateScrapedRecipe(recipe, member);

		Post post = PostReq.toPost(postReq, member, recipe);
		Post savedPost = postRepository.save(post);

		List<RecipeGroceryRes> recipeGroceriesRes = getRecipeGroceryResList(recipe);

		return PostRes.of(savedPost, recipeGroceriesRes);
	}

	private List<RecipeGroceryRes> getRecipeGroceryResList(Recipe recipe) {

		return Optional.ofNullable(recipe)
			.map(existingRecipe -> recipeGroceryRepository.findAllByRecipeId(existingRecipe.getId()))
			.map(RecipeGroceryRes::from)
			.orElse(null);
	}

	private Recipe getNullableRecipe(Long recipeId) {

		return Optional.ofNullable(recipeId)
			.flatMap(recipeRepository::findById)
			.orElse(null);
	}

//	@Transactional(readOnly = true)
//	public PostRes findById(Long id) {
//		Post post = getPostById(id);
//
//		return PostRes.of(post);
//	}

	@Transactional(readOnly = true)
	public PostsRes findAllByMemberId(Long memberId) {
		Member member = getMemberById(memberId);
		List<Post> posts = postRepository.findAllByMemberId(member.getId());

		return PostsRes.from(posts);
	}

//	public PostRes update(Long postId, PostReq postReq, String username) {
//		Member member = getMemberByUsername(username);
//		Post post = getPostByIdAndMemberId(postId, member.getId());
//		Recipe recipe = recipeRepository.findById(postReq.recipeId())
//			.orElse(null);
//
//		Post updatePost = PostReq.toPost(postReq, member, recipe);
//		post.update(updatePost);
//
//		return PostRes.of(post);
//	}

	public void delete(Long postId, String username) {
		Member member = getMemberByUsername(username);
		Post post = getPostByIdAndMemberId(postId, member.getId());

		postRepository.delete(post);
	}

	private Member getMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_ID : {}", memberId);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}

	private Post getPostByIdAndMemberId(Long postId, Long memberId) {
		return postRepository.findByIdAndMemberId(postId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_POST_BY_ID_AND_MEMBER_ID : postId = {}, memberId = {}", postId, memberId);
				return new EntityNotFoundException(NOT_FOUND_POST);
			});
	}

//	private Post getPostById(Long postId) {
//		return postRepository.findById(postId)
//			.orElseThrow(() -> {
//				log.warn("GET:READ:NOT_FOUND_POST_BY_ID : {}", postId);
//				return new EntityNotFoundException(NOT_FOUND_POST);
//			});
//	}

	private Member getMemberByUsername(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}

//	private Recipe getRecipeById(Long recipeId) {
//		return recipeRepository.findById(recipeId)
//			.orElseThrow(() -> {
//				log.warn("GET:READ:NOT_FOUND_RECIPE_BY_ID : {}", recipeId);
//				return new EntityNotFoundException(NOT_FOUND_RECIPE);
//			});
//	}

	private void validateScrapedRecipe(Recipe recipe, Member member) {
		Optional.ofNullable(recipe)
			.ifPresent(existingRecipe -> {
				Long recipeWriterId = recipe.getWriter().getId();
				Long memberId = member.getId();

				if (!Objects.equals(recipeWriterId, memberId) || recipe.getRecipeType() == RecipeType.SCRAPED) {
					log.warn("INVALID_ATTEMPT_TO_POST_RECIPE : recipeId = {}, recipeWriterId = {}, memberId = {}", recipe.getId(), recipeWriterId, memberId);
					throw new BusinessException(INVALID_ATTEMPT_TO_POST_RECIPE);
				}
			});
	}
}
