package com.icebox.freshmate.domain.post.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.icebox.freshmate.domain.member.domain.Member;

public interface PostRepositoryCustom {

	Slice<Post> findAllByCondition(Member member, Pageable pageable, String sortBy, Long lastPageId);
}
