package com.icebox.freshmate.domain.recipebucket.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record RecipeBucketReq(
	@NotNull(message = "레시피 ID를 입력해주세요.")
	Long recipeId
) {
}
