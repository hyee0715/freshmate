package com.icebox.freshmate.domain.grocery.domain;

import static com.icebox.freshmate.domain.grocery.domain.GroceryExpirationType.checkExpiration;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.icebox.freshmate.domain.recipegrocery.domain.RecipeGrocery;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.global.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "groceries", indexes = {
	@Index(name = "index_groceries_name_updated_at", columnList = "name, updatedAt", unique = true),
	@Index(name = "index_groceries_expiration_date_updated_at", columnList = "expirationDate, updatedAt", unique = true),
	@Index(name = "index_groceries_updated_at", columnList = "updatedAt", unique = true)})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Grocery extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "storage_id")
	private Storage storage;

	@OneToMany(mappedBy = "grocery", cascade = CascadeType.ALL)
	private List<RecipeGrocery> recipeGroceries = new ArrayList<>();

	@OneToMany(mappedBy = "grocery", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GroceryImage> groceryImages = new ArrayList<>();

	@Column(length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	private GroceryType groceryType;

	@Column(length = 100)
	private String quantity;

	@Column(length = 400)
	private String description;

	private LocalDate expirationDate;

	@Enumerated(EnumType.STRING)
	private GroceryExpirationType groceryExpirationType;

	@Builder
	public Grocery(Storage storage, String name, GroceryType groceryType, String quantity, String description, LocalDate expirationDate) {
		this.storage = storage;
		this.name = name;
		this.groceryType = groceryType;
		this.quantity = quantity;
		this.description = description;
		this.expirationDate = expirationDate;
		this.groceryExpirationType = checkExpiration(expirationDate, LocalDate.now());
	}

	public void update(Grocery grocery) {
		this.storage = grocery.getStorage();
		this.name = grocery.getName();
		this.groceryType = grocery.getGroceryType();
		this.quantity = grocery.getQuantity();
		this.description = grocery.getDescription();
		this.expirationDate = grocery.getExpirationDate();
		this.groceryExpirationType = checkExpiration(grocery.getExpirationDate(), LocalDate.now());
	}

	public void updateGroceryExpirationType() {
		this.groceryExpirationType = checkExpiration(expirationDate, LocalDate.now());
	}

	public int calculateExpirationDateFromCurrentDate(LocalDate currentDate) {

		return (int) ChronoUnit.DAYS.between(this.expirationDate, currentDate);
	}

	public void addRecipeGrocery(RecipeGrocery recipeGrocery) {
		if (recipeGrocery.getGrocery() != null) {
			recipeGrocery.addGrocery(this);
			this.getRecipeGroceries().add(recipeGrocery);
		}
	}

	public void removeRecipeGrocery(RecipeGrocery recipeGrocery) {
		this.getRecipeGroceries().remove(recipeGrocery);
	}

	public void addGroceryImages(List<GroceryImage> groceryImages) {
		groceryImages.forEach(this::addGroceryImage);
	}

	public void addGroceryImage(GroceryImage groceryImage) {
		groceryImage.addGrocery(this);
		this.getGroceryImages().add(groceryImage);
	}

	public void removeGroceryImage(GroceryImage groceryImage) {
		this.getGroceryImages().remove(groceryImage);
	}
}
