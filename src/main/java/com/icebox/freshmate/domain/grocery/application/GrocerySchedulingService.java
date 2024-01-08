package com.icebox.freshmate.domain.grocery.application;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryExpirationType;
import com.icebox.freshmate.domain.grocery.domain.GroceryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class GrocerySchedulingService {

	private static final String GROCERY_EXPIRATION_UPDATE_PERIOD = "0 10 0 * * *"; //매일 0시 10분에 실행
	private static final String GROCERY_NOTIFICATION_PERIOD = "0 0 18 * * *"; //매일 18시애 실행

	private static final String NOT_EXPIRED_GROCERIES_MESSAGE = "유통기한이 최소 {}일 남은 식료품이 있습니다. 어떤 식료품인지 확인해보세요!";
	private static final String EXPIRED_GROCERIES_MESSAGE = "유통기한으로부터 최대 {}일 지난 식료품이 있습니다. 어떤 식료품인지 확인해보세요!";
	private static final String TODAY_EXPIRATION_MESSAGE = "오늘이 유통기한 마감일인 식료품이 있습니다. 어떤 식료품인지 확인해보세요!";

	private final GroceryRepository groceryRepository;
	private final Map<GroceryExpirationType, Consumer<LocalDate>> expirationTypeStrategies;
	private final Map<Predicate<Integer>, Consumer<Integer>> expirationNotificationStrategies;

	public GrocerySchedulingService(GroceryRepository groceryRepository) {
		this.groceryRepository = groceryRepository;
		this.expirationTypeStrategies = initializeExpirationTypeStrategies();
		this.expirationNotificationStrategies = initializeExpirationStrategies();
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

	private Map<GroceryExpirationType, Consumer<LocalDate>> initializeExpirationTypeStrategies() {
		return Map.of(
			GroceryExpirationType.NOT_EXPIRED, this::handleNotExpired,
			GroceryExpirationType.EXPIRED, this::handleExpired
		);
	}

	private void getExpirationInformation(GroceryExpirationType groceryExpirationType) {
		LocalDate currentDate = LocalDate.now();

		expirationTypeStrategies.get(groceryExpirationType)
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
		if (!groceries.isEmpty()) {
			Grocery grocery = groceries.get(0);
			int expirationDate = grocery.calculateExpirationDateFromCurrentDate(currentDate);

			notifyExpirationDate(expirationDate);
		}
	}

	private void notifyExpirationDate(int expirationDate) {

		expirationNotificationStrategies.entrySet().stream()
			.filter(entry -> entry.getKey().test(expirationDate))
			.findFirst()
			.map(Map.Entry::getValue)
			.ifPresent(strategy -> strategy.accept(expirationDate));
	}

	private Map<Predicate<Integer>, Consumer<Integer>> initializeExpirationStrategies() {
		return Map.of(
			days -> days < 0, this::notifyNotExpiredGroceries,
			days -> days == 0, this::notifyTodayExpirationGroceries,
			days -> days > 0, this::notifyExpiredGroceries
		);
	}

	private void notifyNotExpiredGroceries(int days) {

		log.info(NOT_EXPIRED_GROCERIES_MESSAGE, Math.abs(days));
	}

	private void notifyTodayExpirationGroceries(int days) {

		log.info(TODAY_EXPIRATION_MESSAGE);
	}

	private void notifyExpiredGroceries(int days) {

		log.info(EXPIRED_GROCERIES_MESSAGE, days);
	}
}
