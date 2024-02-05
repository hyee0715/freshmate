package com.icebox.freshmate.domain.grocery.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroceryRepository extends JpaRepository<Grocery, Long> {

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId")
	Slice<Grocery> findAllByStorageIdAndMemberId(@Param("storageId") Long storageId, @Param("memberId") Long memberId, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId ORDER BY g.name ASC, g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdOrderByNameAsc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId ORDER BY g.name DESC, g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdOrderByNameDesc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId ORDER BY g.updatedAt ASC")
	Slice<Grocery> findAllByStorageIdAndMemberIdOrderByUpdatedAtAsc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId ORDER BY g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdOrderByUpdatedAtDesc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryExpirationType = :groceryExpirationType ORDER BY g.name ASC, g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByNameAsc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryExpirationType") GroceryExpirationType groceryExpirationType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryExpirationType = :groceryExpirationType ORDER BY g.name DESC, g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByNameDesc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryExpirationType") GroceryExpirationType groceryExpirationType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryExpirationType = :groceryExpirationType ORDER BY g.updatedAt ASC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByUpdatedAtAsc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryExpirationType") GroceryExpirationType groceryExpirationType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryExpirationType = :groceryExpirationType ORDER BY g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByUpdatedAtDesc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryExpirationType") GroceryExpirationType groceryExpirationType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryExpirationType = :groceryExpirationType ORDER BY g.expirationDate ASC, g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByExpirationDateAsc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryExpirationType") GroceryExpirationType groceryExpirationType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryExpirationType = :groceryExpirationType ORDER BY g.expirationDate DESC, g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryExpirationTypeOrderByExpirationDateDesc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryExpirationType") GroceryExpirationType groceryExpirationType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryType = :groceryType AND g.groceryExpirationType = :groceryExpirationType ORDER BY g.name ASC, g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByNameAsc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryType") GroceryType groceryType, @Param("groceryExpirationType") GroceryExpirationType groceryExpirationType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryType = :groceryType AND g.groceryExpirationType = :groceryExpirationType ORDER BY g.name DESC, g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByNameDesc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryType") GroceryType groceryType, @Param("groceryExpirationType") GroceryExpirationType groceryExpirationType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryType = :groceryType AND g.groceryExpirationType = :groceryExpirationType ORDER BY g.updatedAt ASC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByUpdatedAtAsc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryType") GroceryType groceryType, @Param("groceryExpirationType") GroceryExpirationType groceryExpirationType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryType = :groceryType AND g.groceryExpirationType = :groceryExpirationType ORDER BY g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByUpdatedAtDesc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryType") GroceryType groceryType, @Param("groceryExpirationType") GroceryExpirationType groceryExpirationType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryType = :groceryType AND g.groceryExpirationType = :groceryExpirationType ORDER BY g.expirationDate ASC, g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByExpirationDateAsc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryType") GroceryType groceryType, @Param("groceryExpirationType") GroceryExpirationType groceryExpirationType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryType = :groceryType AND g.groceryExpirationType = :groceryExpirationType ORDER BY g.expirationDate DESC, g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeAndGroceryExpirationTypeOrderByExpirationDateDesc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryType") GroceryType groceryType, @Param("groceryExpirationType") GroceryExpirationType groceryExpirationType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryType = :groceryType ORDER BY g.name ASC, g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderByNameAsc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryType") GroceryType groceryType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryType = :groceryType ORDER BY g.name DESC, g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderByNameDesc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryType") GroceryType groceryType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryType = :groceryType ORDER BY g.updatedAt ASC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderByUpdatedAtAsc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryType") GroceryType groceryType, Pageable pageable);

	@Query("SELECT g FROM Grocery g JOIN g.storage s JOIN s.refrigerator r WHERE r.member.id = :memberId AND g.storage.id = :storageId AND g.groceryType = :groceryType ORDER BY g.updatedAt DESC")
	Slice<Grocery> findAllByStorageIdAndMemberIdAndGroceryTypeOrderByUpdatedAtDesc(@Param("storageId") Long storageId, @Param("memberId") Long memberId, @Param("groceryType") GroceryType groceryType, Pageable pageable);

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
