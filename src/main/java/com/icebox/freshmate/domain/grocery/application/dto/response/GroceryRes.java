package com.icebox.freshmate.domain.grocery.application.dto.response;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;

public record GroceryRes(
	Long groceryId,
	String groceryName,
	String groceryType,
	String quantity,
	String description,

	@JsonFormat(shape = STRING, pattern = "YYYY-MM-dd", timezone = "Asia/Seoul")
	LocalDate expirationDate,

	Long storageId,
	String storageName,
	String groceryExpirationType,

	@JsonFormat(shape = STRING, pattern = "YYYY-MM-dd HH:mm", timezone = "Asia/Seoul")
	LocalDateTime createdAt,
	List<ImageRes> images
) {

	public static GroceryRes of(Grocery grocery, List<ImageRes> imagesRes) {

		return new GroceryRes(
			grocery.getId(),
			grocery.getName(),
			grocery.getGroceryType().name(),
			grocery.getQuantity(),
			grocery.getDescription(),
			grocery.getExpirationDate(),
			grocery.getStorage().getId(),
			grocery.getStorage().getName(),
			grocery.getGroceryExpirationType().name(),
			grocery.getCreatedAt(),
			imagesRes
		);
	}
}
