package org.belex.backend.repo;

import org.belex.backend.model.FoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FoodEntryRepo extends JpaRepository<FoodEntry,Long> {
    List<FoodEntry> findByUserIdAndDateOrderByCreatedAtDesc(Long userId, LocalDate date);
    List<FoodEntry> findByUserIdAndDateBetweenOrderByDateAscCreatedAtAsc(Long userId, LocalDate start, LocalDate end);
}
