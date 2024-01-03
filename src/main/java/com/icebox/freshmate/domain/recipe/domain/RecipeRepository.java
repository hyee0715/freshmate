package com.icebox.freshmate.domain.recipe.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

	List<Recipe> findAllByWriterId(Long writerId);

	List<Recipe> findAllByOwnerId(Long ownerId);

	@Query("SELECT r FROM Recipe r WHERE r.writer.id = :memberId OR r.owner.id = :memberId")
	List<Recipe> findAllByMemberId(@Param("memberId") Long memberId);

	Optional<Recipe> findByIdAndWriterId(Long id, Long writerId);

	Optional<Recipe> findByIdAndOwnerId(Long id, Long ownerId);
}
