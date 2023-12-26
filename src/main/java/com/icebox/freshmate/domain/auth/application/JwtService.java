package com.icebox.freshmate.domain.auth.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.ErrorResponse;
import com.icebox.freshmate.global.error.exception.AuthenticationException;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;
import com.icebox.freshmate.global.error.exception.InvalidValueException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.access.expiration}")
	private long accessTokenValidityInSeconds;

	@Value("${jwt.refresh.expiration}")
	private long refreshTokenValidityInSeconds;

	@Value("${jwt.access.header}")
	private String accessHeader;

	@Value("${jwt.refresh.header}")
	private String refreshHeader;

	private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	private static final String USERNAME_CLAIM = "username";
	private static final String BEARER = "Bearer ";

	private final MemberRepository memberRepository;

	public String createAccessToken(String username) {
		return JWT.create()
			.withSubject(ACCESS_TOKEN_SUBJECT)
			.withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
			.withClaim(USERNAME_CLAIM, username)
			.sign(Algorithm.HMAC512(secret));
	}

	public String createRefreshToken() {
		return JWT.create()
			.withSubject(REFRESH_TOKEN_SUBJECT)
			.withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds * 1000))
			.sign(Algorithm.HMAC512(secret));
	}

	public void updateRefreshToken(String username, String refreshToken) {
		memberRepository.findByUsername(username)
			.ifPresentOrElse(
				member -> member.updateRefreshToken(refreshToken),
				() -> {
					throw new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER);
				}
			);
	}

	public void destroyRefreshToken(String username) {
		memberRepository.findByUsername(username)
			.ifPresentOrElse(
				Member::destroyRefreshToken,
				() -> {
					throw new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER);
				}
			);
	}

	public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
		response.setStatus(HttpServletResponse.SC_OK);

		setAccessTokenHeader(response, accessToken);
		setRefreshTokenHeader(response, refreshToken);

		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
		tokenMap.put(REFRESH_TOKEN_SUBJECT, refreshToken);
	}

	public void sendAccessToken(HttpServletResponse response, String accessToken) {
		response.setStatus(HttpServletResponse.SC_OK);

		setAccessTokenHeader(response, accessToken);

		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
	}

	public Optional<String> extractAccessToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(accessHeader)).filter(
			accessToken -> accessToken.startsWith(BEARER)

		).map(accessToken -> accessToken.replace(BEARER, ""));
	}

	public Optional<String> extractRefreshToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(refreshHeader)).filter(
			refreshToken -> refreshToken.startsWith(BEARER)
		).map(refreshToken -> refreshToken.replace(BEARER, ""));
	}

	public Optional<String> extractUsername(String accessToken) {
		try {
			return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getClaim(USERNAME_CLAIM).asString());
		} catch (Exception e) {
			log.error(e.getMessage());
			return Optional.empty();
		}
	}

	public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
		response.setHeader(accessHeader, accessToken);
	}

	public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
		response.setHeader(refreshHeader, refreshToken);
	}

	public boolean isTokenValid(HttpServletResponse response, String token) throws IOException {
		try {
			JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
			return true;
		} catch (SignatureVerificationException | AlgorithmMismatchException | InvalidClaimException |
				 JWTDecodeException e) {
			log.error("토큰의 유효성(형식, 서명 등)이 올바르지 않습니다. token : {}", token);
			writeErrorResponse(response, ErrorCode.INVALID_TOKEN, HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		} catch (TokenExpiredException e) {
			log.error("토큰이 만료되었습니다.");
			writeErrorResponse(response, ErrorCode.EXPIRED_TOKEN, HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
	}

	private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode, int statusCode) throws
		IOException {
		String errorResponseJsonFormat = getErrorResponseJsonFormat(errorCode);
		writeToHttpServletResponse(response, statusCode, errorResponseJsonFormat);
	}

	private void writeToHttpServletResponse(HttpServletResponse response, int statusCode, String errorMessage) throws
		IOException {
		response.setStatus(statusCode);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(errorMessage);
		response.getWriter().flush();
		response.getWriter().close();
	}

	private String getErrorResponseJsonFormat(ErrorCode errorCode) throws JsonProcessingException {
		ErrorResponse errorResponse = ErrorResponse.of(errorCode);
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(errorResponse);
	}

	public String reissueAccessToken(String refreshToken) {
		Member member = memberRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		String accessToken = createAccessToken(member.getUsername());
		log.info("access Token를 재발급합니다. access Token : {}", accessToken);

		return accessToken;
	}

	public String getRefreshToken(String refreshToken) throws IOException {
		if (refreshToken.startsWith("Bearer ")) {
			refreshToken = refreshToken.replace("Bearer ", "");
		}

		if (!isTokenValid(refreshToken)) {
			throw new InvalidValueException(ErrorCode.INVALID_TOKEN);
		}

		return refreshToken;
	}

	public boolean isTokenValid(String token) {
		try {
			JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
			return true;
		} catch (SignatureVerificationException | AlgorithmMismatchException | InvalidClaimException |
			JWTDecodeException e) {
			log.error("토큰이 유효성(형식, 서명 등)이 올바르지 않습니다. token : {}", token);
			throw new AuthenticationException(ErrorCode.INVALID_TOKEN);

		} catch (TokenExpiredException e) {
			log.error("토큰이 만료되었습니다.");
			throw new AuthenticationException(ErrorCode.EXPIRED_TOKEN);
		}
	}
}
