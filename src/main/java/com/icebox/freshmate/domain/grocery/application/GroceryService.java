package com.icebox.freshmate.domain.grocery.application;

import static com.icebox.freshmate.global.error.ErrorCode.EMPTY_IMAGE;
import static com.icebox.freshmate.global.error.ErrorCode.EXCESSIVE_DELETE_IMAGE_COUNT;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_GROCERY;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_IMAGE;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_STORAGE;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.icebox.freshmate.domain.grocery.application.dto.request.GroceryReq;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceriesRes;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceryRes;
import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryExpirationType;
import com.icebox.freshmate.domain.grocery.domain.GroceryImage;
import com.icebox.freshmate.domain.grocery.domain.GroceryImageRepository;
import com.icebox.freshmate.domain.grocery.domain.GroceryRepository;
import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.image.application.ImageService;
import com.icebox.freshmate.domain.image.application.dto.request.ImageDeleteReq;
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

	public GroceryRes addGroceryImage(Long groceryId, ImageUploadReq imageUploadReq, String username) {
		Member member = getMemberByUsername(username);
		Grocery grocery = getGroceryByIdAndMemberId(groceryId, member.getId());

		validateImageListIsEmpty(imageUploadReq.files());
		saveImages(grocery, imageUploadReq);

		List<ImageRes> images = getImagesRes(grocery.getGroceryImages());

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
	public GroceriesRes findAllByStorageId(Long storageId, String sortBy, String groceryType, String groceryExpirationType, Pageable pageable, String username) {
		Member member = getMemberByUsername(username);
		Storage storage = getStorageByIdAndMemberId(storageId, member.getId());
		GroceryType type = findGroceryType(groceryType);
		GroceryExpirationType expirationType = findGroceryExpirationType(groceryExpirationType);

		Slice<Grocery> groceries = getGroceries(storage, member, pageable, sortBy, type, expirationType);

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

	public void delete(Long id, String username) {
		Member member = getMemberByUsername(username);
		Grocery grocery = getGroceryByIdAndMemberId(id, member.getId());

		for (RecipeGrocery recipeGrocery : grocery.getRecipeGroceries()) {
			recipeGrocery.removeGrocery();
		}

		grocery.getRecipeGroceries().clear();
		groceryRepository.delete(grocery);
	}

	public GroceryRes removeGroceryImage(Long groceryId, ImageDeleteReq imageDeleteReq, String username) {
		Member member = getMemberByUsername(username);
		Grocery grocery = getGroceryByIdAndMemberId(groceryId, member.getId());
		validateDeleteImageCount(imageDeleteReq.filePaths());

		String imagePath = imageDeleteReq.filePaths().get(0);
		GroceryImage groceryImage = getGroceryImageByGroceryIdAndPath(grocery.getId(), imagePath);
		deleteGroceryImage(groceryImage, imageDeleteReq);

		List<ImageRes> imagesRes = getGroceryImagesRes(grocery);

		return GroceryRes.of(grocery, imagesRes);
	}

	private Grocery getGroceryByIdAndMemberId(Long groceryId, Long memberId) {

		return groceryRepository.findByIdAndMemberId(groceryId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_GROCERY_BY_ID_AND_MEMBER_ID : groceryId = {}, memberId = {}", groceryId, memberId);

				return new EntityNotFoundException(NOT_FOUND_GROCERY);
			});
	}

	private Grocery getGroceryById(Long id) {

		return groceryRepository.findById(id)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_GROCERY_BY_ID : {}", id);

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
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);

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

	private void validateDeleteImageCount(List<String> imagePaths) {
		if (imagePaths.size() != 1) {
			log.warn("DELETE:WRITE:EXCESSIVE_DELETE_IMAGE_COUNT : requested image path count = {}", imagePaths.size());

			throw new BusinessException(EXCESSIVE_DELETE_IMAGE_COUNT);
		}
	}

	private void deleteGroceryImage(GroceryImage groceryImage, ImageDeleteReq imageDeleteReq) {
		groceryImage.getGrocery().removeGroceryImage(groceryImage);
		groceryImageRepository.delete(groceryImage);
		imageService.delete(imageDeleteReq);
	}

	private GroceryImage getGroceryImageByGroceryIdAndPath(Long groceryId, String imagePath) {

		return groceryImageRepository.findByGroceryIdAndPath(groceryId, imagePath)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_GROCERY_IMAGE_BY_GROCERY_ID_AND_PATH : groceryId = {}, imagePath = {}", groceryId, imagePath);

				return new EntityNotFoundException(NOT_FOUND_IMAGE);
			});
	}

	private GroceryType findGroceryType(String groceryType) {
		try {

			return GroceryType.findGroceryType(groceryType);
		} catch (BusinessException e) {

			return null;
		}
	}

	private GroceryExpirationType findGroceryExpirationType(String groceryExpirationType) {
		try {

			return GroceryExpirationType.findGroceryExpirationType(groceryExpirationType);
		} catch (BusinessException e) {

			return null;
		}
	}

	private Slice<Grocery> getGroceries(Storage storage, Member member, Pageable pageable, String sortBy, GroceryType groceryType, GroceryExpirationType groceryExpirationType) {

		return Optional.ofNullable(groceryType)
			.map(type -> Optional.ofNullable(groceryExpirationType)
				.map(expirationType -> groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortCondition(storage.getId(), member.getId(), type, expirationType, pageable, sortBy))
				.orElseGet(() -> groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeOrderBySortCondition(storage.getId(), member.getId(), type, pageable, sortBy)))
			.orElseGet(() -> Optional.ofNullable(groceryExpirationType)
				.map(expirationType -> groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortCondition(storage.getId(), member.getId(), expirationType, pageable, sortBy))
				.orElseGet(() -> groceryRepository.findAllByStorageIdAndMemberIdOrderBySortCondition(storage.getId(), member.getId(), pageable, sortBy)));
	}
}
