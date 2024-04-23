package com.icebox.freshmate.domain.auth.application.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebox.freshmate.domain.auth.application.JwtService;
import com.icebox.freshmate.domain.auth.application.dto.response.MemberAuthRes;
import com.icebox.freshmate.domain.member.domain.MemberRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJwtProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");

		String username = extractUsername(authentication);
		String accessToken = jwtService.createAccessToken(username);
		String refreshToken = jwtService.createRefreshToken();

		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

		memberRepository.findByUsername(username)
			.ifPresent(
				member -> member.updateRefreshToken(refreshToken)
			);

		MemberAuthRes memberAuthRes = new MemberAuthRes(accessToken, refreshToken);

		String result = objectMapper.writeValueAsString(memberAuthRes);
		response.getWriter().write(result);

		log.info("로그인에 성공합니다. username: {}", username);
		log.info("Access Token을 발급합니다. Access Token: {}", accessToken);
		log.info("Refresh Token을 발급합니다. Refresh Token: {}", refreshToken);
	}

	private String extractUsername(Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		return userDetails.getUsername();
	}
}
