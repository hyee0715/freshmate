package com.icebox.freshmate.domain.grocerybucket.application.dto.request;

import org.hibernate.validator.constraints.Length;

import com.icebox.freshmate.domain.grocery.domain.GroceryType;
import com.icebox.freshmate.domain.grocerybucket.domain.GroceryBucket;
import com.icebox.freshmate.domain.member.domain.Member;

import jakarta.validation.constraints.NotBlank;

public record GroceryBucketReq(

	@NotBlank(message = "식료품 이름을 입력해주세요.")
	@Length(min = 1, max = 50, message = "식료품 이름은 1자 이상 50자 이하로 등록 가능합니다.")
	String groceryName,

	String groceryType,

	@Length(min = 1, max = 400, message = "식료품 설명은 1자 이상 400자 이하로 등록 가능합니다.")
	String groceryDescription
) {

	public static GroceryBucket toGroceryBucket(GroceryBucketReq groceryBucketReq, Member member) {
		GroceryType foundGroceryType = GroceryType.findGroceryType(groceryBucketReq.groceryType());

		return GroceryBucket.builder()
			.member(member)
			.groceryName(groceryBucketReq.groceryName())
			.groceryType(foundGroceryType)
			.groceryDescription(groceryBucketReq.groceryDescription())
			.build();
	}
}
