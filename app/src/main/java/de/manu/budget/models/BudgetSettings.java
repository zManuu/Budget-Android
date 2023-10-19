package de.manu.budget.models;

public class BudgetSettings {

    private int monthlyPaycheck;

    public BudgetSettings(int monthlyPaycheck) {
        this.monthlyPaycheck = monthlyPaycheck;
    }

    public int getMonthlyPaycheck() {
        return monthlyPaycheck;
    }

    public void setMonthlyPaycheck(int monthlyPaycheck) {
        this.monthlyPaycheck = monthlyPaycheck;
    }
}
