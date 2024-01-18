package com.icebox.freshmate.domain.grocery.domain;

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
@Table(name = "grocery_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroceryImage extends Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grocery_id")
	private Grocery grocery;

	private String path;

	@Builder
	public GroceryImage(String fileName, Grocery grocery, String path) {
		super(fileName);
		this.grocery = grocery;
		this.path = path;
	}

	public void addGrocery(Grocery grocery) {
		this.grocery = grocery;
	}
}
