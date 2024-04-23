package com.icebox.freshmate.domain.recipegrocery.application.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record RecipeGroceryReq(
	Long groceryId,

	@NotBlank(message = "레시피 재료 이름을 입력해주세요.")
	@Length(min = 1, max = 100, message = "레시피 재료 이름은 1자 이상 100자 이하로 등록 가능합니다.")
	String groceryName,

	@NotBlank(message = "레시피 재료 수량을 입력해주세요.")
	@Length(min = 1, max = 100, message = "레시피 재료 수량은 1자 이상 100자 이하로 등록 가능합니다.")
	String groceryQuantity
) {
}
