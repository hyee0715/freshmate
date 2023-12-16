package com.icebox.freshmate.global.error.exception;

import com.icebox.freshmate.global.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidValueException extends RuntimeException {

	private final ErrorCode errorCode;
}
