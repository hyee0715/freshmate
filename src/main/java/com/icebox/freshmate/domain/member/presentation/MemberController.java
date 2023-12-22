package com.icebox.freshmate.domain.member.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.member.application.MemberService;
import com.icebox.freshmate.domain.member.application.dto.request.MemberUpdateInfoReq;
import com.icebox.freshmate.domain.member.application.dto.request.MemberUpdatePasswordReq;
import com.icebox.freshmate.domain.member.application.dto.response.MemberInfoRes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/{id}")
	public ResponseEntity<MemberInfoRes> findInfoById(@Validated @PathVariable("id") Long id) {
		MemberInfoRes memberInfoRes = memberService.findInfoById(id);

		return ResponseEntity.ok(memberInfoRes);
	}

	@GetMapping
	public ResponseEntity<MemberInfoRes> findInfo() {
		MemberInfoRes memberInfoRes = memberService.findInfo();

		return ResponseEntity.ok(memberInfoRes);
	}

	@PatchMapping
	public ResponseEntity<MemberInfoRes> updateInfo(@Valid @RequestBody MemberUpdateInfoReq memberUpdateInfoReq) {
		MemberInfoRes memberInfoRes = memberService.updateInfo(memberUpdateInfoReq);

		return ResponseEntity.ok(memberInfoRes);
	}

	@PatchMapping("/password")
	public ResponseEntity<MemberInfoRes> updatePassword(@Valid @RequestBody MemberUpdatePasswordReq memberUpdatePasswordReq) {
		MemberInfoRes memberInfoRes = memberService.updatePassword(memberUpdatePasswordReq);

		return ResponseEntity.ok(memberInfoRes);
	}
}
