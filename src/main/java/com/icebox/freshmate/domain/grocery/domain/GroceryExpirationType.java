package com.icebox.freshmate.domain.grocery.domain;

import java.time.LocalDate;
import java.util.Arrays;

import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum GroceryExpirationType {

	EXPIRED("expired"),
	NOT_EXPIRED("notExpired"),
	NOT_APPLICABLE("notApplicable");

	String expirationType;

	GroceryExpirationType(String expirationType) {
		this.expirationType = expirationType;
	}

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

	public static GroceryExpirationType findGroceryExpirationType(String groceryExpirationType) {

		return Arrays.stream(GroceryExpirationType.values())
			.filter(type -> type.name().equalsIgnoreCase(groceryExpirationType) || type.getExpirationType().equalsIgnoreCase(groceryExpirationType))
			.findAny()
			.orElseThrow(() -> {
				log.error("INVALID_GROCERY_EXPIRATION_TYPE = {}", groceryExpirationType);

				return new BusinessException(ErrorCode.INVALID_GROCERY_EXPIRATION_TYPE);
			});
	}
}
