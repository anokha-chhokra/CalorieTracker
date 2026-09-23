package org.belex.backend.service;

import org.belex.backend.dto.AuthDtos;
import org.belex.backend.dto.FoodDtos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FoodService {
    private final RestClient restClient;
    private final String apiKey;
    public FoodService(@Value("${usda.api-key}") String apiKey){
        this.restClient = RestClient.builder().baseUrl("https://api.nal.usda.gov/fdc/v1").build();
        this.apiKey = apiKey;
    }

    public List<FoodDtos.FoodSearchItem> search(String query){
        if (query == null || query.isEmpty()){
            return List.of();
        }
        FoodDtos.FoodSearchResponse response = restClient.get().uri(
                uriBuilder -> uriBuilder.path("/foods/search")
                        .queryParam("api_key", apiKey)
                        .queryParam("query", query.trim())
                        .queryParam("pageSize", 12)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(FoodDtos.FoodSearchResponse.class);

        if (response == null || response.foods() == null || response.foods().isEmpty()){
            return List.of();
        }

        List<FoodDtos.FoodSearchItem> result = new ArrayList<>();
        for (FoodDtos.FoodResult food : response.foods()) {
            //nutrientId provided by USDA
            double kcal = nutrient(food, 1008);
            double proteins = nutrient(food, 1003);
            double carbs = nutrient(food, 1005);
            double fats = nutrient(food, 1004);

            if (kcal >= 0) {
                result.add(new FoodDtos.FoodSearchItem(food.fcdId(), Objects.requireNonNullElse(food.description(), "Unnamed food"),
                        Objects.requireNonNullElse(food.brandOwner(), "USDA"),
                        Objects.requireNonNullElse(food.dataType(), ""),
                        kcal, proteins, carbs, fats));
            }
        }
        return result;
    }

    private double nutrient(FoodDtos.FoodResult food, int nutrientId){
        if (food.foodNutrients() == null || food.foodNutrients().isEmpty()){
            return 0;
        }
        return food.foodNutrients().stream()
                .filter(n -> n.nutrientId() != null && n.nutrientId() == nutrientId)
                .map(FoodDtos.FoodNutrient::value)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(0.0);
    }
}
