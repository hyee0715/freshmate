package com.icebox.freshmate.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	//서버
	INTERNAL_SERVER_ERROR("IN001", "예기치 못한 오류가 발생했습니다."),

	//공용
	INVALID_INPUT_VALUE("C001", "잘못된 값을 입력하셨습니다."),

	//회원
	ALREADY_EXIST_USERNAME("M001", "이미 존재하는 아이디입니다."),
	LOGIN_FAILURE("M002", "로그인에 실패했습니다."),
	NOT_FOUND_MEMBER("M003", "회원을 찾을 수 없습니다."),
	WRONG_PASSWORD("M004", "비밀번호가 일치하지 않습니다."),

	//인증, 인가
	INVALID_TOKEN("A001", "토큰의 유효성(형식, 서명 등)이 올바르지 않습니다."),
	EXPIRED_TOKEN("A002", "토큰이 만료되었습니다."),
	BAD_CREDENTIALS("A003", "아이디 또는 비밀번호가 일치하지 않습니다."),
	DISABLE_ACCOUNT("A004", "계정이 비활성화 되었습니다."),
	CREDENTIALS_EXPIRED("A005", "계정 유효기간이 만료 되었습니다."),
	NOT_FOUND_ACCOUNT("A006", "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요."),
	NOT_FOUND_AUTHENTICATION_CREDENTIALS("A007", "인증 요청이 거부되었습니다."),

	//냉장고
	NOT_FOUND_REFRIGERATOR("R001", "냉장고가 존재하지 않습니다."),

	//냉장고 저장소
	INVALID_STORAGE_TYPE("S001", "유효하지 않은 냉장고 저장소 타입입니다."),
	NOT_FOUND_STORAGE("S002", "냉장고 저장소가 존재하지 않습니다."),

	//식료품
	INVALID_GROCERY_TYPE("G001", "유효하지 않은 식료품 타입입니다."),
	NOT_FOUND_GROCERY("G002", "식료품이 존재하지 않습니다."),

	//레시피
	NOT_FOUND_RECIPE("RC001", "레시피가 존재하지 않습니다."),
	INVALID_SCRAP_ATTEMPT_TO_OWN_RECIPE("RC002", "본인이 작성한 레시피는 스크랩할 수 없습니다."),
	INVALID_UPDATE_ATTEMPT_TO_SCRAPED_RECIPE("RC003", "스크랩한 레시피는 수정할 수 없습니다."),

	//게시글
	NOT_FOUND_POST("P001", "게시글이 존재하지 않습니다."),

	//댓글
	NOT_FOUND_COMMENT("CM001", "댓글이 존재하지 않습니다.");

	private final String code;
	private final String message;
}
