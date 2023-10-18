package de.manu.budget;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.manu.budget.models.BudgetData;

public class SharedViewModel extends ViewModel {

    public interface DataSaveListener { void saveData(); }

    private final static String LOG_TAG = SharedViewModel.class.getSimpleName();
    private DataSaveListener dataSaveListener;
    private MutableLiveData<BudgetData> sharedData = new MutableLiveData<>();

    public void setSharedData(BudgetData data) {
        sharedData.setValue(data);
    }

    public LiveData<BudgetData> getSharedData() {
        return sharedData;
    }

    public void setDataSaveListener(DataSaveListener dataSaveListener) {
        this.dataSaveListener = dataSaveListener;
    }

    public void saveChanges() {
        if (this.dataSaveListener == null) {
            Log.w(LOG_TAG, "Couldn't save data because the dataSaveListener is null.");
            return;
        }

        this.dataSaveListener.saveData();
    }

}
