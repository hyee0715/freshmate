package com.icebox.freshmate.domain.refrigerator.domain;

import java.util.Arrays;

import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum RefrigeratorSortType {

	NAME_ASC("nameAsc"),
	NAME_DESC("nameDesc"),
	UPDATED_AT_ASC("updatedAtAsc"),
	UPDATED_AT_DESC("updatedAtDesc");

	String sortType;

	RefrigeratorSortType(String sortType) {
		this.sortType = sortType;
	}

	public static RefrigeratorSortType findRefrigeratorSortType(String refrigeratorSortType) {

		return Arrays.stream(RefrigeratorSortType.values())
			.filter(type -> type.getSortType().equalsIgnoreCase(refrigeratorSortType))
			.findAny()
			.orElseThrow(() -> {
				log.error("INVALID_REFRIGERATOR_SORT_TYPE : {}", refrigeratorSortType);

				return new BusinessException(ErrorCode.INVALID_REFRIGERATOR_SORT_TYPE);
			});
	}
}
