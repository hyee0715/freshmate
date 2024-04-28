package com.icebox.freshmate.domain.grocery.application;

import static com.icebox.freshmate.domain.notification.application.NotificationStrategyType.findNotificationStrategyType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryRepository;
import com.icebox.freshmate.domain.notification.application.NotificationStrategy;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpiredGroceryStrategy implements GroceryExpirationStrategy {

	private final GroceryRepository groceryRepository;
	private final Map<String, NotificationStrategy> expirationNotificationMap;

	@Override
	public void handleGrocery(LocalDate currentDate) {
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
