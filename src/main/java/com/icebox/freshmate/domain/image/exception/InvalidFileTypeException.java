package com.icebox.freshmate.domain.image.exception;

import com.icebox.freshmate.global.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidFileTypeException extends RuntimeException {

	private final ErrorCode errorCode;
}
