package com.icebox.freshmate.domain.grocery.application;

import java.time.LocalDate;

public interface GroceryExpirationStrategy {

	void handleGrocery(LocalDate currentDate);
}
