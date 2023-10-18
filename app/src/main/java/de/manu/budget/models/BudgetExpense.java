package de.manu.budget.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class BudgetExpense {

    private UUID uuid;
    private String name;
    private LocalDateTime dateTime;
    private BudgetCategory category;

    public BudgetExpense(UUID uuid, String name, LocalDateTime dateTime, BudgetCategory category) {
        this.uuid = uuid;
        this.name = name;
        this.dateTime = dateTime;
        this.category = category;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public BudgetCategory getCategory() {
        return category;
    }
}
