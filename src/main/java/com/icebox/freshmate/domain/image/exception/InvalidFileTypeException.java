package com.icebox.freshmate.domain.image.exception;

import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.exception.BusinessException;

public class InvalidFileTypeException extends BusinessException {

	public InvalidFileTypeException(ErrorCode errorCode) {
		super(errorCode);
	}
}
