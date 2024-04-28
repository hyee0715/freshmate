package com.icebox.freshmate.domain.notification.application;

import lombok.Getter;

@Getter
public enum NotificationStrategyType {

	EXPIRED_NOTIFICATION("expiredNotificationStrategy"),
	EXPIRED_TODAY_NOTIFICATION("expiredTodayNotificationStrategy"),
	NOT_EXPIRED_NOTIFICATION("notExpiredNotificationStrategy");

	private final String notificationStrategy;

	NotificationStrategyType(String notificationStrategy) {
		this.notificationStrategy = notificationStrategy;
	}

	public static String findNotificationStrategyType(int expirationDate) {
		if (expirationDate < 0) {
			return NOT_EXPIRED_NOTIFICATION.getNotificationStrategy();
		}

		if (expirationDate == 0) {
			return EXPIRED_TODAY_NOTIFICATION.getNotificationStrategy();
		}

		return EXPIRED_NOTIFICATION.getNotificationStrategy();
	}
}
