package com.icebox.freshmate.domain.notification.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.icebox.freshmate.domain.notification.application.dto.request.NotificationReq;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationEventPublisher {

	private final ApplicationEventPublisher eventPublisher;

	public void publishEvent(NotificationReq notificationReq) {
		eventPublisher.publishEvent(notificationReq);
	}
}
