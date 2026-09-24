package org.belex.backend.dto;

import java.util.List;

public final class FoodDtos {
    private FoodDtos() {}

    public record FoodNutrient(
            Integer nutrientId,
            String nutrientName,
            String unitName,
            Double value
    ){}

    public record FoodResult(
            long fdcId,
            String description,
            String dataType,
            String brandOwner,
            List<FoodNutrient> foodNutrients
    ) {}

    public record FoodSearchResponse(List<FoodResult> foods){}

    public record FoodSearchItem(
            long fdcId,
            String name,
            String brand,
            String dataType,
            Double caloriesPer100g,
            Double proteinsPer100g,
            Double carbsPer100g,
            Double fatsPer100g
    ) {}
}
