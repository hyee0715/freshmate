package com.icebox.freshmate.domain.grocerybucket.application.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.icebox.freshmate.domain.grocerybucket.domain.GroceryBucket;

public record GroceryBucketsRes(
	List<GroceryBucketRes> groceryBuckets,
	boolean hasNext
) {

	public static GroceryBucketsRes from(Slice<GroceryBucket> groceryBuckets) {
		List<GroceryBucketRes> groceryBucketRes = groceryBuckets.stream()
			.map(GroceryBucketRes::from)
			.toList();

		return new GroceryBucketsRes(groceryBucketRes, groceryBuckets.hasNext());
	}
}
