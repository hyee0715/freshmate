package com.icebox.freshmate.domain.grocery.domain;

import java.time.LocalDate;

public enum GroceryExpirationType {
	EXPIRED, NOT_EXPIRED, NOT_APPLICABLE;

	public static GroceryExpirationType checkExpiration(LocalDate expirationDate, LocalDate currentDate) {
		if (expirationDate == null) {
			return NOT_APPLICABLE;
		}

		if (expirationDate.isBefore(currentDate)) {
			return EXPIRED;
		}

		return NOT_EXPIRED;
	}

	public static GroceryExpirationType[] getGroceryExpirationSequence() {

		return new GroceryExpirationType[] {GroceryExpirationType.NOT_EXPIRED, GroceryExpirationType.EXPIRED};
	}
}
