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
	INVALID_LAST_PAGE_UPDATED_AT_FORMAT("C002", "유효하지 않은 이전 페이지의 업데이트 날짜 형식입니다."),
	INVALID_SORT_TYPE("COO3", "유효하지 않은 정렬 타입 입니다."),
	INVALID_LAST_PAGE_EXPIRATION_DATE_FORMAT("C004", "유효하지 않은 이전 페이지의 유통기한 날짜 형식입니다."),

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
	INVALID_REFRIGERATOR_SORT_TYPE("R002", "유효하지 않거나 허용되지 않는 냉장고 정렬 타입입니다."),

	//냉장고 저장소
	INVALID_STORAGE_TYPE("S001", "유효하지 않은 냉장고 저장소 타입입니다."),
	NOT_FOUND_STORAGE("S002", "냉장고 저장소가 존재하지 않습니다."),

	//식료품
	INVALID_GROCERY_TYPE("G001", "유효하지 않은 식료품 타입입니다."),
	NOT_FOUND_GROCERY("G002", "식료품이 존재하지 않습니다."),
	INVALID_GROCERY_EXPIRATION_TYPE("G003", "유효하지 않은 식료품 유통기한 타입입니다."),

	//즐겨 찾는 식료품
	NOT_FOUND_GROCERY_BUCKET("GB001", "즐겨 찾는 식료품이 존재하지 않습니다."),

	//레시피
	NOT_FOUND_RECIPE("RC001", "레시피가 존재하지 않습니다."),
	INVALID_SCRAP_ATTEMPT_TO_OWN_RECIPE("RC002", "본인이 작성한 레시피는 스크랩할 수 없습니다."),
	INVALID_UPDATE_ATTEMPT_TO_SCRAPED_RECIPE("RC003", "스크랩한 레시피는 수정할 수 없습니다."),
	INVALID_ATTEMPT_TO_POST_RECIPE("RC004", "본인이 작성하지 않은 레시피는 게시글로 공유할 수 없습니다."),
	INVALID_RECIPE_TYPE("RC005", "유효하지 않은 레시피 타입입니다."),
	INVALID_RECIPE_SORT_TYPE("RC006", "유효하지 않거나 허용되지 않는 레시피 정렬 타입입니다."),

	//레시피의 식재료
	INVALID_RECIPE_GROCERY_NAME("RG001", "레시피에 추가하려는 식재료 ID와 이름의 정보가 일치하지 않습니다."),
	DUPLICATED_RECIPE_GROCERY("RG002", "해당 레시피에 이미 같은 식재료가 등록되어 있습니다."),
	NOT_FOUND_RECIPE_GROCERY("RG003", "레시피의 식재료 정보가 존재하지 않습니다."),

	//즐겨 찾는 레시피
	RECIPE_OWNER_MISMATCH_TO_CREATE_RECIPE_BUCKET("RB001", "레시피 소유자의 정보와 즐겨 찾는 레시피로 등록하려는 사용자의 정보가 일치하지 않습니다."),
	DUPLICATED_RECIPE_BUCKET("RB002", "해당 레시피는 이미 즐겨 찾는 레시피로 등록되어 있습니다."),
	NOT_FOUND_RECIPE_BUCKET("RB003", "즐겨 찾는 레시피가 존재하지 않습니다."),

	//게시글
	NOT_FOUND_POST("P001", "게시글이 존재하지 않습니다."),

	//댓글
	NOT_FOUND_COMMENT("CM001", "댓글이 존재하지 않습니다."),

	//이미지
	NOT_FOUND_IMAGE("I001", "이미지가 존재하지 않습니다."),
	EMPTY_IMAGE("I002", "이미지가 비어있습니다."),
	INVALID_IMAGE_FORMAT("I003", "지원하지 않는 이미지 파일 형식입니다."),
	EXCESSIVE_DELETE_IMAGE_COUNT("I004", "이미지는 1개씩 삭제 가능합니다."),

	//알림
	INVALID_NOTIFICATION_TYPE("N001", "유효하지 않은 알림 타입입니다.");

	private final String code;
	private final String message;
}
