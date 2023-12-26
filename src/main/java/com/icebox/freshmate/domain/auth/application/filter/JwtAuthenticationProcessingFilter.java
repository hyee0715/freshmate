package com.icebox.freshmate.domain.auth.application.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.icebox.freshmate.domain.auth.application.JwtService;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;

	private final String LOGIN_URL = "/api/auth/login";
	private final String REISSUE_TOKEN = "/api/auth/reissue";

	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (request.getRequestURI().equals(LOGIN_URL) || request.getRequestURI().equals(REISSUE_TOKEN)) {
			filterChain.doFilter(request, response);
			return;
		}

		String refreshToken = jwtService
			.extractRefreshToken(request)
			.filter(token -> {
				try {
					return jwtService.isTokenValid(response, token);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			})
			.orElse(null);

		if(refreshToken != null){
			checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
			return;
		}

		checkAccessTokenAndAuthentication(request, response, filterChain);
	}

	private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		jwtService.extractAccessToken(request).filter(token -> {
			try {
				return jwtService.isTokenValid(response, token);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).ifPresent(
			accessToken -> jwtService.extractUsername(accessToken).ifPresent(
				username -> memberRepository.findByUsername(username).ifPresent(
					member -> saveAuthentication(member)
				)
			)
		);

		filterChain.doFilter(request,response);
	}

	private void saveAuthentication(Member member) {
		UserDetails user = User.builder()
			.username(member.getUsername())
			.password(member.getPassword())
			.roles(member.getRole().name())
			.build();

		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,authoritiesMapper.mapAuthorities(user.getAuthorities()));

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		memberRepository.findByRefreshToken(refreshToken).ifPresent(
			member -> {
				String accessToken = jwtService.createAccessToken(member.getUsername());
				jwtService.sendAccessToken(response, accessToken);
			}
		);
	}
}
