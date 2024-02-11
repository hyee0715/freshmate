package com.icebox.freshmate.domain.grocery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.refrigerator.domain.RefrigeratorRepository;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageRepository;
import com.icebox.freshmate.domain.storage.domain.StorageType;
import com.icebox.freshmate.global.config.JpaConfig;

@Import(JpaConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GroceryRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RefrigeratorRepository refrigeratorRepository;

	@Autowired
	private StorageRepository storageRepository;

	@Autowired
	private GroceryRepository groceryRepository;

	private Member member;
	private Refrigerator refrigerator;
	private Storage storage;
	private Grocery grocery1;
	private Grocery grocery2;
	private Grocery grocery3;
	private Grocery grocery4;
	private Grocery grocery5;
	private Grocery grocery6;
	private Grocery grocery7;
	private Grocery grocery8;
	private Grocery grocery9;
	private Grocery grocery10;
	private Grocery grocery11;
	private Grocery grocery12;
	private Grocery grocery13;
	private Grocery grocery14;
	private Grocery grocery15;

	@BeforeEach
	void setUp() {
		Member memberBuilder = Member.builder()
			.realName("성이름")
			.username("aaaa1111")
			.password("aaaa1111!")
			.nickName("닉네임닉네임")
			.role(Role.USER)
			.build();
		member = memberRepository.save(memberBuilder);

		Refrigerator refrigeratorBuilder = Refrigerator.builder()
			.name("우리 집 냉장고")
			.member(member)
			.build();
		refrigerator = refrigeratorRepository.save(refrigeratorBuilder);

		Storage storageBuilder = Storage.builder()
			.name("냉장실")
			.storageType(StorageType.FRIDGE)
			.refrigerator(refrigerator)
			.build();
		storage = storageRepository.save(storageBuilder);

		Grocery groceryBuilder1 = Grocery.builder()
			.storage(storage)
			.name("양배추1")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().plusDays(1))
			.build();
		grocery1 = groceryRepository.save(groceryBuilder1);

		Grocery groceryBuilder2 = Grocery.builder()
			.storage(storage)
			.name("양배추2")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().minusDays(2))
			.build();
		grocery2 = groceryRepository.save(groceryBuilder2);

		Grocery groceryBuilder3 = Grocery.builder()
			.storage(storage)
			.name("양배추3")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().plusDays(9))
			.build();
		grocery3 = groceryRepository.save(groceryBuilder3);

		Grocery groceryBuilder4 = Grocery.builder()
			.storage(storage)
			.name("양배추4")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().minusDays(8))
			.build();
		grocery4 = groceryRepository.save(groceryBuilder4);

		Grocery groceryBuilder5 = Grocery.builder()
			.storage(storage)
			.name("양배추5")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().plusDays(6))
			.build();
		grocery5 = groceryRepository.save(groceryBuilder5);

		Grocery groceryBuilder6 = Grocery.builder()
			.storage(storage)
			.name("양배추6")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().minusDays(11))
			.build();
		grocery6 = groceryRepository.save(groceryBuilder6);

		Grocery groceryBuilder7 = Grocery.builder()
			.storage(storage)
			.name("양배추7")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().plusDays(15))
			.build();
		grocery7 = groceryRepository.save(groceryBuilder7);

		Grocery groceryBuilder8 = Grocery.builder()
			.storage(storage)
			.name("기타1")
			.groceryType(GroceryType.ETC)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().minusDays(15))
			.build();
		grocery8 = groceryRepository.save(groceryBuilder8);

		Grocery groceryBuilder9 = Grocery.builder()
			.storage(storage)
			.name("기타2")
			.groceryType(GroceryType.ETC)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().minusDays(10))
			.build();
		grocery9 = groceryRepository.save(groceryBuilder9);

		Grocery groceryBuilder10 = Grocery.builder()
			.storage(storage)
			.name("기타3")
			.groceryType(GroceryType.ETC)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().plusDays(5))
			.build();
		grocery10 = groceryRepository.save(groceryBuilder10);

		Grocery groceryBuilder11 = Grocery.builder()
			.storage(storage)
			.name("기타4")
			.groceryType(GroceryType.ETC)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().minusDays(3))
			.build();
		grocery11 = groceryRepository.save(groceryBuilder11);

		Grocery groceryBuilder12 = Grocery.builder()
			.storage(storage)
			.name("기타5")
			.groceryType(GroceryType.ETC)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().plusDays(9))
			.build();
		grocery12 = groceryRepository.save(groceryBuilder12);

		Grocery groceryBuilder13 = Grocery.builder()
			.storage(storage)
			.name("기타6")
			.groceryType(GroceryType.ETC)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now())
			.build();
		grocery13 = groceryRepository.save(groceryBuilder13);

		Grocery groceryBuilder14 = Grocery.builder()
			.storage(storage)
			.name("기타7")
			.groceryType(GroceryType.ETC)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now())
			.build();
		grocery14 = groceryRepository.save(groceryBuilder14);

		Grocery groceryBuilder15 = Grocery.builder()
			.storage(storage)
			.name("기타8")
			.groceryType(GroceryType.ETC)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now())
			.build();
		grocery15 = groceryRepository.save(groceryBuilder15);
	}

	@Nested
	@DisplayName("저장소 ID와 회원 ID로 모든 식료품 목록 조회 테스트")
	class findAllByStorageIdAndMemberIdOrderBySortCondition {

		@DisplayName("식료품 이름 오름차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdOrderByNameAsc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);

			String sortBy = "nameAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery8, grocery9, grocery10, grocery11, grocery12));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdOrderBySortConditions(storageId, memberId, pageable, sortBy, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 이름 오름차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdOrderByNameAscUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			Pageable pageable = PageRequest.of(0, 5);
			String sortBy = "nameAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery11, grocery12, grocery13, grocery14, grocery15));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdOrderBySortConditions(storageId, memberId, pageable, sortBy, grocery10.getName(), grocery10.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 이름 내림차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdOrderByNameDesc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);

			String sortBy = "nameDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery7, grocery6, grocery5, grocery4, grocery3));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdOrderBySortConditions(storageId, memberId, pageable, sortBy, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 이름 내림차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdOrderByNameDescUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			Pageable pageable = PageRequest.of(0, 5);
			String sortBy = "nameDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery3, grocery2, grocery1, grocery15, grocery14));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdOrderBySortConditions(storageId, memberId, pageable, sortBy, grocery4.getName(), grocery4.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 오름차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdOrderByUpdatedAtAsc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);

			String sortBy = "updatedAtAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery1, grocery2, grocery3, grocery4, grocery5));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdOrderBySortConditions(storageId, memberId, pageable, sortBy, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 오름차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdOrderByUpdatedAtAscUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			Pageable pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery4, grocery5, grocery6, grocery7, grocery8));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdOrderBySortConditions(storageId, memberId, pageable, sortBy, grocery3.getName(), grocery3.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 내림차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdOrderByUpdatedAtDesc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery15, grocery14, grocery13, grocery12, grocery11));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdOrderBySortConditions(storageId, memberId, pageable, sortBy, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 내림차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdOrderByUpdatedAtDescUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery12, grocery11, grocery10, grocery9, grocery8));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdOrderBySortConditions(storageId, memberId, pageable, sortBy, grocery13.getName(), grocery13.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}
	}

	@Nested
	@DisplayName("저장소 ID와 회원 ID와 식료품 유통기한 타입으로 모든 식료품 목록 조회 테스트")
	class findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortCondition {

		@DisplayName("식료품 이름 오름차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByNameAsc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "nameAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery10, grocery12, grocery13, grocery14, grocery15));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 이름 오름차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByNameAscUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "nameAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery14, grocery15, grocery1, grocery3, grocery5));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, grocery13.getName(), grocery13.getExpirationDate(), grocery13.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 이름 내림차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByNameDesc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "nameDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery6, grocery4, grocery2, grocery11, grocery9));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryExpirationType.EXPIRED, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 이름 내림차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByNameDescUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "nameDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery9, grocery8));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryExpirationType.EXPIRED, pageable, sortBy, grocery11.getName(), grocery11.getExpirationDate(), grocery11.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 오름차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByUpdatedAtAsc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery2, grocery4, grocery6, grocery8, grocery9));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryExpirationType.EXPIRED, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 오름차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByUpdatedAtAscUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery6, grocery8, grocery9, grocery11));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryExpirationType.EXPIRED, pageable, sortBy, grocery4.getName(), grocery4.getExpirationDate(), grocery4.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 내림차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByUpdatedAtDesc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery15, grocery14, grocery13, grocery12, grocery10));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 내림차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByUpdatedAtDescUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery3, grocery1));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, grocery4.getName(), grocery4.getExpirationDate(), grocery4.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 유통기한 날짜 오름차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByExpirationDateAsc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "expirationDateAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery15, grocery14, grocery13, grocery1, grocery10));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 유통기한 날짜 오름차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByExpirationDateAscUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "expirationDateAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery10, grocery5, grocery12, grocery3, grocery7));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, grocery1.getName(), grocery1.getExpirationDate(), grocery1.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 유통기한 날짜 내림차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByExpirationDateDesc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "expirationDateDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery2, grocery11, grocery4, grocery9, grocery6));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryExpirationType.EXPIRED, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 유통기한 날짜 내림차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByExpirationDateDescUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "expirationDateDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery6, grocery8));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryExpirationType.EXPIRED, pageable, sortBy, grocery9.getName(), grocery9.getExpirationDate(), grocery9.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}
	}

	@Nested
	@DisplayName("저장소 ID와 회원 ID와 식료품 타입과 식료품 유통기한 타입으로 모든 식료품 목록 조회 테스트")
	class findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortCondition {

		@DisplayName("식료품 이름 오름차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByNameAsc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "nameAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery1, grocery3, grocery5, grocery7));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 이름 오름차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByNameAscUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "nameAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery5, grocery7));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, grocery3.getName(), grocery3.getExpirationDate(), grocery3.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 이름 내림차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByNameDesc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "nameDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery11, grocery9, grocery8));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryType.ETC, GroceryExpirationType.EXPIRED, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 이름 내림차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByNameDescUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "nameDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery8));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryType.ETC, GroceryExpirationType.EXPIRED, pageable, sortBy, grocery9.getName(), grocery9.getExpirationDate(), grocery9.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 오름차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByUpdatedAtAsc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery2, grocery4, grocery6));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, GroceryExpirationType.EXPIRED, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 오름차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByUpdatedAtAscUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery6));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, GroceryExpirationType.EXPIRED, pageable, sortBy, grocery4.getName(), grocery4.getExpirationDate(), grocery4.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 내림차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByUpdatedAtDesc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery15, grocery14, grocery13, grocery12, grocery10));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryType.ETC, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 내림차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByUpdatedAtDescUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery13, grocery12, grocery10));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryType.ETC, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, grocery14.getName(), grocery14.getExpirationDate(), grocery14.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 유통기한 날짜 오름차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByExpirationDateAsc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "expirationDateAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery15, grocery14, grocery13, grocery10, grocery12));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryType.ETC, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 유통기한 날짜 오름차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByExpirationDateAscUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "expirationDateAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery10, grocery12));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryType.ETC, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, grocery13.getName(), grocery13.getExpirationDate(), grocery13.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 유통기한 날짜 내림차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByExpirationDateDesc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "expirationDateDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery2, grocery4, grocery6));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, GroceryExpirationType.EXPIRED, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 유통기한 날짜 내림차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByExpirationDateDescUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "expirationDateDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery6));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, GroceryExpirationType.EXPIRED, pageable, sortBy, grocery4.getName(), grocery4.getExpirationDate(), grocery4.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}
	}

	@Nested
	@DisplayName("저장소 ID와 회원 ID와 식료품 타입으로 모든 식료품 목록 조회 테스트")
	class findAllByStorageIdAndMemberIdAndGroceryTypeOrderBySortCondition {

		@DisplayName("식료품 이름 오름차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeOrderByNameAsc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "nameAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery1, grocery2, grocery3, grocery4, grocery5));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, pageable, sortBy, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 이름 오름차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeOrderByNameAscUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "nameAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery5, grocery6, grocery7));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, pageable, sortBy, grocery4.getName(), grocery4.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 이름 내림차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeOrderByNameDesc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "nameDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery15, grocery14, grocery13, grocery12, grocery11));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeOrderBySortConditions(storageId, memberId, GroceryType.ETC, pageable, sortBy, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 이름 내림차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeOrderByNameDescUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "nameDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery11, grocery10, grocery9, grocery8));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeOrderBySortConditions(storageId, memberId, GroceryType.ETC, pageable, sortBy, grocery12.getName(), grocery12.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 오름차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeOrderByUpdatedAtAsc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery1, grocery2, grocery3, grocery4, grocery5));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, pageable, sortBy, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 오름차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeOrderByUpdatedAtAscUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtAsc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery5, grocery6, grocery7));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, pageable, sortBy, grocery4.getName(), grocery4.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 내림차순 정렬")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeOrderByUpdatedAtDesc() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery15, grocery14, grocery13, grocery12, grocery11));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeOrderBySortConditions(storageId, memberId, GroceryType.ETC, pageable, sortBy, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();
			for (Grocery g : groceriesResult) {
				System.out.println(g.getName());
			}
			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}

		@DisplayName("식료품 수정 날짜 내림차순 정렬, No Offset 페이징 테스트")
		@Test
		void findAllByStorageIdAndMemberIdAndGroceryTypeOrderByUpdatedAtDescUsingNoOffset() {
			//given
			Long storageId = storage.getId();
			Long memberId = member.getId();

			PageRequest pageable = PageRequest.of(0, 5);
			String sortBy = "updatedAtDesc";
			List<Grocery> expected = new ArrayList<>(List.of(grocery11, grocery10, grocery9, grocery8));

			//when
			Slice<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberIdAndGroceryTypeOrderBySortConditions(storageId, memberId, GroceryType.ETC, pageable, sortBy, grocery12.getName(), grocery12.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();
			for (Grocery g : groceriesResult) {
				System.out.println(g.getName());
			}
			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}
	}
}
