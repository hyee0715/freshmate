package com.icebox.freshmate.domain.grocery.application.dto.response;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icebox.freshmate.domain.grocery.domain.Grocery;

public record GroceryRes(
	Long groceryId,
	String groceryName,
	String groceryType,
	int quantity,
	String description,

	@JsonFormat(shape = STRING, pattern = "YYYY-MM-dd", timezone = "Asia/Seoul")
	LocalDate expirationDate,

	Long storageId,
	String storageName
) {

	public static GroceryRes from(Grocery grocery) {

		return new GroceryRes(
			grocery.getId(),
			grocery.getName(),
			grocery.getGroceryType().name(),
			grocery.getQuantity(),
			grocery.getDescription(),
			grocery.getExpirationDate(),
			grocery.getStorage().getId(),
			grocery.getStorage().getName()
		);
	}
}
