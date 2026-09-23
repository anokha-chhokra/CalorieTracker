package org.belex.backend.service;

import org.belex.backend.dto.DashboardDtos;
import org.belex.backend.model.FoodEntry;
import org.belex.backend.model.User;
import org.belex.backend.repo.FoodEntryRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final FoodEntryRepo foodEntryRepo;
    private final UserService userService;
    public DashboardService(FoodEntryRepo foodEntryRepo, UserService userService) {
        this.foodEntryRepo = foodEntryRepo;
        this.userService = userService;
    }

    public DashboardDtos.DashboardResponse getDashboard(String email){
        User user = userService.getRequired(email);
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.minusDays(29);

        List<FoodEntry> entries = foodEntryRepo.findByUserIdAndDateBetweenOrderByDateAscCreatedAtAsc(user.getId(), monthStart, today);
        Map<LocalDate, List<FoodEntry>> byDate = entries.stream()
                .collect(Collectors.groupingBy(FoodEntry::getDate, LinkedHashMap::new, Collectors.toList()));
        List<DashboardDtos.DailyPoint> month = days(monthStart, today, byDate);
        List<DashboardDtos.DailyPoint> week = days(today.minusDays(6), today, byDate);

        return new DashboardDtos.DashboardResponse(
                today,
                user.getDailyGoal(),
                totals(byDate.getOrDefault(today, List.of())),
                week,
                month
        );
    }

    private List<DashboardDtos.DailyPoint> days(LocalDate start, LocalDate end, Map<LocalDate, List<FoodEntry>> byDate){
        List<DashboardDtos.DailyPoint> result = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            result.add(new DashboardDtos.DailyPoint(
                    date,
                    totals(byDate.getOrDefault(date, List.of())).calories(),
                    totals(byDate.getOrDefault(date, List.of())).proteins(),
                    totals(byDate.getOrDefault(date, List.of())).carbs(),
                    totals(byDate.getOrDefault(date, List.of())).fats()
            ));
        }
        return result;
    }

    private DashboardDtos.MacroTotals totals(List<FoodEntry> entries){
        return new DashboardDtos.MacroTotals(
                sum(entries, FoodEntry::getCalories),
                sum(entries, FoodEntry::getProteins),
                sum(entries, FoodEntry::getCarbs),
                sum(entries, FoodEntry::getFats)
        );
    }

    private BigDecimal sum(List<FoodEntry> entries, Function<FoodEntry, BigDecimal> func){
        return entries.stream()
                .map(func)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(1, RoundingMode.HALF_UP);
    }
}
