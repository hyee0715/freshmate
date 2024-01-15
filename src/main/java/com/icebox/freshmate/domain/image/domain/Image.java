package com.icebox.freshmate.domain.image.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@NoArgsConstructor
@Getter
public abstract class Image {

	protected String fileName;

	public Image(String fileName) {
		this.fileName = fileName;
	}
}
