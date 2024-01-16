package com.icebox.freshmate.domain.recipebucket.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeRepository;
import com.icebox.freshmate.domain.recipe.domain.RecipeType;
import com.icebox.freshmate.domain.recipebucket.application.dto.request.RecipeBucketReq;
import com.icebox.freshmate.domain.recipebucket.application.dto.response.RecipeBucketRes;
import com.icebox.freshmate.domain.recipebucket.application.dto.response.RecipeBucketsRes;
import com.icebox.freshmate.domain.recipebucket.domain.RecipeBucket;
import com.icebox.freshmate.domain.recipebucket.domain.RecipeBucketRepository;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageType;

@ExtendWith(MockitoExtension.class)
class RecipeBucketServiceTest {

	@InjectMocks
	private RecipeBucketService recipeBucketService;

	@Mock
	private RecipeRepository recipeRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private RecipeBucketRepository recipeBucketRepository;

	private Member member;
	private Recipe recipe;
	private RecipeBucket recipeBucket;
	private Refrigerator refrigerator;
	private Storage storage;
	private Grocery grocery1;
	private Grocery grocery2;
	private RecipeGrocery recipeGrocery1;
	private RecipeGrocery recipeGrocery2;

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

		grocery1 = Grocery.builder()
			.storage(storage)
			.name("양배추")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().plusDays(7))
			.build();

		grocery2 = Grocery.builder()
			.storage(storage)
			.name("배추")
			.groceryType(GroceryType.VEGETABLES)
			.quantity("1개")
			.description("필수 식재료")
			.expirationDate(LocalDate.now().plusDays(7))
			.build();

		recipe = Recipe.builder()
			.writer(member)
			.owner(member)
			.recipeType(RecipeType.WRITTEN)
			.title("레시피")
			.content("내용")
			.build();

		recipeGrocery1 = RecipeGrocery.builder()
			.recipe(recipe)
			.grocery(grocery1)
			.groceryName(grocery1.getName())
			.build();

		recipeGrocery2 = RecipeGrocery.builder()
			.recipe(recipe)
			.grocery(grocery2)
			.groceryName(grocery2.getName())
			.build();

		recipe.addRecipeGrocery(recipeGrocery1);
		recipe.addRecipeGrocery(recipeGrocery2);

		recipeBucket = RecipeBucket.builder()
			.recipe(recipe)
			.member(member)
			.build();
	}

	@DisplayName("즐겨 찾는 레시피 생성 테스트")
	@Test
	void create() {
		//given
		Long recipeId = 1L;

		RecipeBucketReq recipeBucketReq = new RecipeBucketReq(recipeId);

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
		when(recipeBucketRepository.save(any(RecipeBucket.class))).thenReturn(recipeBucket);

		//when
		RecipeBucketRes recipeBucketRes = recipeBucketService.create(recipeBucketReq, member.getUsername());

		//then
		assertThat(recipeBucketRes.writerNickName()).isEqualTo(recipeBucket.getRecipe().getWriter().getNickName());
		assertThat(recipeBucketRes.recipeType()).isEqualTo(recipeBucket.getRecipe().getRecipeType().name());
		assertThat(recipeBucketRes.recipeTitle()).isEqualTo(recipeBucket.getRecipe().getTitle());
		assertThat(recipeBucketRes.recipeContent()).isEqualTo(recipeBucket.getRecipe().getContent());
		assertThat(recipeBucketRes.materials()).hasSize(2);
		assertThat(recipeBucketRes.materials().get(0).recipeTitle()).isEqualTo(recipeBucket.getRecipe().getRecipeGroceries().get(0).getRecipe().getTitle());
		assertThat(recipeBucketRes.materials().get(0).groceryName()).isEqualTo(recipeBucket.getRecipe().getRecipeGroceries().get(0).getGrocery().getName());
		assertThat(recipeBucketRes.memberNickName()).isEqualTo(recipeBucket.getMember().getNickName());
	}

	@DisplayName("즐겨 찾는 레시피 단건 조회 테스트")
	@Test
	void findById() {
		//given
		Long recipeBucketId = 1L;

		when(recipeBucketRepository.findById(anyLong())).thenReturn(Optional.of(recipeBucket));

		//when
		RecipeBucketRes recipeBucketRes = recipeBucketService.findById(recipeBucketId);

		//then
		assertThat(recipeBucketRes.writerNickName()).isEqualTo(recipeBucket.getRecipe().getWriter().getNickName());
		assertThat(recipeBucketRes.recipeType()).isEqualTo(recipeBucket.getRecipe().getRecipeType().name());
		assertThat(recipeBucketRes.recipeTitle()).isEqualTo(recipeBucket.getRecipe().getTitle());
		assertThat(recipeBucketRes.recipeContent()).isEqualTo(recipeBucket.getRecipe().getContent());
		assertThat(recipeBucketRes.materials()).hasSize(2);
		assertThat(recipeBucketRes.materials().get(0).recipeTitle()).isEqualTo(recipeBucket.getRecipe().getRecipeGroceries().get(0).getRecipe().getTitle());
		assertThat(recipeBucketRes.materials().get(0).groceryName()).isEqualTo(recipeBucket.getRecipe().getRecipeGroceries().get(0).getGrocery().getName());
		assertThat(recipeBucketRes.memberNickName()).isEqualTo(recipeBucket.getMember().getNickName());
	}

	@DisplayName("사용자의 즐겨 찾는 레시피 목록 조회 테스트")
	@Test
	void findAllByMemberId() {
		//given
		Recipe recipe2 = Recipe.builder()
			.writer(member)
			.owner(member)
			.recipeType(RecipeType.WRITTEN)
			.title("레시피2")
			.content("내용")
			.build();

		RecipeBucket recipeBucket2 = RecipeBucket.builder()
			.recipe(recipe2)
			.member(member)
			.build();

		recipe2.addRecipeGrocery(recipeGrocery1);
		recipe2.addRecipeGrocery(recipeGrocery2);

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(recipeBucketRepository.findAllByMemberId(any())).thenReturn(List.of(recipeBucket, recipeBucket2));

		//when
		RecipeBucketsRes recipeBucketsRes = recipeBucketService.findAllByMemberId(member.getUsername());

		//then
		assertThat(recipeBucketsRes.recipeBuckets()).hasSize(2);
		assertThat(recipeBucketsRes.recipeBuckets().get(0).writerNickName()).isEqualTo(recipeBucket.getRecipe().getWriter().getNickName());
		assertThat(recipeBucketsRes.recipeBuckets().get(0).recipeType()).isEqualTo(recipeBucket.getRecipe().getRecipeType().name());
		assertThat(recipeBucketsRes.recipeBuckets().get(0).recipeTitle()).isEqualTo(recipeBucket.getRecipe().getTitle());
		assertThat(recipeBucketsRes.recipeBuckets().get(0).recipeContent()).isEqualTo(recipeBucket.getRecipe().getContent());
		assertThat(recipeBucketsRes.recipeBuckets().get(0).materials()).hasSize(2);
		assertThat(recipeBucketsRes.recipeBuckets().get(0).materials().get(0).recipeTitle()).isEqualTo(recipeBucket.getRecipe().getRecipeGroceries().get(0).getRecipe().getTitle());
		assertThat(recipeBucketsRes.recipeBuckets().get(0).materials().get(0).groceryName()).isEqualTo(recipeBucket.getRecipe().getRecipeGroceries().get(0).getGrocery().getName());
		assertThat(recipeBucketsRes.recipeBuckets().get(0).memberNickName()).isEqualTo(recipeBucket.getMember().getNickName());
	}
}
