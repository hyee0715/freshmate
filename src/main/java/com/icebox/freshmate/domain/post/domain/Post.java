package com.icebox.freshmate.domain.post.domain;

import java.util.ArrayList;
import java.util.List;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.recipe.domain.Recipe;
import com.icebox.freshmate.global.BaseEntity;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recipe_id")
	private Recipe recipe;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostImage> postImages = new ArrayList<>();

	@Column(length = 200)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Builder
	public Post(Member member, Recipe recipe, String title, String content) {
		this.member = member;
		this.recipe = recipe;
		this.title = title;
		this.content = content;
	}

	public void update(Post post) {
		this.title = post.getTitle();
		this.content = post.getContent();
		this.recipe = post.getRecipe();
	}

	public void addPostImages(List<PostImage> postImages) {
		postImages.forEach(this::addPostImage);
	}

	public void addPostImage(PostImage postImage) {
		postImage.addPost(this);
		this.getPostImages().add(postImage);
	}

	public void removePostImage(PostImage postImage) {
		this.getPostImages().remove(postImage);
	}
}
