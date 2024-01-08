package com.icebox.freshmate.domain.grocery.domain;

import java.time.LocalDate;

import com.icebox.freshmate.domain.storage.domain.Storage;
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
@Table(name = "groceries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Grocery extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "storage_id")
	private Storage storage;

	@Column(length = 50)
	private String name;

	@Enumerated(EnumType.STRING)
	private GroceryType groceryType;

	private int quantity;

	@Column(length = 400)
	private String description;

	private LocalDate expirationDate;

	@Builder
	public Grocery(Storage storage, String name, GroceryType groceryType, int quantity, String description, LocalDate expirationDate) {
		this.storage = storage;
		this.name = name;
		this.groceryType = groceryType;
		this.quantity = quantity;
		this.description = description;
		this.expirationDate = expirationDate;
	}

	public void update(Grocery grocery) {
		this.storage = grocery.getStorage();
		this.name = grocery.getName();
		this.groceryType = grocery.getGroceryType();
		this.quantity = grocery.getQuantity();
		this.description = grocery.getDescription();
		this.expirationDate = grocery.getExpirationDate();
	}
}
