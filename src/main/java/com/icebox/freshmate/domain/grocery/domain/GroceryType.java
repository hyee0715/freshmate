package com.icebox.freshmate.domain.grocery.domain;

import java.util.Arrays;

import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.InvalidValueException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum GroceryType {

	GRAINS("곡물류"),
	VEGETABLES("채소류"),
	FRUITS("과일류"),
	MEAT("육류"),
	SEAFOOD("어패류"),
	DAIRY_PRODUCTS("유제품"),
	SAUCES("양념 및 소스류"),
	BEVERAGES("음료수"),
	SNACKS("간식 및 과자류"),
	CONVENIENCE_FOODS("즉석식품"),
	ETC("기타");

	String meaning;

	GroceryType(String meaning) {
		this.meaning = meaning;
	}

	public static GroceryType findGroceryType(String groceryType) {

		return Arrays.stream(GroceryType.values())
			.filter(type -> type.name().equalsIgnoreCase(groceryType))
			.findAny()
			.orElseThrow(() -> {
				log.error("INVALID_GROCERY_TYPE : {}", groceryType);

				return new InvalidValueException(ErrorCode.INVALID_GROCERY_TYPE);
			});
	}
}
