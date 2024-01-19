package com.icebox.freshmate.domain.grocery.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroceryImageRepository extends JpaRepository<GroceryImage, Long> {

	Optional<GroceryImage> findByGroceryIdAndPath(Long groceryId, String path);
}
