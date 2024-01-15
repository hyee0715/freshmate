package com.icebox.freshmate.domain.image.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@NoArgsConstructor
@Getter
public abstract class Image {

	protected String filename;

	public Image(String filename) {
		this.filename = filename;
	}
}
