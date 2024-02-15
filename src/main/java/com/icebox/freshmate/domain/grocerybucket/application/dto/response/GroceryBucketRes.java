package com.icebox.freshmate.domain.grocerybucket.application.dto.response;

import java.time.LocalDateTime;

import com.icebox.freshmate.domain.grocerybucket.domain.GroceryBucket;

public record GroceryBucketRes(
	Long groceryBucketId,
	Long memberId,
	String memberNickName,
	String groceryName,
	String groceryType,
	String groceryDescription,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public static GroceryBucketRes from(GroceryBucket groceryBucket) {

		return new GroceryBucketRes(
			groceryBucket.getId(),
			groceryBucket.getMember().getId(),
			groceryBucket.getMember().getNickName(),
			groceryBucket.getGroceryName(),
			groceryBucket.getGroceryType().name(),
			groceryBucket.getGroceryDescription(),
			groceryBucket.getCreatedAt(),
			groceryBucket.getUpdatedAt()
		);
	}
}
