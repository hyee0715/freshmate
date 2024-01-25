package com.icebox.freshmate.domain.notification.domain;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.global.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Member receiver;

	@Embedded
	private NotificationContent notificationContent;

	@Embedded
	private RelatedUrl relatedUrl;

	@Column(nullable = false)
	private Boolean isRead;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType notificationType;

	@Builder
	public Notification(Member receiver, NotificationType notificationType, NotificationContent notificationContent, RelatedUrl relatedUrl, Boolean isRead) {
		this.receiver = receiver;
		this.notificationType = notificationType;
		this.notificationContent = notificationContent;
		this.relatedUrl = relatedUrl;
		this.isRead = isRead;
	}
}
