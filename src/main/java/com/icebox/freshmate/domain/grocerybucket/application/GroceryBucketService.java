package com.icebox.freshmate.domain.grocerybucket.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_GROCERY_BUCKET;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.grocerybucket.application.dto.request.GroceryBucketReq;
import com.icebox.freshmate.domain.grocerybucket.application.dto.response.GroceryBucketRes;
import com.icebox.freshmate.domain.grocerybucket.application.dto.response.GroceryBucketsRes;
import com.icebox.freshmate.domain.grocerybucket.domain.GroceryBucket;
import com.icebox.freshmate.domain.grocerybucket.domain.GroceryBucketRepository;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
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
	public GroceryBucketsRes findAll(String username) {
		Member member = getMemberByUsername(username);

		List<GroceryBucket> groceryBuckets = groceryBucketRepository.findAllByMemberId(member.getId());

		return GroceryBucketsRes.from(groceryBuckets);
	}

	public GroceryBucketRes update(Long id, GroceryBucketReq groceryBucketReq, String username) {
		Member member = getMemberByUsername(username);
		GroceryBucket groceryBucket = getGroceryBucketByIdAndMemberId(id, member.getId());

		GroceryBucket updateGroceryBucket = GroceryBucketReq.toGroceryBucket(groceryBucketReq, member);
		groceryBucket.update(updateGroceryBucket);

		return GroceryBucketRes.from(groceryBucket);
	}

	private Member getMemberByUsername(String username) {

		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORE_BY_MEMBER_USERNAME : {}", username);
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
}
