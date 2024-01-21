package com.icebox.freshmate.domain.comment.domain;

import com.icebox.freshmate.domain.image.domain.Image;

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

@Getter
@Entity
@Table(name = "comment_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentImage extends Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id")
	private Comment comment;

	private String path;

	@Builder
	public CommentImage(String fileName, Comment comment, String path) {
		super(fileName);
		this.comment = comment;
		this.path = path;
	}

	public void addComment(Comment comment) {
		this.comment = comment;
	}
}
