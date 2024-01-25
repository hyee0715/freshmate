package com.icebox.freshmate.domain.notification.domain;

import java.util.Arrays;

import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum NotificationType {
	EXPIRATION, COMMENT;

	public static NotificationType findNotificationType(String notificationType) {

		return Arrays.stream(NotificationType.values())
			.filter(type -> type.name().equalsIgnoreCase(notificationType))
			.findAny()
			.orElseThrow(() -> {
				log.error("INVALID_NOTIFICATION_TYPE : {}", notificationType);

				return new BusinessException(ErrorCode.INVALID_NOTIFICATION_TYPE);
			});
	}
}
