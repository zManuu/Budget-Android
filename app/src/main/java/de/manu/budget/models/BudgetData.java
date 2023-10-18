package de.manu.budget.models;

import java.util.Set;

public class BudgetData {

    private Set<BudgetCategory> categorySet;
    private Set<BudgetExpense> expenseSet;

    public BudgetData(Set<BudgetCategory> categorySet, Set<BudgetExpense> expenseSet) {
        this.categorySet = categorySet;
        this.expenseSet = expenseSet;
    }

    public Set<BudgetCategory> getCategorySet() {
        return categorySet;
    }

    public Set<BudgetExpense> getExpenseSet() {
        return expenseSet;
    }
}
