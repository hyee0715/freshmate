package com.icebox.freshmate.domain.comment.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
import com.icebox.freshmate.domain.comment.application.CommentService;
import com.icebox.freshmate.domain.comment.application.dto.request.CommentReq;
import com.icebox.freshmate.domain.comment.application.dto.response.CommentRes;
import com.icebox.freshmate.domain.comment.application.dto.response.CommentsRes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/comments")
@RestController
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<CommentRes> create(@Validated @RequestBody CommentReq commentReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		CommentRes commentRes = commentService.create(commentReq, principalDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(commentRes);
	}

	@GetMapping
	public ResponseEntity<CommentsRes> findAllByPostId(@RequestParam("post-id") Long postId) {
		CommentsRes commentsRes = commentService.findAllByPostId(postId);

		return ResponseEntity.ok(commentsRes);
	}
}
