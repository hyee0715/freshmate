package com.icebox.freshmate.domain.grocery.application;

import com.icebox.freshmate.domain.grocery.domain.GroceryExpirationType;

import lombok.Getter;

@Getter
public enum GroceryExpirationStrategyType {

	EXPIRED_GROCERY("expiredGroceryStrategy"),
	NOT_EXPIRED_GROCERY("notExpiredGroceryStrategy");

	private final String groceryExpirationStrategy;

	GroceryExpirationStrategyType(String groceryExpirationStrategy) {
		this.groceryExpirationStrategy = groceryExpirationStrategy;
	}

	public static String findGroceryExpirationStrategyType(GroceryExpirationType groceryExpirationType) {
		if (groceryExpirationType.equals(GroceryExpirationType.EXPIRED)) {

			return EXPIRED_GROCERY.getGroceryExpirationStrategy();
		}

		return NOT_EXPIRED_GROCERY.getGroceryExpirationStrategy();
	}
}
