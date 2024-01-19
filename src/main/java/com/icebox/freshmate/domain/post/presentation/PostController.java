package com.icebox.freshmate.domain.post.presentation;

import java.util.List;

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

	private final PostService postService;

	@PostMapping
	public ResponseEntity<PostRes> create(@Validated @RequestPart PostReq postReq, @RequestPart(required = false) List<MultipartFile> imageFiles, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		ImageUploadReq imageUploadReq = new ImageUploadReq(imageFiles);

		PostRes postRes = postService.create(postReq, imageUploadReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(postRes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PostRes> findById(@PathVariable Long id) {
		PostRes postRes = postService.findById(id);

		return ResponseEntity.ok(postRes);
	}

	@GetMapping
	public ResponseEntity<PostsRes> findAllByMemberId(@RequestParam("member-id") Long memberId) {
		PostsRes postsRes = postService.findAllByMemberId(memberId);

		return ResponseEntity.ok(postsRes);
	}

//	@PatchMapping("/{id}")
//	public ResponseEntity<PostRes> update(@PathVariable Long id, @Validated @RequestBody PostReq postReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
//		PostRes postRes = postService.update(id, postReq, principalDetails.getUsername());
//
//		return ResponseEntity.ok(postRes);
//	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		postService.delete(id, principalDetails.getUsername());

		return ResponseEntity.noContent()
			.build();
	}
}
