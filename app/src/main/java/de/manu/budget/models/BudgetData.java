package de.manu.budget.models;

import java.util.Set;

public class BudgetData {

    private final Set<BudgetCategory> categorySet;
    private final Set<BudgetExpense> expenseSet;
    private final BudgetSettings settings;

    public BudgetData(Set<BudgetCategory> categorySet, Set<BudgetExpense> expenseSet, BudgetSettings settings) {
        this.categorySet = categorySet;
        this.expenseSet = expenseSet;
        this.settings = settings;
    }

    public Set<BudgetCategory> getCategorySet() {
        return categorySet;
    }

    public Set<BudgetExpense> getExpenseSet() {
        return expenseSet;
    }

    public BudgetSettings getSettings() {
        return settings;
    }
}
