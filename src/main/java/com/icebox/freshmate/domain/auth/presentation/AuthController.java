package com.icebox.freshmate.domain.auth.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.auth.application.AuthService;
import com.icebox.freshmate.domain.auth.application.dto.request.MemberLoginReq;
import com.icebox.freshmate.domain.auth.application.dto.request.MemberSignUpAuthReq;
import com.icebox.freshmate.domain.auth.application.dto.response.MemberAuthRes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/sign-up")
	public ResponseEntity<MemberAuthRes> signUp(@Valid @RequestBody MemberSignUpAuthReq memberSignUpAuthReq) {
		MemberAuthRes memberAuthRes = authService.signUp(memberSignUpAuthReq);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(memberAuthRes);
	}

	@PostMapping("/login")
	public ResponseEntity<MemberAuthRes> login(@Valid @RequestBody MemberLoginReq memberLoginReq) {
		MemberAuthRes memberAuthRes = authService.login(memberLoginReq);

		return ResponseEntity.ok(memberAuthRes);
	}
}
