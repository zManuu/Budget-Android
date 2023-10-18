package de.manu.budget.ui.createcategory;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.UUID;

import de.manu.budget.SharedViewModel;
import de.manu.budget.databinding.FragmentCreateCategoryBinding;
import de.manu.budget.models.BudgetCategory;
import de.manu.budget.models.BudgetData;

public class CreateCategoryFragment extends Fragment {

    private static final String LOG_TAG = CreateCategoryFragment.class.getSimpleName();
    private FragmentCreateCategoryBinding binding;
    private SharedViewModel viewModel;
    private String selectedColorCodePrimary = "#FFFFFF";
    private String selectedColorCodeSecondary = "#FFFFFF";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        binding = FragmentCreateCategoryBinding.inflate(inflater, container, false);
        binding.ccColorPickerPrimary.setColorListener((ColorEnvelopeListener) (envelope, fromUser) -> this.selectedColorCodePrimary = '#' + envelope.getHexCode());
        binding.ccColorPickerSecondary.setColorListener((ColorEnvelopeListener) (envelope, fromUser) -> this.selectedColorCodeSecondary = '#' + envelope.getHexCode());
        binding.ccButtonConfirm.setOnClickListener(this::create);

        return binding.getRoot();
    }

    private void create(View _v) {
        String nameInput = binding.ccInputName.getText().toString();
        String targetedExpensesInput = binding.ccInputTargetedExpenses.getText().toString();

        if (nameInput.isEmpty() || targetedExpensesInput.isEmpty()) {
            Log.w(LOG_TAG, "One of the required fields name or targetedExpenses was null or blank -> creation cancelled.");
            Toast.makeText(this.getContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int targetedExpenses = Integer.parseInt(targetedExpensesInput);

            Log.i(LOG_TAG,
                    String.format(
                            "Creating a new category: name=%s, targetedExpenses=%s, colorPrimary=%s, colorSecondary=%s",
                            nameInput,
                            targetedExpensesInput,
                            selectedColorCodePrimary,
                            selectedColorCodeSecondary
                    )
            );

            BudgetCategory category = new BudgetCategory(
                    UUID.randomUUID(),
                    nameInput,
                    targetedExpenses,
                    selectedColorCodePrimary,
                    selectedColorCodeSecondary
            );

            BudgetData currentData = this.viewModel.getSharedData().getValue();

            if (currentData == null) {
                Log.w(LOG_TAG, "Couldn't create a new category because the current data is null.");
                return;
            }

            if (currentData.getCategorySet().stream().anyMatch(e -> e.getName().equalsIgnoreCase(nameInput))) {
                Log.w(LOG_TAG, "A category with the name " + nameInput + " exists -> cancelling creation.");
                Toast.makeText(this.getContext(), "A category with the name " + nameInput + " exists already.", Toast.LENGTH_SHORT).show();
                return;
            }

            currentData.getCategorySet().add(category);
            this.viewModel.setSharedData(currentData);
            this.viewModel.saveChanges();

            Log.i(LOG_TAG, "The category " + nameInput + " was created.");
            Toast.makeText(this.getContext(), "The category " + nameInput + " was created.", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Log.w(LOG_TAG, "TargetExpenses must be a number.", e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel = null;
        selectedColorCodePrimary = "#FFFFFF";
        selectedColorCodeSecondary = "#FFFFFF";
    }
}