package com.icebox.freshmate.domain.grocery.application.dto.request;

import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.storage.domain.Storage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GroceryReq(
	@NotBlank(message = "식료품 이름을 입력해주세요.")
	@Length(min = 1, max = 50, message = "식료품 이름은 1자 이상 50자 이하로 등록 가능합니다.")
	String name,

	String groceryType,

	@Min(value = 1, message = "식료품 수량은 1개 이상부터 등록 가능합니다.")
	int quantity,

	@Length(min = 1, max = 400, message = "식료품 설명은 1자 이상 400자 이하로 등록 가능합니다.")
	String description,

	LocalDate expirationDate,
	Long storageId
) {

	public static Grocery toGrocery(GroceryReq groceryReq, Storage storage) {
		GroceryType foundGroceryType = GroceryType.findGroceryType(groceryReq.groceryType());

		return Grocery.builder()
			.storage(storage)
			.name(groceryReq.name())
			.groceryType(foundGroceryType)
			.quantity(groceryReq.quantity())
			.description(groceryReq.description())
			.expirationDate(groceryReq.expirationDate())
			.build();
	}
}
