package org.belex.backend.dto;

import jakarta.validation.constraints.*;
import org.belex.backend.model.MealType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

public final class EntryDtos {
    private EntryDtos() {}

    public record AddEntryRequest(
            @NotBlank
            @Size(max = 240)
            String foodName,

            Long fdcId,

            @NotNull
            LocalDate date,

            @NotNull
            MealType mealType,

            @NotNull
            @DecimalMin("0.1")
            @DecimalMax("5000")
            BigDecimal serving,

            @NotNull
            @DecimalMin("0")
            BigDecimal calories,

            @NotNull
            @DecimalMin("0")
            BigDecimal proteins,

            @NotNull
            @DecimalMin("0")
            BigDecimal carbs,

            @NotNull
            @DecimalMin("0")
            BigDecimal fats
    ) {}

    public record EntryResponse(
            Long id,
            String foodName,
            Long fdcId,
            LocalDate date,
            MealType mealType,
            BigDecimal servingGrams,
            BigDecimal calories,
            BigDecimal protein,
            BigDecimal carbs,
            BigDecimal fat,
            Instant createdAt
    ) {}
}
