package com.icebox.freshmate.domain.post.application;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.image.application.ImageService;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.image.application.dto.response.ImagesRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;
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
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;
import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGroceryRepository;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageType;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@InjectMocks
	private PostService postService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PostRepository postRepository;

	@Mock
	private RecipeRepository recipeRepository;

	@Mock
	private RecipeGroceryRepository recipeGroceryRepository;

	@Mock
	private PostImageRepository postImageRepository;

	@Mock
	private ImageService imageService;

	private Member member;
	private Post post;
	private Recipe recipe;
	private Refrigerator refrigerator;
	private Storage storage;
	private Grocery grocery1;
	private Grocery grocery2;
	private RecipeGrocery recipeGrocery1;
	private RecipeGrocery recipeGrocery2;
	private PostImage postImage;

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
			.quantity("2개")
			.description("김장용")
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

		post = Post.builder()
			.title("제목")
			.content("내용")
			.member(member)
			.recipe(recipe)
			.build();

		String imageFileName = "image.jpg";
		String imagePath = "https://fake-image-url.com/image.jpg";

		postImage = PostImage
			.builder()
			.post(post)
			.fileName(imageFileName)
			.path(imagePath)
			.build();

		post.addPostImage(postImage);
	}

	@DisplayName("게시글 생성 테스트")
	@Test
	void create() {
		//given
		Long recipeId = 1L;

		PostReq postReq = new PostReq(post.getTitle(), post.getContent(), recipeId);

		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "Spring Framework".getBytes());
		ImageUploadReq imageUploadReq = new ImageUploadReq(List.of(file));

		ImageRes imageRes = new ImageRes(postImage.getFileName(), postImage.getPath());
		ImagesRes imagesRes = new ImagesRes(List.of(imageRes));

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(postRepository.save(any(Post.class))).thenReturn(post);
		when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
		when(recipeGroceryRepository.findAllByRecipeId(any())).thenReturn(List.of(recipeGrocery1, recipeGrocery2));
		when(postImageRepository.save(any(PostImage.class))).thenReturn(postImage);
		when(imageService.store(any(ImageUploadReq.class))).thenReturn(imagesRes);

		//when
		PostRes postRes = postService.create(postReq, imageUploadReq, member.getUsername());

		//then
		assertThat(postRes.postTitle()).isEqualTo(post.getTitle());
		assertThat(postRes.postContent()).isEqualTo(post.getContent());
		assertThat(postRes.recipeWriterNickName()).isEqualTo(post.getRecipe().getWriter().getNickName());
		assertThat(postRes.recipeTitle()).isEqualTo(post.getRecipe().getTitle());
		assertThat(postRes.recipeContent()).isEqualTo(post.getRecipe().getContent());
		assertThat(postRes.recipeMaterials()).hasSize(2);
		assertThat(postRes.recipeMaterials().get(0).recipeTitle()).isEqualTo(recipe.getTitle());
		assertThat(postRes.recipeMaterials().get(0).groceryName()).isEqualTo(grocery1.getName());
		assertThat(postRes.images().get(0).fileName()).isEqualTo(postImage.getFileName());
		assertThat(postRes.images().get(0).path()).isEqualTo(postImage.getPath());
	}

	@DisplayName("게시글 단건 조회 테스트")
	@Test
	void findById() {
		//given
		Long postId = 1L;

		when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
		when(recipeGroceryRepository.findAllByRecipeId(any())).thenReturn(List.of(recipeGrocery1, recipeGrocery2));

		//when
		PostRes postRes = postService.findById(postId);

		//then
		assertThat(postRes.postTitle()).isEqualTo(post.getTitle());
		assertThat(postRes.postContent()).isEqualTo(post.getContent());
		assertThat(postRes.recipeWriterNickName()).isEqualTo(post.getRecipe().getWriter().getNickName());
		assertThat(postRes.recipeTitle()).isEqualTo(post.getRecipe().getTitle());
		assertThat(postRes.recipeContent()).isEqualTo(post.getRecipe().getContent());
		assertThat(postRes.recipeMaterials()).hasSize(2);
		assertThat(postRes.recipeMaterials().get(0).recipeTitle()).isEqualTo(recipe.getTitle());
		assertThat(postRes.recipeMaterials().get(0).groceryName()).isEqualTo(grocery1.getName());
	}

	@DisplayName("게시글 목록 조회 테스트")
	@Test
	void findAllByMemberId() {
		//given
		Long memberId = 1L;

		Post post2 = Post.builder()
			.title("제목2")
			.content("내용2")
			.member(member)
			.recipe(recipe)
			.build();

		int page = 0;
		int size = 10;
		PageRequest pageRequest = PageRequest.of(page, size);

		SliceImpl<Post> posts = new SliceImpl<>(List.of(post, post2));

		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
		when(postRepository.findAllByCondition(any(), any(), any(), any())).thenReturn(posts);

		//when
		PostsRes postsRes = postService.findAll("idDesc", pageRequest, memberId, null);

		//then
		assertThat(postsRes.posts()).hasSize(2);
		assertThat(postsRes.posts().get(0).postTitle()).isEqualTo(post.getTitle());
		assertThat(postsRes.posts().get(0).postContent()).isEqualTo(post.getContent());
		assertThat(postsRes.posts().get(0).recipeWriterNickName()).isEqualTo(post.getRecipe().getWriter().getNickName());
		assertThat(postsRes.posts().get(0).recipeTitle()).isEqualTo(post.getRecipe().getTitle());
		assertThat(postsRes.posts().get(0).recipeContent()).isEqualTo(post.getRecipe().getContent());
		assertThat(postsRes.posts().get(0).recipeMaterials()).hasSize(2);
		assertThat(postsRes.posts().get(0).recipeMaterials().get(0).recipeTitle()).isEqualTo(recipe.getTitle());
		assertThat(postsRes.posts().get(0).recipeMaterials().get(0).groceryName()).isEqualTo(grocery1.getName());
	}

	@DisplayName("게시글 수정 테스트")
	@Test
	void update() {
		//given
		Long postId = 1L;
		Long recipeId = 1L;

		PostReq postReq = new PostReq("제목 수정", "내용 수정", recipeId);

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(postRepository.findByIdAndMemberId(any(), any())).thenReturn(Optional.of(post));
		when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
		when(recipeGroceryRepository.findAllByRecipeId(any())).thenReturn(List.of(recipeGrocery1, recipeGrocery2));

		//when
		PostRes postRes = postService.update(postId, postReq, member.getUsername());

		//then
		assertThat(postRes.postTitle()).isEqualTo(postReq.title());
		assertThat(postRes.postContent()).isEqualTo(postReq.content());
		assertThat(postRes.recipeWriterNickName()).isEqualTo(post.getRecipe().getWriter().getNickName());
		assertThat(postRes.recipeTitle()).isEqualTo(post.getRecipe().getTitle());
		assertThat(postRes.recipeContent()).isEqualTo(post.getRecipe().getContent());
		assertThat(postRes.recipeMaterials()).hasSize(2);
		assertThat(postRes.recipeMaterials().get(0).recipeTitle()).isEqualTo(recipe.getTitle());
		assertThat(postRes.recipeMaterials().get(0).groceryName()).isEqualTo(grocery1.getName());
	}
}
