package org.belex.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class DashboardDtos {
    private DashboardDtos() {}

    public record MacroTotals(
            BigDecimal calories,
            BigDecimal proteins,
            BigDecimal carbs,
            BigDecimal fats
    ) {}

    public record DailyPoint(
            LocalDate date,
            BigDecimal calories,
            BigDecimal proteins,
            BigDecimal fats,
            BigDecimal carbs
    ){}

    public record DashboardResponse(
            LocalDate date,
            Integer dailyGoal,
            MacroTotals today,
            List<DailyPoint> week,
            List<DailyPoint> month
    ){}
}
