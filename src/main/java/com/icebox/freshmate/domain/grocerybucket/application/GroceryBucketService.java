package com.icebox.freshmate.domain.grocerybucket.application;

import static com.icebox.freshmate.global.error.ErrorCode.INVALID_GROCERY_BUCKET_SORT_TYPE;
import static com.icebox.freshmate.global.error.ErrorCode.INVALID_LAST_PAGE_UPDATED_AT_FORMAT;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_GROCERY_BUCKET;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.grocerybucket.application.dto.request.GroceryBucketReq;
import com.icebox.freshmate.domain.grocerybucket.application.dto.response.GroceryBucketRes;
import com.icebox.freshmate.domain.grocerybucket.application.dto.response.GroceryBucketsRes;
import com.icebox.freshmate.domain.grocerybucket.domain.GroceryBucket;
import com.icebox.freshmate.domain.grocerybucket.domain.GroceryBucketRepository;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.global.error.exception.BusinessException;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GroceryBucketService {

	private final GroceryBucketRepository groceryBucketRepository;
	private final MemberRepository memberRepository;

	public GroceryBucketRes create(GroceryBucketReq groceryBucketReq, String username) {
		Member member = getMemberByUsername(username);

		GroceryBucket groceryBucket = GroceryBucketReq.toGroceryBucket(groceryBucketReq, member);
		GroceryBucket savedGroceryBucket = groceryBucketRepository.save(groceryBucket);

		return GroceryBucketRes.from(savedGroceryBucket);
	}

	@Transactional(readOnly = true)
	public GroceryBucketRes findById(Long id) {
		GroceryBucket groceryBucket = getGroceryBucketById(id);

		return GroceryBucketRes.from(groceryBucket);
	}

	@Transactional(readOnly = true)
	public GroceryBucketsRes findAll(String sortBy, Pageable pageable, String lastPageName, String lastPageUpdatedAt, String username) {
		Member member = getMemberByUsername(username);

		LocalDateTime lastUpdatedAt = getLastPageUpdatedAt(lastPageUpdatedAt);
		validateGroceryBucketSortType(sortBy);

		Slice<GroceryBucket> groceryBuckets = groceryBucketRepository.findAllByMemberId(member.getId(), pageable, sortBy, lastPageName, lastUpdatedAt);

		return GroceryBucketsRes.from(groceryBuckets);
	}

	public GroceryBucketRes update(Long id, GroceryBucketReq groceryBucketReq, String username) {
		Member member = getMemberByUsername(username);
		GroceryBucket groceryBucket = getGroceryBucketByIdAndMemberId(id, member.getId());

		GroceryBucket updateGroceryBucket = GroceryBucketReq.toGroceryBucket(groceryBucketReq, member);
		groceryBucket.update(updateGroceryBucket);

		return GroceryBucketRes.from(groceryBucket);
	}

	public void delete(Long id, String username) {
		Member member = getMemberByUsername(username);
		GroceryBucket groceryBucket = getGroceryBucketByIdAndMemberId(id, member.getId());

		groceryBucketRepository.delete(groceryBucket);
	}

	private Member getMemberByUsername(String username) {

		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);

				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}

	private GroceryBucket getGroceryBucketById(Long groceryId) {

		return groceryBucketRepository.findById(groceryId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_GROCERY_BUCKET_BY_ID : {}", groceryId);

				return new EntityNotFoundException(NOT_FOUND_GROCERY_BUCKET);
			});
	}

	private GroceryBucket getGroceryBucketByIdAndMemberId(Long groceryId, Long memberId) {

		return groceryBucketRepository.findByIdAndMemberId(groceryId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_GROCERY_BUCKET_BY_ID_AND_MEMBER_ID : groceryId = {}, memberId = {}", groceryId, memberId);

				return new EntityNotFoundException(NOT_FOUND_GROCERY_BUCKET);
			});
	}

	private void validateGroceryBucketSortType(String sortBy) {
		if (!sortBy.equalsIgnoreCase("nameAsc") && !sortBy.equalsIgnoreCase("nameDesc") && !sortBy.equalsIgnoreCase("updatedAtAsc") && !sortBy.equalsIgnoreCase("updatedAtDesc")) {
			log.warn("GET:READ:INVALID_GROCERY_BUCKET_SORT_TYPE : {}", sortBy);

			throw new BusinessException(INVALID_GROCERY_BUCKET_SORT_TYPE);
		}
	}

	private LocalDateTime getLastPageUpdatedAt(String lastPageUpdatedAt) {
		return Optional.ofNullable(lastPageUpdatedAt)
			.map(date -> {
				if (checkLocalDateTimeFormat(date)) {
					date += "0";
				}
				return date;
			})
			.map(date -> {
				validateLastPageUpdatedAtFormat(date);
				return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
			})
			.orElse(null);
	}

	private boolean checkLocalDateTimeFormat(String date) {
		String pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{5}";

		return Pattern.matches(pattern, date);
	}

	private void validateLastPageUpdatedAtFormat(String lastPageUpdatedAt) {
		String pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}";

		if (!Pattern.matches(pattern, lastPageUpdatedAt)) {
			log.warn("GET:READ:INVALID_LAST_PAGE_UPDATED_AT_FORMAT : {}", lastPageUpdatedAt);

			throw new BusinessException(INVALID_LAST_PAGE_UPDATED_AT_FORMAT);
		}
	}
}
