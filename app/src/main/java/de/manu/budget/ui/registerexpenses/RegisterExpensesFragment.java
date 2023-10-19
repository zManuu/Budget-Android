package de.manu.budget.ui.registerexpenses;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import de.manu.budget.SharedViewModel;
import de.manu.budget.databinding.FragmentRegisterExpensesBinding;
import de.manu.budget.models.BudgetCategory;
import de.manu.budget.models.BudgetData;
import de.manu.budget.models.BudgetExpense;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class RegisterExpensesFragment extends Fragment {

    private static final String LOG_TAG = RegisterExpensesFragment.class.getSimpleName();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private FragmentRegisterExpensesBinding binding;
    private SharedViewModel viewModel;
    private LocalDate selectedDate;
    private LocalTime selectedTime;
    private UUID selectedCategory;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterExpensesBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        selectedDate = LocalDate.now();
        selectedTime = LocalTime.now();
        updateDateTimeDisplays();

        setupCategorySpinner();

        binding.reButtonPickerDate.setOnClickListener((view) -> {
            new DatePickerDialog(
                    requireContext(),
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        selectedDate = LocalDate.of(year, monthOfYear, dayOfMonth);
                        updateDateTimeDisplays();
                    },
                    selectedDate.getYear(),
                    selectedDate.getMonthValue(),
                    selectedDate.getDayOfMonth()
            ).show();
        });

        binding.reButtonPickerTime.setOnClickListener((view) -> {
            new TimePickerDialog(
                    requireContext(),
                    (view1, hour, minute) -> {
                        selectedTime = LocalTime.of(hour, minute);
                        updateDateTimeDisplays();
                    },
                    selectedTime.getHour(),
                    selectedTime.getMinute(),
                    true
            ).show();
        });

        binding.reButtonConfirm.setOnClickListener((view) -> {
            Log.i(LOG_TAG, "Creating a new expense.");

            String priceInput = binding.reInputPrice.getText().toString();
            String informationInput = binding.reInputInformation.getText().toString();

            if (priceInput.isEmpty() || selectedCategory == null) {
                Log.w(LOG_TAG, "One of the required fields price, category is not set, cancelling creation process.");
                Toast.makeText(getContext(), "Please fill in all required fields (price, category).", Toast.LENGTH_SHORT).show();
                return;
            }

            LocalDateTime dateTime = LocalDateTime.of(
                    selectedDate.getYear(),
                    selectedDate.getMonthValue(),
                    selectedDate.getDayOfMonth(),
                    selectedTime.getHour(),
                    selectedTime.getMinute()
            );

            try {
                int price = Integer.parseInt(priceInput);

                BudgetExpense expense = new BudgetExpense(
                        UUID.randomUUID(),
                        price,
                        informationInput,
                        dateTime,
                        selectedCategory
                );

                BudgetData currentData = viewModel.getSharedData().getValue();

                if (currentData == null) {
                    Log.e(LOG_TAG, "Couldn't create the expense because the live data is null.");
                    Toast.makeText(getContext(), "An error occurred.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(LOG_TAG, MessageFormat.format("Successfully created an expense: UUID={0}", expense.getUuid()));
                Toast.makeText(getContext(), "The expense was saved.", Toast.LENGTH_SHORT).show();

                currentData.getExpenseSet().add(expense);
                viewModel.setSharedData(currentData);
                viewModel.saveChanges();
            } catch (NumberFormatException e) {
                Log.e(LOG_TAG, MessageFormat.format("Couldn't create expense because the inputted price is not parsable to an integer: {0}", priceInput));
                Toast.makeText(getContext(), "Please input a whole number as price.", Toast.LENGTH_SHORT).show();
            }

        });

        return binding.getRoot();
    }

    private void setupCategorySpinner() {
        if (viewModel == null || viewModel.getSharedData() == null || viewModel.getSharedData().getValue() == null
                || viewModel.getSharedData().getValue().getCategorySet() == null) {
            Log.e(LOG_TAG, "The categories or one of it's wrappers is null.");
            return;
        }

        Set<BudgetCategory> categories = viewModel.getSharedData().getValue().getCategorySet();
        String[] categoryNames = categories.stream().map(BudgetCategory::getName).toArray(String[]::new);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
        );

        binding.reSpinnerCategory.setAdapter(spinnerAdapter);
        binding.reSpinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String categoryName = categoryNames[position];

                Optional<BudgetCategory> category = categories.stream()
                        .filter(e -> e.getName().equalsIgnoreCase(categoryName))
                        .findAny();

                if (category.isPresent()) {
                    selectedCategory = category.get().getUuid();
                    Log.i(LOG_TAG, MessageFormat.format("Selected category. uuid={0}, name={1}", category.get().getUuid(), category.get().getName()));
                } else {
                    selectedCategory = null;
                    Log.e(LOG_TAG, MessageFormat.format("Invalid category was selected. position={0}, id={1}, categoryName={2}", position, id, categoryName));
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedCategory = null;
            }
        });
    }

    private void updateDateTimeDisplays() {
        binding.reDisplayDate.setText(dateFormatter.format(selectedDate));
        binding.reDisplayTime.setText(timeFormatter.format(selectedTime));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel = null;
        selectedCategory = null;
        selectedDate = null;
        selectedTime = null;
    }
}