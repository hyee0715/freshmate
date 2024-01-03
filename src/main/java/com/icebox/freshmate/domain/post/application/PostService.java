package com.icebox.freshmate.domain.post.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.post.application.dto.request.PostReq;
import com.icebox.freshmate.domain.post.application.dto.response.PostRes;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.post.domain.PostRepository;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final MemberRepository memberRepository;
	private final PostRepository postRepository;

	public PostRes create(PostReq postReq, String username) {
		Member member = getMemberByUsername(username);

		Post post = PostReq.toPost(postReq, member);
		Post savedPost = postRepository.save(post);

		return PostRes.from(savedPost);
	}

	private Member getMemberByUsername(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}
}
