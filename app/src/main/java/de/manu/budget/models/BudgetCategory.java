package de.manu.budget.models;

import java.text.MessageFormat;
import java.util.UUID;

public class BudgetCategory {

    private final UUID uuid;
    private final String name;
    private final int targetExpenses;
    private final int colorPrimary;
    private final int colorSecondary;

    public BudgetCategory(UUID uuid, String name, int targetExpenses, int colorPrimary, int colorSecondary) {
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

    public int getColorPrimary() {
        return colorPrimary;
    }

    public int getColorSecondary() {
        return colorSecondary;
    }

}
