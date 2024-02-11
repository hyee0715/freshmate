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
		Member memberBuilder = getMember("성이름", "aaaa1111", "aaaa1111!", "닉네임", Role.USER);
		member = memberRepository.save(memberBuilder);

		Refrigerator refrigeratorBuilder = getRefrigerator("우리 집 냉장고", member);
		refrigerator = refrigeratorRepository.save(refrigeratorBuilder);

		Storage storageBuilder = getStorage("냉장실", StorageType.FRIDGE, refrigerator);
		storage = storageRepository.save(storageBuilder);

		Grocery groceryBuilder1 = getGrocery(storage, "양배추1", GroceryType.VEGETABLES, "1개", "필수 식재료", LocalDate.now().plusDays(1));
		grocery1 = groceryRepository.save(groceryBuilder1);

		Grocery groceryBuilder2 = getGrocery(storage, "양배추2", GroceryType.VEGETABLES, "1개", "필수 식재료", LocalDate.now().minusDays(2));
		grocery2 = groceryRepository.save(groceryBuilder2);

		Grocery groceryBuilder3 = getGrocery(storage, "양배추3", GroceryType.VEGETABLES, "1개", "필수 식재료", LocalDate.now().plusDays(9));
		grocery3 = groceryRepository.save(groceryBuilder3);

		Grocery groceryBuilder4 = getGrocery(storage, "양배추4", GroceryType.VEGETABLES, "1개", "필수 식재료", LocalDate.now().minusDays(8));
		grocery4 = groceryRepository.save(groceryBuilder4);

		Grocery groceryBuilder5 = getGrocery(storage, "양배추5", GroceryType.VEGETABLES, "1개", "필수 식재료", LocalDate.now().plusDays(6));
		grocery5 = groceryRepository.save(groceryBuilder5);

		Grocery groceryBuilder6 = getGrocery(storage, "양배추6", GroceryType.VEGETABLES, "1개", "필수 식재료", LocalDate.now().minusDays(11));
		grocery6 = groceryRepository.save(groceryBuilder6);

		Grocery groceryBuilder7 = getGrocery(storage, "양배추7", GroceryType.VEGETABLES, "1개", "필수 식재료", LocalDate.now().plusDays(15));
		grocery7 = groceryRepository.save(groceryBuilder7);

		Grocery groceryBuilder8 = getGrocery(storage, "기타1", GroceryType.ETC, "1개", "필수 식재료", LocalDate.now().minusDays(15));
		grocery8 = groceryRepository.save(groceryBuilder8);

		Grocery groceryBuilder9 = getGrocery(storage, "기타2", GroceryType.ETC, "1개", "필수 식재료", LocalDate.now().minusDays(10));
		grocery9 = groceryRepository.save(groceryBuilder9);

		Grocery groceryBuilder10 = getGrocery(storage, "기타3", GroceryType.ETC, "1개", "필수 식재료", LocalDate.now().plusDays(5));
		grocery10 = groceryRepository.save(groceryBuilder10);

		Grocery groceryBuilder11 = getGrocery(storage, "기타4", GroceryType.ETC, "1개", "필수 식재료", LocalDate.now().minusDays(3));
		grocery11 = groceryRepository.save(groceryBuilder11);

		Grocery groceryBuilder12 = getGrocery(storage, "기타5", GroceryType.ETC, "1개", "필수 식재료", LocalDate.now().plusDays(9));
		grocery12 = groceryRepository.save(groceryBuilder12);

		Grocery groceryBuilder13 = getGrocery(storage, "기타6", GroceryType.ETC, "1개", "필수 식재료", LocalDate.now());
		grocery13 = groceryRepository.save(groceryBuilder13);

		Grocery groceryBuilder14 = getGrocery(storage, "기타7", GroceryType.ETC, "1개", "필수 식재료", LocalDate.now());
		grocery14 = groceryRepository.save(groceryBuilder14);

		Grocery groceryBuilder15 = getGrocery(storage, "기타8", GroceryType.ETC, "1개", "필수 식재료", LocalDate.now());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, null, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, null, pageable, sortBy, grocery10.getName(), grocery10.getExpirationDate(), grocery10.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, null, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, null, pageable, sortBy, grocery4.getName(), grocery4.getExpirationDate(), grocery4.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, null, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, null, pageable, sortBy, grocery3.getName(), grocery3.getExpirationDate(), grocery3.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, null, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, null, pageable, sortBy, grocery13.getName(), grocery13.getExpirationDate(), grocery13.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, grocery13.getName(), grocery13.getExpirationDate(), grocery13.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, GroceryExpirationType.EXPIRED, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, GroceryExpirationType.EXPIRED, pageable, sortBy, grocery11.getName(), grocery11.getExpirationDate(), grocery11.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, GroceryExpirationType.EXPIRED, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, GroceryExpirationType.EXPIRED, pageable, sortBy, grocery4.getName(), grocery4.getExpirationDate(), grocery4.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, grocery4.getName(), grocery4.getExpirationDate(), grocery4.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, grocery1.getName(), grocery1.getExpirationDate(), grocery1.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, GroceryExpirationType.EXPIRED, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, null, GroceryExpirationType.EXPIRED, pageable, sortBy, grocery9.getName(), grocery9.getExpirationDate(), grocery9.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, grocery3.getName(), grocery3.getExpirationDate(), grocery3.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.ETC, GroceryExpirationType.EXPIRED, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.ETC, GroceryExpirationType.EXPIRED, pageable, sortBy, grocery9.getName(), grocery9.getExpirationDate(), grocery9.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, GroceryExpirationType.EXPIRED, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, GroceryExpirationType.EXPIRED, pageable, sortBy, grocery4.getName(), grocery4.getExpirationDate(), grocery4.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.ETC, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.ETC, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, grocery14.getName(), grocery14.getExpirationDate(), grocery14.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.ETC, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.ETC, GroceryExpirationType.NOT_EXPIRED, pageable, sortBy, grocery13.getName(), grocery13.getExpirationDate(), grocery13.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, GroceryExpirationType.EXPIRED, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, GroceryExpirationType.EXPIRED, pageable, sortBy, grocery4.getName(), grocery4.getExpirationDate(), grocery4.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, null, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, null, pageable, sortBy, grocery4.getName(), grocery4.getExpirationDate(), grocery4.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.ETC, null, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.ETC, null, pageable, sortBy, grocery12.getName(), grocery12.getExpirationDate(), grocery12.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, null, pageable, sortBy, null, null, null);
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.VEGETABLES, null, pageable, sortBy, grocery4.getName(), grocery4.getExpirationDate(), grocery4.getUpdatedAt());
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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.ETC, null, pageable, sortBy, null, null, null);
			List<Grocery> groceriesResult = groceries.stream().toList();

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
			Slice<Grocery> groceries = groceryRepository.findAllByWhereConditionsAndOrderBySortConditions(storageId, memberId, GroceryType.ETC, null, pageable, sortBy, grocery12.getName(), grocery12.getExpirationDate(), grocery12.getUpdatedAt());
			List<Grocery> groceriesResult = groceries.stream().toList();

			//then
			assertThat(groceriesResult.size()).isEqualTo(expected.size());
			assertThat(groceriesResult).containsExactlyElementsOf(expected);
		}
	}

	private Grocery getGrocery(Storage storage, String name, GroceryType groceryType, String quantity, String description, LocalDate expirationDate) {

		return Grocery.builder()
			.storage(storage)
			.name(name)
			.groceryType(groceryType)
			.quantity(quantity)
			.description(description)
			.expirationDate(expirationDate)
			.build();
	}

	private Member getMember(String realName, String username, String password, String nickName, Role role) {

		return Member.builder()
			.realName(realName)
			.username(username)
			.password(password)
			.nickName(nickName)
			.role(role)
			.build();
	}

	private Refrigerator getRefrigerator(String name, Member member) {

		return Refrigerator.builder()
			.name(name)
			.member(member)
			.build();
	}

	private Storage getStorage(String name, StorageType storageType, Refrigerator refrigerator) {

		return Storage.builder()
			.name(name)
			.storageType(storageType)
			.refrigerator(refrigerator)
			.build();
	}
}
