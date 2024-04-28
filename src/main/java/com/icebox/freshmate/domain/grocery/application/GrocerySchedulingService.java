package com.icebox.freshmate.domain.grocery.application;

import static com.icebox.freshmate.domain.grocery.application.GroceryExpirationStrategyType.findGroceryExpirationStrategyType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryExpirationType;
import com.icebox.freshmate.domain.grocery.domain.GroceryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class GrocerySchedulingService {

	private static final String GROCERY_EXPIRATION_UPDATE_PERIOD = "0 10 0 * * *"; //매일 0시 10분에 실행
	private static final String GROCERY_NOTIFICATION_PERIOD = "0 0 18 * * *"; //매일 18시에 실행

	private final GroceryRepository groceryRepository;
	private final Map<String, GroceryExpirationStrategy> groceryExpirationMap;

	@Async
	@Scheduled(cron = GROCERY_EXPIRATION_UPDATE_PERIOD)
	public void updateGroceryExpiration() {
		List<Grocery> expiredGroceries = groceryRepository.findAllNotExpiredBeforeCurrentDate(LocalDate.now());

		expiredGroceries.forEach(Grocery::updateGroceryExpirationType);
	}

	@Async
	@Scheduled(cron = GROCERY_NOTIFICATION_PERIOD)
	public void checkGroceryExpiration() {
		GroceryExpirationType[] groceryExpirationSequence = GroceryExpirationType.getGroceryExpirationSequence();

		Arrays.stream(groceryExpirationSequence)
			.forEach(this::getExpirationInformation);
	}

	private void getExpirationInformation(GroceryExpirationType groceryExpirationType) {
		LocalDate currentDate = LocalDate.now();

		String groceryExpirationStrategyType = findGroceryExpirationStrategyType(groceryExpirationType);

		GroceryExpirationStrategy groceryExpirationStrategy = groceryExpirationMap.get(groceryExpirationStrategyType);
		groceryExpirationStrategy.handleGrocery(currentDate);
	}
}
