package com.icebox.freshmate.domain.grocerybucket.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.grocerybucket.application.dto.request.GroceryBucketReq;
import com.icebox.freshmate.domain.grocerybucket.application.dto.response.GroceryBucketRes;
import com.icebox.freshmate.domain.grocerybucket.application.dto.response.GroceryBucketsRes;
import com.icebox.freshmate.domain.grocerybucket.domain.GroceryBucket;
import com.icebox.freshmate.domain.grocerybucket.domain.GroceryBucketRepository;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.storage.domain.Storage;

@ExtendWith(MockitoExtension.class)
class GroceryBucketServiceTest {

	@InjectMocks
	private GroceryBucketService groceryBucketService;

	@Mock
	private GroceryBucketRepository groceryBucketRepository;

	@Mock
	private MemberRepository memberRepository;

	private Member member;
	private GroceryBucket groceryBucket;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.realName("성이름")
			.username("aaaa1111")
			.password("aaaa1111!")
			.nickName("닉네임닉네임")
			.role(Role.USER)
			.build();

		groceryBucket = GroceryBucket.builder()
			.member(member)
			.groceryName("양배추")
			.groceryType(GroceryType.VEGETABLES)
			.groceryDescription("필수 식재료")
			.build();
	}

	@DisplayName("즐겨 찾는 식료품 생성 테스트")
	@Test
	void create() {
		//given
		GroceryBucketReq groceryBucketReq = new GroceryBucketReq(groceryBucket.getGroceryName(), groceryBucket.getGroceryType().name(), groceryBucket.getGroceryDescription());

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(groceryBucketRepository.save(any(GroceryBucket.class))).thenReturn(groceryBucket);

		//when
		GroceryBucketRes groceryBucketRes = groceryBucketService.create(groceryBucketReq, member.getUsername());

		//then
		assertThat(groceryBucketRes.groceryName()).isEqualTo(groceryBucket.getGroceryName());
		assertThat(groceryBucketRes.groceryType()).isEqualTo(groceryBucket.getGroceryType().name());
		assertThat(groceryBucketRes.groceryDescription()).isEqualTo(groceryBucket.getGroceryDescription());
	}

	@DisplayName("즐겨 찾는 식료품 단건 조회 테스트")
	@Test
	void findById() {
		//given
		Long groceryBucketId = 1L;

		when(groceryBucketRepository.findById(any(Long.class))).thenReturn(Optional.of(groceryBucket));

		//when
		GroceryBucketRes groceryBucketRes = groceryBucketService.findById(groceryBucketId);

		//then
		assertThat(groceryBucketRes.groceryName()).isEqualTo(groceryBucket.getGroceryName());
		assertThat(groceryBucketRes.groceryType()).isEqualTo(groceryBucket.getGroceryType().name());
		assertThat(groceryBucketRes.groceryDescription()).isEqualTo(groceryBucket.getGroceryDescription());
	}

	@DisplayName("사용자의 즐겨 찾는 식료품 목록 조회 테스트")
	@Test
	void findAll() {
		//given
		GroceryBucket groceryBucket2 = GroceryBucket.builder()
			.member(member)
			.groceryName("배추")
			.groceryType(GroceryType.VEGETABLES)
			.groceryDescription("김장용")
			.build();

		int page = 0;
		int size = 5;
		PageRequest pageRequest = PageRequest.of(page, size);

		SliceImpl<GroceryBucket> groceryBuckets = new SliceImpl<>(List.of(groceryBucket, groceryBucket2));


		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(groceryBucketRepository.findAllByMemberId(any(), any(), any(), any(), any())).thenReturn(groceryBuckets);

		//when
		GroceryBucketsRes groceryBucketsRes = groceryBucketService.findAll("updatedAtDesc", pageRequest, null, null, member.getUsername());

		//then
		assertThat(groceryBucketsRes.groceryBuckets()).hasSize(2);
		assertThat(groceryBucketsRes.groceryBuckets().get(0).groceryName()).isEqualTo(groceryBucket.getGroceryName());
		assertThat(groceryBucketsRes.groceryBuckets().get(0).groceryType()).isEqualTo(groceryBucket.getGroceryType().name());
		assertThat(groceryBucketsRes.groceryBuckets().get(0).groceryDescription()).isEqualTo(groceryBucket.getGroceryDescription());
	}

	@DisplayName("즐겨 찾는 식료품 수정 테스트")
	@Test
	void update() {
		//given
		Long groceryBucketId = 1L;

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(groceryBucketRepository.findByIdAndMemberId(any(), any())).thenReturn(Optional.of(groceryBucket));

		GroceryBucketReq groceryBucketReq = new GroceryBucketReq("과자", GroceryType.SNACKS.name(), "최애 과자");

		//when
		GroceryBucketRes groceryBucketRes = groceryBucketService.update(groceryBucketId, groceryBucketReq, member.getUsername());

		//then
		assertThat(groceryBucketRes.groceryName()).isEqualTo(groceryBucketReq.groceryName());
		assertThat(groceryBucketRes.groceryType()).isEqualTo(groceryBucketReq.groceryType());
		assertThat(groceryBucketRes.groceryDescription()).isEqualTo(groceryBucketReq.groceryDescription());
	}
}
