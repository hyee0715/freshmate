package com.icebox.freshmate.domain.scheduler.application;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SchedulerService {

	private static final String GROCERY_EXPIRATION_UPDATE_PERIOD = "0 10 0 * * *"; //매일 0시 10분에 실행

	private final GroceryRepository groceryRepository;

	@Scheduled(cron = GROCERY_EXPIRATION_UPDATE_PERIOD)
	public void updateGroceryExpiration() {
		List<Grocery> expiredGroceries = groceryRepository.findAllNotExpiredBeforeCurrentDate(LocalDate.now());

		expiredGroceries.forEach(Grocery::updateGroceryExpirationType);
	}
}
