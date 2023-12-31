package com.icebox.freshmate.domain.grocery.application.dto.response;

import java.util.List;

import com.icebox.freshmate.domain.grocery.domain.Grocery;

public record GroceriesRes(
	List<GroceryRes> groceries
) {

	public static GroceriesRes from(List<Grocery> groceries) {

		List<GroceryRes> groceriesRes = groceries.stream()
			.map(GroceryRes::from)
			.toList();

		return new GroceriesRes(groceriesRes);
	}
}
