package com.icebox.freshmate.domain.image.domain;

import com.icebox.freshmate.global.BaseEntity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@NoArgsConstructor
@Getter
public abstract class Image extends BaseEntity {

	protected String fileName;

	public Image(String fileName) {
		this.fileName = fileName;
	}
}
