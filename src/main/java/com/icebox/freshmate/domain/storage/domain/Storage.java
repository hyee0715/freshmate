package com.icebox.freshmate.domain.storage.domain;

import com.icebox.freshmate.domain.icebox.domain.Icebox;
import com.icebox.freshmate.global.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "storages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Storage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "icebox_id")
	private Icebox icebox;

	@Builder
	public Storage(Icebox icebox) {
		this.icebox = icebox;
	}
}
