package org.belex.backend.controller;

import org.belex.backend.dto.FoodDtos;
import org.belex.backend.service.FoodService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodController {
    private final FoodService foodService;
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/search")
    public List<FoodDtos.FoodSearchItem> search(@RequestParam String query){
        return foodService.search(query);
    }
}
