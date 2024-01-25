package com.icebox.freshmate.domain.notification.application.dto.request;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.notification.domain.Notification;
import com.icebox.freshmate.domain.notification.domain.NotificationContent;
import com.icebox.freshmate.domain.notification.domain.NotificationType;
import com.icebox.freshmate.domain.notification.domain.RelatedUrl;

public record NotificationReq(
	Long receiverId,
	String notificationType,
	String notificationContent,
	String relatedUrl
) {

	public static Notification toNotification(NotificationReq notificationReq, Member receiver) {
		NotificationContent notificationContent = new NotificationContent(notificationReq.notificationContent());
		RelatedUrl relatedUrl = new RelatedUrl(notificationReq.relatedUrl());
		NotificationType notificationType = NotificationType.findNotificationType(notificationReq.notificationType());

		return Notification.builder()
			.receiver(receiver)
			.notificationType(notificationType)
			.notificationContent(notificationContent)
			.relatedUrl(relatedUrl)
			.isRead(false)
			.build();
	}
}
