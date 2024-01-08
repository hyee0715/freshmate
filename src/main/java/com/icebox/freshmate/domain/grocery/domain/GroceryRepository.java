package com.icebox.freshmate.domain.grocery.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroceryRepository extends JpaRepository<Grocery, Long> {

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId")
	List<Grocery> findAllByStorageIdAndMemberId(@Param("storageId") Long storageId, @Param("memberId") Long memberId);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.id = :groceryId")
	Optional<Grocery> findByIdAndMemberId(@Param("groceryId") Long groceryId, @Param("memberId") Long memberId);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE g.groceryExpirationType = 'NOT_EXPIRED' AND g.expirationDate < :currentDate")
	List<Grocery> findAllNotExpiredBeforeCurrentDate(@Param("currentDate") LocalDate currentDate);

	@Query(value = """
		SELECT * 
		FROM groceries 
		WHERE expiration_date BETWEEN DATE_ADD(:currentDate, INTERVAL 1 DAY) AND DATE_ADD(:currentDate, INTERVAL 10 DAY) 
		ORDER BY expiration_date ASC;
		""", nativeQuery = true)
	List<Grocery> findAllWithExpirationDate10DaysEarlier(@Param("currentDate") LocalDate currentDate);

	@Query(value = """
		SELECT * 
		FROM groceries 
		WHERE :currentDate BETWEEN expiration_date AND DATE_ADD(expiration_date, INTERVAL 20 DAY) 
		ORDER BY expiration_date ASC;
		""", nativeQuery = true)
	List<Grocery> findAllWithExpirationDate20DaysLater(@Param("currentDate") LocalDate currentDate);


	@Query("SELECT g FROM Grocery g WHERE g.expirationDate = :currentDate")
	List<Grocery> findAllWithExpirationDateIsToday(@Param("currentDate") LocalDate currentDate);
}
