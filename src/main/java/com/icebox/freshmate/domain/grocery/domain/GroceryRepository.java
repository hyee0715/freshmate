package com.icebox.freshmate.domain.grocery.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroceryRepository extends JpaRepository<Grocery, Long> {

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId")
	List<Grocery> findAllByStorageIdAndMemberId(@Param("storageId") Long storageId, @Param("memberId") Long memberId);
}
