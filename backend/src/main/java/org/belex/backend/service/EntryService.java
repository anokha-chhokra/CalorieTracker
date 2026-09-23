package org.belex.backend.service;

import org.belex.backend.dto.EntryDtos;
import org.belex.backend.model.FoodEntry;
import org.belex.backend.model.User;
import org.belex.backend.repo.FoodEntryRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class EntryService {
    private final FoodEntryRepo foodEntryRepo;
    private final UserService userService;
    public EntryService(FoodEntryRepo foodEntryRepo, UserService userService) {
        this.foodEntryRepo = foodEntryRepo;
        this.userService = userService;
    }

    public List<EntryDtos.EntryResponse> findForDate(String email, LocalDate date){
        User user = userService.getRequired(email);
        return foodEntryRepo.findByUserIdAndDateOrderByCreatedAtDesc(user.getId(), date)
                .stream().map(this::toResponse).toList();
    }

    public EntryDtos.EntryResponse add(String email, EntryDtos.AddEntryRequest entry){
        User user = userService.getRequired(email);
        FoodEntry foodEntry = new FoodEntry();
        foodEntry.setUser(user);
        foodEntry.setFoodName(entry.foodName().trim());
        foodEntry.setFdcId(entry.fdcId());
        foodEntry.setDate(entry.date());
        foodEntry.setMealType(entry.mealType());
        foodEntry.setServing(entry.serving());
        foodEntry.setCalories(entry.calories());
        foodEntry.setProteins(entry.proteins());
        foodEntry.setCarbs(entry.carbs());
        foodEntry.setFats(entry.fats());

        return toResponse(foodEntryRepo.save(foodEntry));
    }

    public void delete(String email, Long id){
        User user = userService.getRequired(email);
        FoodEntry foodEntry = foodEntryRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));
        if(!(foodEntry.getUser().getId().equals(user.getId()))){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }
        foodEntryRepo.delete(foodEntry);
    }

    private EntryDtos.EntryResponse toResponse(FoodEntry foodEntry){
        return new EntryDtos.EntryResponse(
                foodEntry.getId(),
                foodEntry.getFoodName(),
                foodEntry.getFdcId(),
                foodEntry.getDate(),
                foodEntry.getMealType(),
                foodEntry.getServing(),
                foodEntry.getCalories(),
                foodEntry.getProteins(),
                foodEntry.getCarbs(),
                foodEntry.getFats(),
                foodEntry.getCreatedAt()
        );
    }
}
