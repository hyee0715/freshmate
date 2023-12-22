package com.icebox.freshmate.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	//서버
	INTERNAL_SERVER_ERROR("S001", "예기치 못한 오류가 발생했습니다."),

	//공용
	INVALID_INPUT_VALUE("C001", "잘못된 값을 입력하셨습니다."),

	//회원
	ALREADY_EXIST_USERNAME("M001", "이미 존재하는 아이디입니다."),
	LOGIN_FAILURE("M002", "로그인에 실패했습니다."),
	NOT_FOUND_MEMBER("M003", "회원 정보가 없습니다."),
	WRONG_PASSWORD("M004", "비밀번호가 일치하지 않습니다.");

	private final String code;
	private final String message;
}
