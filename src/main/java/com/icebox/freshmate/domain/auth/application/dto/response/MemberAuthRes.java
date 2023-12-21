package com.icebox.freshmate.domain.auth.application.dto.response;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public record MemberAuthRes(
	String accessToken,
	String refreshToken
) {

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("accessToken", accessToken)
			.append("refreshToken", refreshToken)
			.toString();
	}
}