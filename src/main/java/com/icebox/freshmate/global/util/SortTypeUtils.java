package com.icebox.freshmate.global.util;

import java.util.Arrays;

import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.InvalidValueException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum SortTypeUtils {

	NAME_ASC("nameAsc"),
	NAME_DESC("nameDesc"),
	UPDATED_AT_ASC("updatedAtAsc"),
	UPDATED_AT_DESC("updatedAtDesc"),
	EXPIRATION_DATE_ASC("expirationDateAsc"),
	EXPIRATION_DATE_DESC("expirationDateDesc"),
	TITLE_ASC("titleAsc"),
	TITLE_DESC("titleDesc"),
	CREATED_AT_ASC("createdAtAsc"),
	CREATED_AT_DESC("createdAtDesc"),
	ID_ASC("idAsc"),
	ID_DESC("idDesc");

	String sortType;

	SortTypeUtils(String sortType) {
		this.sortType = sortType;
	}

	public static SortTypeUtils findSortType(String sortType) {

		return Arrays.stream(SortTypeUtils.values())
			.filter(type -> type.getSortType().equalsIgnoreCase(sortType))
			.findAny()
			.orElseThrow(() -> {
				log.error("INVALID_SORT_TYPE : {}", sortType);

				return new InvalidValueException(ErrorCode.INVALID_SORT_TYPE);
			});
	}
}
