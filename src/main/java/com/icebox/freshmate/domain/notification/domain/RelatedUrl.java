package com.icebox.freshmate.domain.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class RelatedUrl {

	@Column(nullable = false)
	private String url;

	public RelatedUrl(String url){
		this.url = url;
	}
}
