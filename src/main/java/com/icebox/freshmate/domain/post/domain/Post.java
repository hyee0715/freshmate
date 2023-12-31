package com.icebox.freshmate.domain.post.domain;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.global.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Post extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(length = 200)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Builder
	public Post(Member member, String title, String content) {
		this.member = member;
		this.title = title;
		this.content = content;
	}

	public void update(Post post) {
		this.title = post.getTitle();
		this.content = post.getContent();
	}
}
