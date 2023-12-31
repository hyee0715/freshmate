package com.icebox.freshmate.domain.grocery.application;

import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_GROCERY;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_STORAGE;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icebox.freshmate.domain.grocery.application.dto.request.GroceryReq;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceriesRes;
import com.icebox.freshmate.domain.grocery.application.dto.response.GroceryRes;
import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryRepository;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageRepository;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GroceryService {

	private final GroceryRepository groceryRepository;
	private final MemberRepository memberRepository;
	private final StorageRepository storageRepository;

	public GroceryRes create(GroceryReq groceryReq, String username) {
		Member member = getMember(username);

		Storage storage = getStorageByIdAndMemberId(groceryReq.storageId(), member.getId());

		Grocery grocery = GroceryReq.toGrocery(groceryReq, storage);
		Grocery savedGrocery = groceryRepository.save(grocery);

		return GroceryRes.from(savedGrocery);
	}

	@Transactional(readOnly = true)
	public GroceryRes findById(Long id) {
		Grocery grocery = getGroceryById(id);

		return GroceryRes.from(grocery);
	}

	@Transactional(readOnly = true)
	public GroceriesRes findAllByStorageId(Long storageId, String username) {
		Member member = getMember(username);

		List<Grocery> groceries = groceryRepository.findAllByStorageIdAndMemberId(storageId, member.getId());

		return GroceriesRes.from(groceries);
	}

	public GroceryRes update(Long id, GroceryReq groceryReq, String username) {
		Member member = getMember(username);
		Storage storage = getStorageByIdAndMemberId(groceryReq.storageId(), member.getId());
		Grocery grocery = getGroceryByIdAndMemberId(id, member.getId());

		Grocery updateGrocery = GroceryReq.toGrocery(groceryReq, storage);
		grocery.update(updateGrocery);

		return GroceryRes.from(grocery);
	}

	public void delete(Long id, String username) {
		Member member = getMember(username);
		Grocery grocery = getGroceryByIdAndMemberId(id, member.getId());

		groceryRepository.delete(grocery);
	}

	private Grocery getGroceryByIdAndMemberId(Long groceryId, Long memberId) {
		return groceryRepository.findByIdAndMemberId(groceryId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORAGE_BY_ID_AND_MEMBER_ID : groceryId = {}, memberId = {}", groceryId, memberId);
				return new EntityNotFoundException(NOT_FOUND_GROCERY);
			});
	}

	private Grocery getGroceryById(Long id) {
		return groceryRepository.findById(id)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORAGE_BY_ID : {}", id);
				return new EntityNotFoundException(NOT_FOUND_GROCERY);
			});
	}

	private Storage getStorageByIdAndMemberId(Long storageId, Long memberId) {
		return storageRepository.findByIdAndMemberId(storageId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORAGE_BY_ID_AND_MEMBER_ID : storageId = {}, memberId = {}", storageId, memberId);
				return new EntityNotFoundException(NOT_FOUND_STORAGE);
			});
	}

	private Member getMember(String username) {
		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_STORE_BY_MEMBER_USERNAME : {}", username);
				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}
}
