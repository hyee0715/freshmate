package com.icebox.freshmate.domain.auth.application.handler;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebox.freshmate.global.error.ErrorCode;
import com.icebox.freshmate.global.error.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
		ErrorCode errorCode;

		if (exception instanceof BadCredentialsException) {
			errorCode = ErrorCode.BAD_CREDENTIALS;
		} else if (exception instanceof InternalAuthenticationServiceException) {
			errorCode = ErrorCode.BAD_CREDENTIALS;
		} else if (exception instanceof DisabledException) {
			errorCode = ErrorCode.DISABLE_ACCOUNT;
		} else if (exception instanceof CredentialsExpiredException) {
			errorCode = ErrorCode.CREDENTIALS_EXPIRED;
		} else if (exception instanceof UsernameNotFoundException) {
			errorCode = ErrorCode.NOT_FOUND_ACCOUNT;
		} else if (exception instanceof AuthenticationCredentialsNotFoundException) {
			errorCode = ErrorCode.NOT_FOUND_AUTHENTICATION_CREDENTIALS;
		} else {
			errorCode = ErrorCode.LOGIN_FAILURE;
		}

		log.info(errorCode.getMessage());

		writeErrorResponse(response, errorCode, HttpServletResponse.SC_BAD_REQUEST);
	}

	private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode, int statusCode) throws IOException {
		String errorResponseJsonFormat = getErrorResponseJsonFormat(errorCode);

		writeToHttpServletResponse(response, statusCode, errorResponseJsonFormat);
	}

	private void writeToHttpServletResponse(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
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
}
