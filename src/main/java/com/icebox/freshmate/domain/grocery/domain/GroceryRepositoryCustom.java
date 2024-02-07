package com.icebox.freshmate.domain.grocery.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GroceryRepositoryCustom {

	Slice<Grocery> findAllByStorageIdAndMemberIdOrderByNameAsc(Long storageId, Long memberId, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdOrderByNameDesc(Long storageId, Long memberId, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdOrderByUpdatedAtAsc(Long storageId, Long memberId, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdOrderByUpdatedAtDesc(Long storageId, Long memberId, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByNameAsc(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByNameDesc(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByUpdatedAtAsc(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByUpdatedAtDesc(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByExpirationDateAsc(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByExpirationDateDesc(Long storageId, Long memberId, GroceryExpirationType groceryExpirationType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByNameAsc(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByNameDesc(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByUpdatedAtAsc(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByUpdatedAtDesc(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByExpirationDateAsc(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByExpirationDateDesc(Long storageId, Long memberId, GroceryType groceryType, GroceryExpirationType groceryExpirationType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderByNameAsc(Long storageId, Long memberId, GroceryType groceryType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderByNameDesc(Long storageId, Long memberId, GroceryType groceryType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderByUpdatedAtAsc(Long storageId, Long memberId, GroceryType groceryType, Pageable pageable);

	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderByUpdatedAtDesc(Long storageId, Long memberId, GroceryType groceryType, Pageable pageable);
}
