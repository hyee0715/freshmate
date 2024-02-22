package com.icebox.freshmate.domain.post.presentation;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.image.application.dto.request.ImageDeleteReq;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.post.application.PostService;
import com.icebox.freshmate.domain.post.application.dto.request.PostReq;
import com.icebox.freshmate.domain.post.application.dto.response.PostRes;
import com.icebox.freshmate.domain.post.application.dto.response.PostsRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostController {

	private static final String DEFAULT_PAGE_SIZE = "10";

	private final PostService postService;

	@PostMapping
	public ResponseEntity<PostRes> create(@Validated @RequestPart PostReq postReq, @RequestPart(required = false) List<MultipartFile> imageFiles, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		ImageUploadReq imageUploadReq = new ImageUploadReq(imageFiles);

		PostRes postRes = postService.create(postReq, imageUploadReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(postRes);
	}

	@PostMapping("/post-images/{postId}")
	public ResponseEntity<PostRes> addPostImage(@PathVariable Long postId, MultipartFile imageFile, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		ImageUploadReq imageUploadReq = new ImageUploadReq(List.of(imageFile));

		PostRes postRes = postService.addPostImage(postId, imageUploadReq, principalDetails.getUsername());

		return ResponseEntity.ok(postRes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PostRes> findById(@PathVariable Long id) {
		PostRes postRes = postService.findById(id);

		return ResponseEntity.ok(postRes);
	}

	@GetMapping
	public ResponseEntity<PostsRes> findAll(@RequestParam(value = "search-type", required = false, defaultValue = "all") String searchType,
											@RequestParam(required = false, defaultValue = "") String keyword,
											@RequestParam(value = "sort-by", required = false, defaultValue = "idDesc") String sortBy,
											@RequestParam(value = "last-page-id", required = false) Long lastPageId,
											@RequestParam(required = false, defaultValue = "0") int page,
											@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
											@RequestParam(value = "member-id", required = false) Long memberId) {
		page = Math.max(page - 1, 0);
		PageRequest pageable = PageRequest.of(page, size);

		PostsRes postsRes = postService.findAll(searchType, keyword, sortBy, pageable, memberId, lastPageId);

		return ResponseEntity.ok(postsRes);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<PostRes> update(@PathVariable Long id, @Validated @RequestBody PostReq postReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		PostRes postRes = postService.update(id, postReq, principalDetails.getUsername());

		return ResponseEntity.ok(postRes);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		postService.delete(id, principalDetails.getUsername());

		return ResponseEntity.noContent()
			.build();
	}

	@DeleteMapping("/post-images/{postId}")
	public ResponseEntity<PostRes> removePostImage(@PathVariable Long postId, @RequestBody ImageDeleteReq imageDeleteReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		PostRes postRes = postService.removePostImage(postId, imageDeleteReq, principalDetails.getUsername());

		return ResponseEntity.ok(postRes);
	}
}
