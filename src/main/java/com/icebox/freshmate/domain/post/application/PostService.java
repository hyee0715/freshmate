package com.icebox.freshmate.domain.post.application;

import static com.icebox.freshmate.global.error.ErrorCode.INVALID_ATTEMPT_TO_POST_RECIPE;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_POST;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.image.application.ImageService;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.image.application.dto.response.ImagesRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.post.application.dto.request.PostReq;
import com.icebox.freshmate.domain.post.application.dto.response.PostRes;
import com.icebox.freshmate.domain.post.application.dto.response.PostsRes;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.post.domain.PostImage;
import com.icebox.freshmate.domain.post.domain.PostImageRepository;
import com.icebox.freshmate.domain.post.domain.PostRepository;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeRepository;
import com.icebox.freshmate.domain.recipe.domain.RecipeType;
import com.icebox.freshmate.domain.recipegrocery.application.dto.response.RecipeGroceryRes;
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
	private final PostImageRepository postImageRepository;
	private final ImageService imageService;

	public PostRes create(PostReq postReq, ImageUploadReq imageUploadReq, String username) {
		Member member = getMemberByUsername(username);
		Recipe recipe = getNullableRecipe(postReq.recipeId());

		validateScrapedRecipe(recipe, member);

		Post post = savePost(postReq, member, recipe);

		List<RecipeGroceryRes> recipeGroceriesRes = getRecipeGroceryResList(recipe);

		ImagesRes imagesRes = saveImages(post, imageUploadReq);
		List<ImageRes> images = getImagesRes(imagesRes);

		return PostRes.of(post, recipeGroceriesRes, images);
	}

	@Transactional(readOnly = true)
	public PostRes findById(Long id) {
		Post post = getPostById(id);
		Recipe recipe = post.getRecipe();

		List<RecipeGroceryRes> recipeGroceriesRes = getRecipeGroceryResList(recipe);
		List<ImageRes> imagesRes = getPostImagesRes(post);

		return PostRes.of(post, recipeGroceriesRes, imagesRes);
	}

	private List<ImageRes> getPostImagesRes(Post post) {
		List<PostImage> postImages = post.getPostImages();

		return getImagesRes(postImages);
	}

	@Transactional(readOnly = true)
	public PostsRes findAllByMemberId(Long memberId) {
		Member member = getMemberById(memberId);
		List<Post> posts = postRepository.findAllByMemberId(member.getId());

		return PostsRes.from(posts);
	}

//	public PostRes update(Long postId, PostReq postReq, String username) {
//		Member member = getMemberByUsername(username);
//		Post post = getPostByIdAndMemberId(postId, member.getId());
//		Recipe recipe = getNullableRecipe(postReq.recipeId());
//
//		validateScrapedRecipe(recipe, member);
//
//		Post updatePost = PostReq.toPost(postReq, member, recipe);
//		post.update(updatePost);
//
//		List<RecipeGroceryRes> recipeGroceriesRes = getRecipeGroceryResList(recipe);
//
//		return PostRes.of(post, recipeGroceriesRes);
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

	private Post getPostById(Long postId) {
		return postRepository.findById(postId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_POST_BY_ID : {}", postId);
				return new EntityNotFoundException(NOT_FOUND_POST);
			});
	}

	private Member getMemberByUsername(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}

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

	private List<ImageRes> getImagesRes(ImagesRes imagesRes) {

		return Optional.ofNullable(imagesRes)
			.map(ImagesRes::images)
			.orElse(null);
	}

	private List<ImageRes> getImagesRes(List<PostImage> postImages) {

		return postImages.stream()
			.map(postImage -> ImageRes.of(postImage.getFileName(), postImage.getPath()))
			.toList();
	}

	private Post savePost(PostReq postReq, Member member, Recipe recipe) {
		Post post = PostReq.toPost(postReq, member, recipe);
		return postRepository.save(post);
	}

	private ImagesRes saveImages(Post post, ImageUploadReq imageUploadReq) {

		return Optional.ofNullable(imageUploadReq.files())
			.map(files -> imageService.store(imageUploadReq))
			.map(imagesRes -> {
				List<PostImage> postImages = saveImages(post, imagesRes);
				post.addPostImages(postImages);

				return imagesRes;
			})
			.orElse(null);
	}

	private List<PostImage> saveImages(Post post, ImagesRes imagesRes) {

		return imagesRes.images().stream()
			.map(imageRes -> buildPostImage(imageRes, post))
			.peek(postImageRepository::save)
			.toList();
	}

	private PostImage buildPostImage(ImageRes imageRes, Post post) {

		return PostImage.builder()
			.fileName(imageRes.fileName())
			.path(imageRes.path())
			.post(post)
			.build();
	}
}
