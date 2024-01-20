package com.icebox.freshmate.domain.grocery.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.icebox.freshmate.domain.grocery.application.dto.request.GroceryReq;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceriesRes;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceryRes;
import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryImage;
import com.icebox.freshmate.domain.grocery.domain.GroceryImageRepository;
import com.icebox.freshmate.domain.grocery.domain.GroceryRepository;
import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.image.application.ImageService;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.image.application.dto.response.ImagesRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageRepository;
import com.icebox.freshmate.domain.storage.domain.StorageType;

@ExtendWith(MockitoExtension.class)
class GroceryServiceTest {

	@InjectMocks
	private GroceryService groceryService;

	@Mock
	private StorageRepository storageRepository;

	@Mock
	private GroceryRepository groceryRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private GroceryImageRepository groceryImageRepository;

	@Mock
	private ImageService imageService;

	private Member member;
	private Refrigerator refrigerator;
	private Storage storage;
	private Grocery grocery;
	private GroceryImage groceryImage;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.realName("성이름")
			.username("aaaa1111")
			.password("aaaa1111!")
			.nickName("닉네임닉네임")
			.role(Role.USER)
			.build();

		refrigerator = Refrigerator.builder()
			.name("우리 집 냉장고")
			.member(member)
			.build();

		storage = Storage.builder()
			.name("냉장실")
			.storageType(StorageType.FRIDGE)
			.refrigerator(refrigerator)
			.build();

		grocery = Grocery.builder()
			.storage(storage)
			.name("양배추")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().plusDays(7))
			.build();

		String imageFileName = "image.jpg";
		String imagePath = "https://fake-image-url.com/image.jpg";

		groceryImage = GroceryImage
			.builder()
			.grocery(grocery)
			.fileName(imageFileName)
			.path(imagePath)
			.build();

		grocery.addGroceryImage(groceryImage);
	}

	@DisplayName("식료품 생성 테스트")
	@Test
	void create() {
		//given
		GroceryReq groceryReq = new GroceryReq(grocery.getName(), grocery.getGroceryType().name(), grocery.getQuantity(), grocery.getDescription(), grocery.getExpirationDate(), grocery.getStorage().getId());

		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "Spring Framework".getBytes());
		ImageUploadReq imageUploadReq = new ImageUploadReq(List.of(file));

		ImageRes imageRes = new ImageRes(groceryImage.getFileName(), groceryImage.getPath());
		ImagesRes imagesRes = new ImagesRes(List.of(imageRes));

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(storageRepository.findByIdAndMemberId(any(), any())).thenReturn(Optional.of(storage));
		when(groceryRepository.save(any(Grocery.class))).thenReturn(grocery);
		when(groceryImageRepository.save(any(GroceryImage.class))).thenReturn(groceryImage);
		when(imageService.store(any(ImageUploadReq.class))).thenReturn(imagesRes);

		//when
		GroceryRes groceryRes = groceryService.create(groceryReq, imageUploadReq, member.getUsername());

		//then
		assertThat(groceryRes.groceryName()).isEqualTo(grocery.getName());
		assertThat(groceryRes.groceryType()).isEqualTo(grocery.getGroceryType().name());
		assertThat(groceryRes.quantity()).isEqualTo(grocery.getQuantity());
		assertThat(groceryRes.description()).isEqualTo(grocery.getDescription());
		assertThat(groceryRes.expirationDate()).isEqualTo(grocery.getExpirationDate());
		assertThat(groceryRes.storageName()).isEqualTo(grocery.getStorage().getName());
		assertThat(groceryRes.images().get(0).fileName()).isEqualTo(groceryImage.getFileName());
		assertThat(groceryRes.images().get(0).path()).isEqualTo(groceryImage.getPath());
	}

	@DisplayName("식료품 단건 조회 테스트")
	@Test
	void findById() {
		//given
		Long groceryId = 1L;

		when(groceryRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(grocery));

		//when
		GroceryRes groceryRes = groceryService.findById(groceryId);

		//then
		assertThat(groceryRes.groceryName()).isEqualTo(grocery.getName());
		assertThat(groceryRes.groceryType()).isEqualTo(grocery.getGroceryType().name());
		assertThat(groceryRes.quantity()).isEqualTo(grocery.getQuantity());
		assertThat(groceryRes.description()).isEqualTo(grocery.getDescription());
		assertThat(groceryRes.expirationDate()).isEqualTo(grocery.getExpirationDate());
		assertThat(groceryRes.storageName()).isEqualTo(grocery.getStorage().getName());
	}

	@DisplayName("특정 냉장고 저장소의 모든 식료품 조회 테스트")
	@Test
	void findAllByStorageId() {
		//given
		Long storageId = 1L;

		Grocery grocery2 = Grocery.builder()
			.storage(storage)
			.name("햄")
			.groceryType(GroceryType.MEAT)
			.quantity("3개")
			.description("김밥 재료")
			.expirationDate(LocalDate.now().plusDays(14))
			.build();

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(groceryRepository.findAllByStorageIdAndMemberId(any(), any())).thenReturn(List.of(grocery, grocery2));

		//when
		GroceriesRes groceriesRes = groceryService.findAllByStorageId(storageId, member.getUsername());

		//then
		assertThat(groceriesRes.groceries()).hasSize(2);
		assertThat(groceriesRes.groceries().get(0).groceryName()).isEqualTo(grocery.getName());
		assertThat(groceriesRes.groceries().get(0).groceryType()).isEqualTo(grocery.getGroceryType().name());
		assertThat(groceriesRes.groceries().get(0).quantity()).isEqualTo(grocery.getQuantity());
		assertThat(groceriesRes.groceries().get(0).description()).isEqualTo(grocery.getDescription());
		assertThat(groceriesRes.groceries().get(0).expirationDate()).isEqualTo(grocery.getExpirationDate());
		assertThat(groceriesRes.groceries().get(0).storageName()).isEqualTo(grocery.getStorage().getName());
	}

	@DisplayName("식료품 수정 테스트")
	@Test
	void update() {
		//given
		Long groceryId = 1L;
		Long storageId = 1L;

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(storageRepository.findByIdAndMemberId(any(), any())).thenReturn(Optional.of(storage));
		when(groceryRepository.findByIdAndMemberId(any(), any())).thenReturn(Optional.of(grocery));

		GroceryReq groceryReq = new GroceryReq("식료품 수정", GroceryType.SNACKS.name(), "10개", "수정", LocalDate.now().plusDays(2), storageId);

		//when
		GroceryRes groceryRes = groceryService.update(groceryId, groceryReq, member.getUsername());

		//then
		assertThat(groceryRes.groceryName()).isEqualTo(grocery.getName());
		assertThat(groceryRes.groceryType()).isEqualTo(grocery.getGroceryType().name());
		assertThat(groceryRes.quantity()).isEqualTo(grocery.getQuantity());
		assertThat(groceryRes.description()).isEqualTo(grocery.getDescription());
		assertThat(groceryRes.expirationDate()).isEqualTo(grocery.getExpirationDate());
		assertThat(groceryRes.storageName()).isEqualTo(grocery.getStorage().getName());
	}
}
