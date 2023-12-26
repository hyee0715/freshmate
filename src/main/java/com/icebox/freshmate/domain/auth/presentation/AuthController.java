package com.icebox.freshmate.domain.auth.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icebox.freshmate.domain.auth.application.AuthService;
import com.icebox.freshmate.domain.auth.application.JwtService;
import com.icebox.freshmate.domain.auth.application.dto.request.MemberLoginReq;
import com.icebox.freshmate.domain.auth.application.dto.request.MemberSignUpAuthReq;
import com.icebox.freshmate.domain.auth.application.dto.request.MemberWithdrawReq;
import com.icebox.freshmate.domain.auth.application.dto.response.MemberAuthRes;
import com.icebox.freshmate.domain.member.application.dto.response.MemberInfoRes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final JwtService jwtService;

	@PostMapping("/sign-up")
	public ResponseEntity<MemberInfoRes> signUp(@Valid @RequestBody MemberSignUpAuthReq memberSignUpAuthReq) {
		MemberInfoRes memberInfoRes = authService.signUp(memberSignUpAuthReq);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(memberInfoRes);
	}

	@PostMapping("/login")
	public ResponseEntity<MemberAuthRes> login(@Valid @RequestBody MemberLoginReq memberLoginReq) {
		MemberAuthRes memberAuthRes = authService.login(memberLoginReq);

		return ResponseEntity.ok(memberAuthRes);
	}

	@DeleteMapping("/withdraw")
	public ResponseEntity<Void> withdraw(@Valid @RequestBody MemberWithdrawReq memberWithdrawReq) {
		authService.withdraw(memberWithdrawReq.password());

		return ResponseEntity
			.noContent()
			.build();
	}

	@GetMapping("/reissue")
	public ResponseEntity<MemberAuthRes> reissueToken(@RequestHeader("Authorization-refresh") String refreshTokenHeader) {
		String refreshToken = jwtService.getRefreshToken(refreshTokenHeader);
		String reissuedAccessToken = jwtService.reissueAccessToken(refreshToken);

		MemberAuthRes memberAuthRes = new MemberAuthRes(reissuedAccessToken, refreshToken);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(memberAuthRes);
	}

	@PatchMapping("/logout")
	public ResponseEntity<Void> logout() {
		authService.logout();

		return ResponseEntity
			.noContent()
			.build();
	}
}
