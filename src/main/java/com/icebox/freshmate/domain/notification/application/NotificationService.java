package com.icebox.freshmate.domain.notification.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.notification.application.dto.request.NotificationReq;
import com.icebox.freshmate.domain.notification.application.dto.response.NotificationRes;
import com.icebox.freshmate.domain.notification.domain.EmitterRepository;
import com.icebox.freshmate.domain.notification.domain.Notification;
import com.icebox.freshmate.domain.notification.domain.NotificationRepository;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

	private final EmitterRepository emitterRepository;
	private final NotificationRepository notificationRepository;
	private final MemberRepository memberRepository;

	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

	public SseEmitter subscribe(Long memberId, String lastEventId) {
		String emitterId = makeTimeIncludeId(memberId);
		SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

		emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
		emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

		String eventId = makeTimeIncludeId(memberId);
		sendNotification(emitter, eventId, emitterId, "EventStream Created. [memberId = " + memberId + ", eventId = " + eventId + "]");

		if (hasLostData(lastEventId)) {
			sendLostData(lastEventId, memberId, emitterId, emitter);
		}

		return emitter;
	}

	public void send(NotificationReq notificationReq) {
		Member receiver = getMemberById(notificationReq.receiverId());
		Notification notification = saveNotification(notificationReq, receiver);

		String receiverId = String.valueOf(receiver.getId());
		String eventId = receiverId + "_" + System.currentTimeMillis();

		Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverId);

		emitters.forEach(
			(key, emitter) -> {
				emitterRepository.saveEventCache(key, notification);

				NotificationRes notificationRes = NotificationRes.of(notification, receiver, eventId);
				sendNotification(emitter, eventId, key, notificationRes);
			}
		);
	}

	private Notification saveNotification(NotificationReq notificationReq, Member receiver) {
		Notification notification = NotificationReq.toNotification(notificationReq, receiver);

		return notificationRepository.save(notification);
	}

	private Member getMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_ID : {}", memberId);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}

	private String makeTimeIncludeId(Long memberId) {
		return memberId + "_" + System.currentTimeMillis();
	}

	private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
		try {
			emitter.send(SseEmitter.event()
				.id(eventId)
				.data(data));
		} catch (IOException exception) {
			emitterRepository.deleteById(emitterId);
		}
	}

	private boolean hasLostData(String lastEventId) {
		return !lastEventId.isEmpty();
	}

	private void sendLostData(String lastEventId, Long memberId, String emitterId, SseEmitter emitter) {
		Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
		eventCaches.entrySet().stream()
			.filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
			.forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
	}
}
