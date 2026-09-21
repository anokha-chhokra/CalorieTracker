package org.belex.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "food_entries")
public class FoodEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MealType mealType;
    @Column(nullable = false, length = 80)
    private String foodName;

    private Long fdcId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal serving;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal calories;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal carbs;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal proteins;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fats;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getDate() {
        return date;
    }

    public MealType getMealType() {
        return mealType;
    }

    public String getFoodName() {
        return foodName;
    }

    public Long getFdcId() {
        return fdcId;
    }

    public BigDecimal getServing() {
        return serving;
    }

    public BigDecimal getCalories() {
        return calories;
    }

    public BigDecimal getCarbs() {
        return carbs;
    }

    public BigDecimal getProteins() {
        return proteins;
    }

    public BigDecimal getFats() {
        return fats;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setFats(BigDecimal fats) {
        this.fats = fats;
    }

    public void setProteins(BigDecimal proteins) {
        this.proteins = proteins;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setFdcId(Long fdcId) {
        this.fdcId = fdcId;
    }

    public void setServing(BigDecimal serving) {
        this.serving = serving;
    }

    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }

    public void setCarbs(BigDecimal carbs) {
        this.carbs = carbs;
    }
}
