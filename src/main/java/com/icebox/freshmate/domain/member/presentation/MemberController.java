package com.icebox.freshmate.domain.member.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.member.application.MemberService;
import com.icebox.freshmate.domain.member.application.dto.response.MemberInfoRes;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/{id}")
	public ResponseEntity<MemberInfoRes> findMemberInfoById(@Validated @PathVariable("id") Long id) {
		MemberInfoRes memberInfoRes = memberService.findMemberInfoById(id);

		return ResponseEntity.ok(memberInfoRes);
	}

	@GetMapping
	public ResponseEntity<MemberInfoRes> findMemberInfo() {
		MemberInfoRes memberInfoRes = memberService.findMemberInfo();

		return ResponseEntity.ok(memberInfoRes);
	}
}
