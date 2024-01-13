package com.icebox.freshmate.domain.grocerybucket.application.dto.response;

import java.util.List;

import com.icebox.freshmate.domain.grocerybucket.domain.GroceryBucket;

public record GroceryBucketsRes(
	List<GroceryBucketRes> groceryBuckets
) {

	public static GroceryBucketsRes from(List<GroceryBucket> groceryBuckets) {
		List<GroceryBucketRes> groceryBucketRes = groceryBuckets.stream()
			.map(GroceryBucketRes::from)
			.toList();

		return new GroceryBucketsRes(groceryBucketRes);
	}
}
