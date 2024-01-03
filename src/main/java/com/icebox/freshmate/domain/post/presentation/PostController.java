package com.icebox.freshmate.domain.post.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.post.application.PostService;
import com.icebox.freshmate.domain.post.application.dto.request.PostReq;
import com.icebox.freshmate.domain.post.application.dto.response.PostRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostController {

	private final PostService postService;

	@PostMapping
	public ResponseEntity<PostRes> create(@Validated @RequestBody PostReq postReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		PostRes postRes = postService.create(postReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(postRes);
	}
}
