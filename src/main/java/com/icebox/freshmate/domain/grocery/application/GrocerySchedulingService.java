package com.icebox.freshmate.domain.grocery.application;

import static com.icebox.freshmate.domain.notification.application.NotificationStrategyType.findNotificationStrategyType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryExpirationType;
import com.icebox.freshmate.domain.grocery.domain.GroceryRepository;
import com.icebox.freshmate.domain.notification.application.NotificationStrategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class GrocerySchedulingService {

	private static final String GROCERY_EXPIRATION_UPDATE_PERIOD = "0 10 0 * * *"; //매일 0시 10분에 실행
	private static final String GROCERY_NOTIFICATION_PERIOD = "0 0 18 * * *"; //매일 18시에 실행

	private final GroceryRepository groceryRepository;
	private final Map<GroceryExpirationType, Consumer<LocalDate>> expirationTypeMap;
	private final Map<String, NotificationStrategy> expirationNotificationMap;

	public GrocerySchedulingService(GroceryRepository groceryRepository, Map<GroceryExpirationType, Consumer<LocalDate>> expirationTypeMap, Map<String, NotificationStrategy> expirationNotificationMap) {
		this.groceryRepository = groceryRepository;
		this.expirationTypeMap = initializeExpirationTypeMap();
		this.expirationNotificationMap = expirationNotificationMap;
	}

	@Scheduled(cron = GROCERY_EXPIRATION_UPDATE_PERIOD)
	public void updateGroceryExpiration() {
		List<Grocery> expiredGroceries = groceryRepository.findAllNotExpiredBeforeCurrentDate(LocalDate.now());

		expiredGroceries.forEach(Grocery::updateGroceryExpirationType);
	}

	@Scheduled(cron = GROCERY_NOTIFICATION_PERIOD)
	public void checkGroceryExpiration() {
		GroceryExpirationType[] groceryExpirationSequence = GroceryExpirationType.getGroceryExpirationSequence();

		Arrays.stream(groceryExpirationSequence)
			.forEach(this::getExpirationInformation);
	}

	private Map<GroceryExpirationType, Consumer<LocalDate>> initializeExpirationTypeMap() {

		return Map.of(
			GroceryExpirationType.NOT_EXPIRED, this::handleNotExpired,
			GroceryExpirationType.EXPIRED, this::handleExpired
		);
	}

	private void getExpirationInformation(GroceryExpirationType groceryExpirationType) {
		LocalDate currentDate = LocalDate.now();

		expirationTypeMap.get(groceryExpirationType)
			.accept(currentDate);
	}

	private void handleNotExpired(LocalDate currentDate) {
		List<Grocery> allWithExpirationDate10DaysEarlier = groceryRepository.findAllWithExpirationDate10DaysEarlier(currentDate);
		checkExpirationDate(allWithExpirationDate10DaysEarlier, currentDate);

		List<Grocery> allWithExpirationDateIsToday = groceryRepository.findAllWithExpirationDateIsToday(currentDate);
		checkExpirationDate(allWithExpirationDateIsToday, currentDate);
	}

	private void handleExpired(LocalDate currentDate) {
		List<Grocery> allWithExpirationDate10DaysLater = groceryRepository.findAllWithExpirationDate20DaysLater(currentDate);
		checkExpirationDate(allWithExpirationDate10DaysLater, currentDate);
	}

	private void checkExpirationDate(List<Grocery> groceries, LocalDate currentDate) {

		divideGroceriesByMemberId(groceries)
			.forEach((memberId, groceriesOfMember) -> {
				int expirationDate = groceriesOfMember.get(0).calculateExpirationDateFromCurrentDate(currentDate);
				notifyExpirationInformation(expirationDate, memberId);
			});
	}

	private Map<Long, List<Grocery>> divideGroceriesByMemberId(List<Grocery> groceries) {

		return groceries.stream()
			.collect(Collectors.groupingBy(
				grocery -> grocery.getStorage()
					.getRefrigerator()
					.getMember().getId(),
				Collectors.toList()
			));
	}

	private void notifyExpirationInformation(int expirationDate, Long memberId) {
		String notificationStrategyType = findNotificationStrategyType(expirationDate);

		NotificationStrategy notificationStrategy = expirationNotificationMap.get(notificationStrategyType);

		notificationStrategy.notifyMessage(expirationDate, memberId);
	}
}
