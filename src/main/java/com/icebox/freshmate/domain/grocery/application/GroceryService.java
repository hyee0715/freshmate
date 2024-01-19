package com.icebox.freshmate.domain.grocery.application;

import static com.icebox.freshmate.global.error.ErrorCode.EMPTY_IMAGE;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_GROCERY;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_STORAGE;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.icebox.freshmate.domain.grocery.application.dto.request.GroceryReq;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceriesRes;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceryRes;
import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryImage;
import com.icebox.freshmate.domain.grocery.domain.GroceryImageRepository;
import com.icebox.freshmate.domain.grocery.domain.GroceryRepository;
import com.icebox.freshmate.domain.image.application.ImageService;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.image.application.dto.response.ImagesRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageRepository;
import com.icebox.freshmate.global.error.exception.BusinessException;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GroceryService {

	private final GroceryRepository groceryRepository;
	private final MemberRepository memberRepository;
	private final StorageRepository storageRepository;
	private final GroceryImageRepository groceryImageRepository;
	private final ImageService imageService;

	public GroceryRes create(GroceryReq groceryReq, ImageUploadReq imageUploadReq, String username) {
		Member member = getMemberByUsername(username);
		Storage storage = getStorageByIdAndMemberId(groceryReq.storageId(), member.getId());

		Grocery grocery = saveGrocery(groceryReq, storage);

		ImagesRes imagesRes = saveImages(grocery, imageUploadReq);
		List<ImageRes> images = getImagesRes(imagesRes);

		return GroceryRes.of(grocery, images);
	}

	@Transactional(readOnly = true)
	public GroceryRes findById(Long id) {
		Grocery grocery = getGroceryById(id);

		List<ImageRes> imagesRes = getGroceryImagesRes(grocery);

		return GroceryRes.of(grocery, imagesRes);
	}

	private List<ImageRes> getGroceryImagesRes(Grocery grocery) {
		List<GroceryImage> groceryImages = grocery.getGroceryImages();

		return getImagesRes(groceryImages);
	}

	@Transactional(readOnly = true)
	public GroceriesRes findAllByStorageId(Long storageId, String username) {
		Member member = getMemberByUsername(username);

		List<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberId(storageId, member.getId());

		return GroceriesRes.from(groceries);
	}

	public GroceryRes update(Long id, GroceryReq groceryReq, String username) {
		Member member = getMemberByUsername(username);
		Storage storage = getStorageByIdAndMemberId(groceryReq.storageId(), member.getId());
		Grocery grocery = getGroceryByIdAndMemberId(id, member.getId());

		Grocery updateGrocery = GroceryReq.toGrocery(groceryReq, storage);
		grocery.update(updateGrocery);

		List<ImageRes> imagesRes = getGroceryImagesRes(grocery);

		return GroceryRes.of(grocery, imagesRes);
	}

	public GroceryRes addGroceryImage(Long groceryId, ImageUploadReq imageUploadReq, String username) {
		Member member = getMemberByUsername(username);
		Grocery grocery = getGroceryByIdAndMemberId(groceryId, member.getId());

		validateImageListIsEmpty(imageUploadReq.files());
		saveImages(grocery, imageUploadReq);

		List<ImageRes> images = getImagesRes(grocery.getGroceryImages());

		return GroceryRes.of(grocery, images);
	}

	public void delete(Long id, String username) {
		Member member = getMemberByUsername(username);
		Grocery grocery = getGroceryByIdAndMemberId(id, member.getId());

		for (RecipeGrocery recipeGrocery : grocery.getRecipeGroceries()) {
			recipeGrocery.removeGrocery();
		}

		grocery.getRecipeGroceries().clear();
		groceryRepository.delete(grocery);
	}

	private Grocery getGroceryByIdAndMemberId(Long groceryId, Long memberId) {
		return groceryRepository.findByIdAndMemberId(groceryId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORAGE_BY_ID_AND_MEMBER_ID : groceryId = {}, memberId = {}", groceryId, memberId);
				return new EntityNotFoundException(NOT_FOUND_GROCERY);
			});
	}

	private Grocery getGroceryById(Long id) {
		return groceryRepository.findById(id)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORAGE_BY_ID : {}", id);
				return new EntityNotFoundException(NOT_FOUND_GROCERY);
			});
	}

	private Storage getStorageByIdAndMemberId(Long storageId, Long memberId) {
		return storageRepository.findByIdAndMemberId(storageId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORAGE_BY_ID_AND_MEMBER_ID : storageId = {}, memberId = {}", storageId, memberId);
				return new EntityNotFoundException(NOT_FOUND_STORAGE);
			});
	}

	private Member getMemberByUsername(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORE_BY_MEMBER_USERNAME : {}", username);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}

	private Grocery saveGrocery(GroceryReq groceryReq, Storage storage) {
		Grocery grocery = GroceryReq.toGrocery(groceryReq, storage);

		return groceryRepository.save(grocery);
	}

	private ImagesRes saveImages(Grocery grocery, ImageUploadReq imageUploadReq) {

		return Optional.ofNullable(imageUploadReq.files())
			.map(files -> imageService.store(imageUploadReq))
			.map(imagesRes -> {
				List<GroceryImage> groceryImages = saveImages(grocery, imagesRes);
				grocery.addGroceryImages(groceryImages);

				return imagesRes;
			})
			.orElse(null);
	}

	private List<GroceryImage> saveImages(Grocery grocery, ImagesRes imagesRes) {

		return imagesRes.images().stream()
			.map(imageRes -> buildGroceryImage(imageRes, grocery))
			.peek(groceryImageRepository::save)
			.toList();
	}

	private GroceryImage buildGroceryImage(ImageRes imageRes, Grocery grocery) {

		return GroceryImage.builder()
			.fileName(imageRes.fileName())
			.path(imageRes.path())
			.grocery(grocery)
			.build();
	}

	private List<ImageRes> getImagesRes(ImagesRes imagesRes) {

		return Optional.ofNullable(imagesRes)
			.map(ImagesRes::images)
			.orElse(null);
	}

	private List<ImageRes> getImagesRes(List<GroceryImage> groceryImages) {

		return groceryImages.stream()
			.map(groceryImage -> ImageRes.of(groceryImage.getFileName(), groceryImage.getPath()))
			.toList();
	}

	private void validateImageListIsEmpty(List<MultipartFile> images) {
		if (images.size() == 1 && Objects.equals(images.get(0).getOriginalFilename(), "")) {
			log.warn("PATCH:WRITE:EMPTY_IMAGE");

			throw new BusinessException(EMPTY_IMAGE);
		}
	}
}
