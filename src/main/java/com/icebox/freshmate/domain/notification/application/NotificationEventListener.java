package com.icebox.freshmate.domain.notification.application;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.icebox.freshmate.domain.notification.application.dto.request.NotificationReq;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

	private final NotificationService notificationService;

	@TransactionalEventListener
	@Async
	public void handleNotification(NotificationReq notificationReq) {
		notificationService.send(notificationReq);
	}
}
