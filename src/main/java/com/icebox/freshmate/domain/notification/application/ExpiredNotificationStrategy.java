package com.icebox.freshmate.domain.notification.application;

import org.springframework.stereotype.Component;

import com.icebox.freshmate.domain.notification.application.dto.request.NotificationReq;
import com.icebox.freshmate.domain.notification.domain.NotificationType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpiredNotificationStrategy implements NotificationStrategy {

	private static final String EXPIRED_GROCERIES_MESSAGE = "유통기한으로부터 최대 %d일 지난 식료품이 있습니다. 어떤 식료품인지 확인해보세요!";

	private final NotificationEventPublisher notificationEventPublisher;

	@Override
	public void notifyMessage(int days, Long memberId) {
		String message = String.format(EXPIRED_GROCERIES_MESSAGE, days);

		notifyGroceryExpiration(memberId, message);
	}

	private void notifyGroceryExpiration(Long memberId, String message) {
		NotificationReq groceryNotificationReq = getGroceryNotificationReq(memberId, message);
		notificationEventPublisher.publishEvent(groceryNotificationReq);
	}

	private NotificationReq getGroceryNotificationReq(Long memberId, String message) {

		return new NotificationReq(memberId, NotificationType.EXPIRATION.name(), message, "");
	}
}
