package com.icebox.freshmate.domain.recipe.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.recipe.application.dto.request.RecipeReq;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipeRes;
import com.icebox.freshmate.domain.recipe.application.dto.response.RecipesRes;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.domain.recipe.domain.RecipeRepository;
import com.icebox.freshmate.domain.recipe.domain.RecipeType;
import com.icebox.freshmate.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

	@InjectMocks
	private RecipeService recipeService;

	@Mock
	private RecipeRepository recipeRepository;

	@Mock
	private MemberRepository memberRepository;

	private Member member1;
	private Recipe recipe1;
	private Recipe recipe2;
	private Recipe recipe3;

	@BeforeEach
	void setUp() {
		member1 = Member.builder()
			.realName("성이름")
			.username("aaaa1111")
			.password("aaaa1111!")
			.nickName("닉네임닉네임")
			.role(Role.USER)
			.build();

		Member member2 = Member.builder()
			.realName("김이름")
			.username("aaaa2222")
			.password("aaaa2222!")
			.nickName("닉네임닉네임2")
			.role(Role.USER)
			.build();

		recipe1 = Recipe.builder()
			.writer(member1)
			.owner(member1)
			.recipeType(RecipeType.WRITTEN)
			.title("레시피1")
			.material("재료")
			.content("내용")
			.build();

		recipe2 = Recipe.builder()
			.writer(member2)
			.owner(member1)
			.recipeType(RecipeType.SCRAPED)
			.title("레시피2")
			.material("재료")
			.content("내용")
			.build();

		recipe3 = Recipe.builder()
			.writer(member1)
			.owner(member2)
			.recipeType(RecipeType.SCRAPED)
			.title("레시피3")
			.material("재료")
			.content("내용")
			.build();
	}

	@DisplayName("레시피 생성 테스트")
	@Test
	void create() {
		//given
		RecipeReq recipeReq = new RecipeReq(recipe1.getTitle(), recipe1.getMaterial(), recipe1.getContent());

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member1));
		when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe1);

		//when
		RecipeRes recipeRes = recipeService.create(recipeReq, member1.getUsername());

		//then
		assertThat(recipeRes.writerNickName()).isEqualTo(recipe1.getWriter().getNickName());
		assertThat(recipeRes.ownerNickName()).isEqualTo(recipe1.getOwner().getNickName());
		assertThat(recipeRes.recipeType()).isEqualTo(recipe1.getRecipeType().name());
		assertThat(recipeRes.title()).isEqualTo(recipe1.getTitle());
		assertThat(recipeRes.material()).isEqualTo(recipe1.getMaterial());
		assertThat(recipeRes.content()).isEqualTo(recipe1.getContent());
	}

	@DisplayName("레시피 단건 조회 테스트")
	@Test
	void findById() {
		//given
		Long recipeId = 1L;

		when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe1));

		//when
		RecipeRes recipeRes = recipeService.findById(recipeId);

		//then
		assertThat(recipeRes.writerNickName()).isEqualTo(recipe1.getWriter().getNickName());
		assertThat(recipeRes.ownerNickName()).isEqualTo(recipe1.getOwner().getNickName());
		assertThat(recipeRes.recipeType()).isEqualTo(recipe1.getRecipeType().name());
		assertThat(recipeRes.title()).isEqualTo(recipe1.getTitle());
		assertThat(recipeRes.material()).isEqualTo(recipe1.getMaterial());
		assertThat(recipeRes.content()).isEqualTo(recipe1.getContent());
	}

	@DisplayName("사용자가 작성한 모든 레시피 조회 테스트")
	@Test
	void findAllByWriterId() {
		//given
		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member1));
		when(recipeRepository.findAllByWriterId(any())).thenReturn(List.of(recipe1, recipe2));

		//when
		RecipesRes recipesRes = recipeService.findAllByWriterId(member1.getUsername());

		//then
		assertThat(recipesRes.recipes()).hasSize(2);
		assertThat(recipesRes.recipes().get(0).writerNickName()).isEqualTo(recipe1.getWriter().getNickName());
		assertThat(recipesRes.recipes().get(0).ownerNickName()).isEqualTo(recipe1.getOwner().getNickName());
		assertThat(recipesRes.recipes().get(0).recipeType()).isEqualTo(recipe1.getRecipeType().name());
		assertThat(recipesRes.recipes().get(0).title()).isEqualTo(recipe1.getTitle());
		assertThat(recipesRes.recipes().get(0).material()).isEqualTo(recipe1.getMaterial());
		assertThat(recipesRes.recipes().get(0).content()).isEqualTo(recipe1.getContent());
	}

	@DisplayName("사용자가 스크랩한(소유한) 모든 레시피 조회 테스트")
	@Test
	void findAllByOwnerId() {
		//given
		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member1));
		when(recipeRepository.findAllByWriterId(any())).thenReturn(List.of(recipe1, recipe3));

		//when
		RecipesRes recipesRes = recipeService.findAllByWriterId(member1.getUsername());

		//then
		assertThat(recipesRes.recipes()).hasSize(2);
		assertThat(recipesRes.recipes().get(0).writerNickName()).isEqualTo(recipe1.getWriter().getNickName());
		assertThat(recipesRes.recipes().get(0).ownerNickName()).isEqualTo(recipe1.getOwner().getNickName());
		assertThat(recipesRes.recipes().get(0).recipeType()).isEqualTo(recipe1.getRecipeType().name());
		assertThat(recipesRes.recipes().get(0).title()).isEqualTo(recipe1.getTitle());
		assertThat(recipesRes.recipes().get(0).material()).isEqualTo(recipe1.getMaterial());
		assertThat(recipesRes.recipes().get(0).content()).isEqualTo(recipe1.getContent());
	}

	@DisplayName("사용자가 작성하고 스크랩한(소유한) 모든 레시피 조회 테스트")
	@Test
	void findAllByMemberId() {
		//given
		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member1));
		when(recipeRepository.findAllByWriterId(any())).thenReturn(List.of(recipe1, recipe2, recipe3));

		//when
		RecipesRes recipesRes = recipeService.findAllByWriterId(member1.getUsername());

		//then
		assertThat(recipesRes.recipes()).hasSize(3);
		assertThat(recipesRes.recipes().get(0).writerNickName()).isEqualTo(recipe1.getWriter().getNickName());
		assertThat(recipesRes.recipes().get(0).ownerNickName()).isEqualTo(recipe1.getOwner().getNickName());
		assertThat(recipesRes.recipes().get(0).recipeType()).isEqualTo(recipe1.getRecipeType().name());
		assertThat(recipesRes.recipes().get(0).title()).isEqualTo(recipe1.getTitle());
		assertThat(recipesRes.recipes().get(0).material()).isEqualTo(recipe1.getMaterial());
		assertThat(recipesRes.recipes().get(0).content()).isEqualTo(recipe1.getContent());
	}

	@DisplayName("사용자가 작성한 레시피 수정 성공 테스트")
	@Test
	void update() {
		//given
		Long recipeId = 1L;

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member1));
		when(recipeRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(recipe1));

		String updateTitle = "수정된제목";
		String updateMaterial = "수정된재료";
		String updateContent = "수정된내용";

		RecipeReq recipeReq = new RecipeReq(updateTitle, updateMaterial, updateContent);

		//when
		RecipeRes recipeRes = recipeService.update(recipeId, recipeReq, member1.getUsername());

		//then
		assertThat(recipeRes.writerNickName()).isEqualTo(recipe1.getWriter().getNickName());
		assertThat(recipeRes.ownerNickName()).isEqualTo(recipe1.getOwner().getNickName());
		assertThat(recipeRes.recipeType()).isEqualTo(recipe1.getRecipeType().name());
		assertThat(recipeRes.title()).isEqualTo(updateTitle);
		assertThat(recipeRes.material()).isEqualTo(updateMaterial);
		assertThat(recipeRes.content()).isEqualTo(updateContent);
	}

	@DisplayName("사용자가 작성한 레시피 수정 실패 테스트 - 스크랩한 레시피는 수정 불가")
	@Test
	void updateFailure() {
		//given
		Long recipeId = 1L;

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member1));
		when(recipeRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(recipe2));

		String updateTitle = "수정된제목";
		String updateMaterial = "수정된재료";
		String updateContent = "수정된내용";

		RecipeReq recipeReq = new RecipeReq(updateTitle, updateMaterial, updateContent);

		//when
		//then
		assertThrows(BusinessException.class, () -> recipeService.update(recipeId, recipeReq, member1.getUsername()));
	}
}