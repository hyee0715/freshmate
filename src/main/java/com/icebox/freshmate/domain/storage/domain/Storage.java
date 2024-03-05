package com.icebox.freshmate.domain.storage.domain;

import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.global.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "storages", indexes = {
	@Index(name = "index_storages_refrigerator_id_storage_type_name_updated_at", columnList = "refrigerator_id, storage_type, name, updated_at", unique = true),
	@Index(name = "index_storages_updated_at", columnList = "refrigerator_id, storage_type, updated_at", unique = true)})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Storage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "refrigerator_id")
	private Refrigerator refrigerator;

	@Column(length = 50)
	private String name;

	@Enumerated(EnumType.STRING)
	private StorageType storageType;

	@Builder
	public Storage(Refrigerator refrigerator, String name, StorageType storageType) {
		this.refrigerator = refrigerator;
		this.name = name;
		this.storageType = storageType;
	}

	public void update(Storage storage) {
		this.name = storage.getName();
		this.storageType = storage.getStorageType();
	}
}
