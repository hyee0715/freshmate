package com.icebox.freshmate.domain.storage.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StorageRepository extends JpaRepository<Storage, Long> {

	List<Storage> findAllByRefrigeratorId(Long refrigeratorId);

	@Query("SELECT s FROM Storage s JOIN s.refrigerator r WHERE s.id = :storageId AND r.member.id = :memberId")
	Optional<Storage> findByIdAndMemberId(@Param("storageId") Long storageId, @Param("memberId") Long memberId);
}
