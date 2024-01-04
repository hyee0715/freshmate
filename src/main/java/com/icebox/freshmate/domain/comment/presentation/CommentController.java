package com.icebox.freshmate.domain.comment.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.comment.application.CommentService;
import com.icebox.freshmate.domain.comment.application.dto.request.CommentCreateReq;
import com.icebox.freshmate.domain.comment.application.dto.request.CommentUpdateReq;
import com.icebox.freshmate.domain.comment.application.dto.response.CommentRes;
import com.icebox.freshmate.domain.comment.application.dto.response.CommentsRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/comments")
@RestController
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<CommentRes> create(@Validated @RequestBody CommentCreateReq commentCreateReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		CommentRes commentRes = commentService.create(commentCreateReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(commentRes);
	}

	@GetMapping
	public ResponseEntity<CommentsRes> findAllByPostId(@RequestParam("post-id") Long postId) {
		CommentsRes commentsRes = commentService.findAllByPostId(postId);

		return ResponseEntity.ok(commentsRes);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<CommentRes> update(@PathVariable Long id, @Validated @RequestBody CommentUpdateReq commentUpdateReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		CommentRes commentRes = commentService.update(id, commentUpdateReq, principalDetails.getUsername());

		return ResponseEntity.ok(commentRes);
	}
}
