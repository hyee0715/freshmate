package com.icebox.freshmate.domain.comment.domain;

import java.util.ArrayList;
import java.util.List;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.post.domain.Post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommentImage> commentImages = new ArrayList<>();

	@Column(columnDefinition = "TEXT")
	private String content;

	@Builder
	public Comment(Post post, Member member, String content) {
		this.post = post;
		this.member = member;
		this.content = content;
	}

	public void update(Comment comment) {
		this.content = comment.getContent();
	}

	public void addCommentImages(List<CommentImage> commentImages) {
		commentImages.forEach(this::addCommentImage);
	}

	public void addCommentImage(CommentImage commentImage) {
		commentImage.addComment(this);
		this.getCommentImages().add(commentImage);
	}

	public void removeCommentImage(CommentImage commentImage) {
		this.getCommentImages().remove(commentImage);
	}
}
