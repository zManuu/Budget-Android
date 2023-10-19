package de.manu.budget.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.O)
public class BudgetExpense {

    private final UUID uuid;
    private final int price;
    private final String information;
    private final LocalDateTime dateTime;
    private final UUID categoryUuid;

    public BudgetExpense(UUID uuid, int price, String information, LocalDateTime dateTime, UUID categoryUuid) {
        this.uuid = uuid;
        this.price = price;
        this.information = information;
        this.dateTime = dateTime;
        this.categoryUuid = categoryUuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getPrice() {
        return price;
    }

    public String getInformation() {
        return information;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public UUID getCategoryUuid() {
        return categoryUuid;
    }

    public boolean isInCurrentMonth() {
        LocalDate dateNow = LocalDate.now();
        return dateNow.getMonthValue() == dateTime.getMonthValue()
                && dateNow.getYear() == dateTime.getYear();
    }
}
