package com.icebox.freshmate.domain.member.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.auth.application.PrincipalDetails;
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
	public ResponseEntity<MemberInfoRes> findInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		MemberInfoRes memberInfoRes = memberService.findInfo(principalDetails.getUsername());

		return ResponseEntity.ok(memberInfoRes);
	}

	@PatchMapping
	public ResponseEntity<MemberInfoRes> updateInfo(@Valid @RequestBody MemberUpdateInfoReq memberUpdateInfoReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		MemberInfoRes memberInfoRes = memberService.updateInfo(memberUpdateInfoReq, principalDetails.getUsername());

		return ResponseEntity.ok(memberInfoRes);
	}

	@PatchMapping("/password")
	public ResponseEntity<MemberInfoRes> updatePassword(@Valid @RequestBody MemberUpdatePasswordReq memberUpdatePasswordReq, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		MemberInfoRes memberInfoRes = memberService.updatePassword(memberUpdatePasswordReq, principalDetails.getUsername());

		return ResponseEntity.ok(memberInfoRes);
	}
}
