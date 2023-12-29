package com.icebox.freshmate.domain.storage.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, Long> {

	List<Storage> findAllByRefrigeratorId(Long refrigeratorId);
}
