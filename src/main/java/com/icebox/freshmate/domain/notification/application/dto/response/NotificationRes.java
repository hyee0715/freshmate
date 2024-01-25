package com.icebox.freshmate.domain.notification.application.dto.response;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.notification.domain.Notification;

public record NotificationRes(
	Long notificationId,
	Long receiverId,
	String receiverNickName,
	String notificationContent,
	String relatedUrl,
	Boolean isRead,
	String notificationType,

	@JsonFormat(shape = STRING, pattern = "YYYY-MM-dd HH:mm", timezone = "Asia/Seoul")
	LocalDateTime createdAt,
	String eventId
) {

	public static NotificationRes of(Notification notification, Member receiver, String eventId) {

		return new NotificationRes(
			notification.getId(),
			receiver.getId(),
			receiver.getNickName(),
			notification.getNotificationContent().getContent(),
			notification.getRelatedUrl().getUrl(),
			notification.getIsRead(),
			notification.getNotificationType().name(),
			notification.getCreatedAt(),
			eventId
		);
	}
}
