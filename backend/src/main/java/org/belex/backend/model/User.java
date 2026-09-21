package org.belex.backend.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="app_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long  id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(nullable = false)
    private Integer dailyGoal = 2000;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getDisplayName() {
        return displayName;
    }
    public Integer getDailyGoal() {
        return dailyGoal;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public void setDailyGoal(Integer dailyGoal) {
        this.dailyGoal = dailyGoal;
    }
}
