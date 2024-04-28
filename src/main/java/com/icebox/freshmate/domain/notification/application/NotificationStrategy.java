package com.icebox.freshmate.domain.notification.application;

public interface NotificationStrategy {

	void notifyMessage(int days, Long memberId);
}
