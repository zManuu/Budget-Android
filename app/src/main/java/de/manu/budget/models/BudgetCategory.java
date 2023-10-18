package de.manu.budget.models;

import java.util.UUID;

public class BudgetCategory {

    private UUID uuid;
    private String name;
    private int targetExpenses;
    private String colorPrimary;
    private String colorSecondary;

    public BudgetCategory(UUID uuid, String name, int targetExpenses, String colorPrimary, String colorSecondary) {
        this.uuid = uuid;
        this.name = name;
        this.targetExpenses = targetExpenses;
        this.colorPrimary = colorPrimary;
        this.colorSecondary = colorSecondary;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getTargetExpenses() {
        return targetExpenses;
    }

    public String getColorPrimary() {
        return colorPrimary;
    }

    public String getColorSecondary() {
        return colorSecondary;
    }
}
