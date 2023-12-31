package com.icebox.freshmate.domain.grocery.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.icebox.freshmate.domain.grocery.application.dto.request.GroceryReq;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceriesRes;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceryRes;
import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryRepository;
import com.icebox.freshmate.domain.grocery.domain.GroceryType;
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

	private Member member;
	private Refrigerator refrigerator;
	private Storage storage;
	private Grocery grocery;

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
			.quantity(1)
			.description("필수 식재료")
			.expirationDateTime(LocalDateTime.now().plusDays(7))
			.build();
	}

	@DisplayName("식료품 생성 테스트")
	@Test
	void create() {
		//given
		GroceryReq groceryReq = new GroceryReq(grocery.getName(), grocery.getGroceryType().name(), grocery.getQuantity(), grocery.getDescription(), grocery.getExpirationDateTime(), grocery.getStorage().getId());

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(storageRepository.findByIdAndMemberId(any(), any())).thenReturn(Optional.of(storage));
		when(groceryRepository.save(any(Grocery.class))).thenReturn(grocery);

		//when
		GroceryRes groceryRes = groceryService.create(groceryReq, member.getUsername());

		//then
		assertThat(groceryRes.groceryName()).isEqualTo(grocery.getName());
		assertThat(groceryRes.groceryType()).isEqualTo(grocery.getGroceryType().name());
		assertThat(groceryRes.quantity()).isEqualTo(grocery.getQuantity());
		assertThat(groceryRes.description()).isEqualTo(grocery.getDescription());
		assertThat(groceryRes.expirationDateTime()).isEqualTo(grocery.getExpirationDateTime());
		assertThat(groceryRes.storageName()).isEqualTo(grocery.getStorage().getName());
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
		assertThat(groceryRes.expirationDateTime()).isEqualTo(grocery.getExpirationDateTime());
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
			.quantity(3)
			.description("김밥 재료")
			.expirationDateTime(LocalDateTime.now().plusDays(14))
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
		assertThat(groceriesRes.groceries().get(0).expirationDateTime()).isEqualTo(grocery.getExpirationDateTime());
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

		GroceryReq groceryReq = new GroceryReq("식료품 수정", GroceryType.SNACKS.name(), 2, "수정", LocalDateTime.now().plusDays(2), storageId);

		//when
		GroceryRes groceryRes = groceryService.update(groceryId, groceryReq, member.getUsername());

		//then
		assertThat(groceryRes.groceryName()).isEqualTo(grocery.getName());
		assertThat(groceryRes.groceryType()).isEqualTo(grocery.getGroceryType().name());
		assertThat(groceryRes.quantity()).isEqualTo(grocery.getQuantity());
		assertThat(groceryRes.description()).isEqualTo(grocery.getDescription());
		assertThat(groceryRes.expirationDateTime()).isEqualTo(grocery.getExpirationDateTime());
		assertThat(groceryRes.storageName()).isEqualTo(grocery.getStorage().getName());
	}
}
