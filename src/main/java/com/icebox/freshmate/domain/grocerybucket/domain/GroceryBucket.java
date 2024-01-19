package com.icebox.freshmate.domain.grocerybucket.domain;

import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.global.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "grocery_buckets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class GroceryBucket extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(length = 50)
	private String groceryName;

	@Enumerated(EnumType.STRING)
	private GroceryType groceryType;

	@Column(length = 400)
	private String groceryDescription;

	@Builder
	public GroceryBucket(Member member, String groceryName, GroceryType groceryType, String groceryDescription) {
		this.member = member;
		this.groceryName = groceryName;
		this.groceryType = groceryType;
		this.groceryDescription = groceryDescription;
	}

	public void update(GroceryBucket groceryBucket) {
		this.groceryName = groceryBucket.getGroceryName();
		this.groceryType = groceryBucket.getGroceryType();
		this.groceryDescription = groceryBucket.getGroceryDescription();
	}
}
