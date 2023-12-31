package com.icebox.freshmate.domain.comment.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_COMMENT;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_POST;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.comment.application.dto.request.CommentCreateReq;
import com.icebox.freshmate.domain.comment.application.dto.request.CommentUpdateReq;
import com.icebox.freshmate.domain.comment.application.dto.response.CommentRes;
import com.icebox.freshmate.domain.comment.application.dto.response.CommentsRes;
import com.icebox.freshmate.domain.comment.domain.Comment;
import com.icebox.freshmate.domain.comment.domain.CommentRepository;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.post.domain.PostRepository;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

	private final MemberRepository memberRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	public CommentRes create(CommentCreateReq commentCreateReq, String username) {
		Member member = getMemberByUsername(username);
		Post post = getPostById(commentCreateReq.postId());

		Comment comment = CommentCreateReq.toComment(commentCreateReq, post, member);
		Comment savedComment = commentRepository.save(comment);

		return CommentRes.from(savedComment);
	}

	@Transactional(readOnly = true)
	public CommentsRes findAllByPostId(Long postId) {
		List<Comment> comments = commentRepository.findAllByPostId(postId);

		return CommentsRes.from(comments);
	}

	public CommentRes update(Long commentId, CommentUpdateReq commentUpdateReq, String username) {
		Member member = getMemberByUsername(username);
		Comment comment = getCommentByIdAndMemberId(commentId, member.getId());
		Post post = getPostById(comment.getId());

		Comment updateComment = CommentUpdateReq.toComment(commentUpdateReq, post, member);
		comment.update(updateComment);

		return CommentRes.from(comment);
	}

	public void delete(Long commentId, String username) {
		Member member = getMemberByUsername(username);
		Comment comment = getCommentByIdAndMemberId(commentId, member.getId());

		commentRepository.delete(comment);
	}

	private Comment getCommentByIdAndMemberId(Long commentId, Long memberId) {
		return commentRepository.findByIdAndMemberId(commentId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_COMMENT_BY_ID_AND_MEMBER_ID : commentId = {}, memberId = {}", commentId, memberId);
				return new EntityNotFoundException(NOT_FOUND_COMMENT);
			});
	}

	private Post getPostById(Long postId) {
		return postRepository.findById(postId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_POST_BY_ID : {}", postId);
				return new EntityNotFoundException(NOT_FOUND_POST);
			});
	}

	private Member getMemberByUsername(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}
}
