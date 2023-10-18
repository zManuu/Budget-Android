package de.manu.budget;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import de.manu.budget.databinding.ActivityMainBinding;
import de.manu.budget.models.BudgetData;
import de.manu.budget.utils.serializer.DateTimeSerializer;
import de.manu.budget.utils.serializer.UUIDSerializer;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDSerializer())
            .registerTypeAdapter(UUID.class, new UUIDSerializer.Deserializer())
            .registerTypeAdapter(LocalDateTime.class, new DateTimeSerializer())
            .registerTypeAdapter(LocalDateTime.class, new DateTimeSerializer.Deserializer())
            .setPrettyPrinting()
            .create();

    private ActivityMainBinding binding;
    private SharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.navigation_dashboard, R.id.navigation_register_expenses, R.id.navigation_create_category)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        viewModel.setDataSaveListener(this::saveData);
        viewModel.setSharedData(this.loadData());
    }

    private void saveData() {
        Log.i(LOG_TAG, "Saving data...");

        if (viewModel == null || viewModel.getSharedData() == null || viewModel.getSharedData().getValue() == null) {
            Log.e(LOG_TAG, "Couldn't save data because some data wrapper / the data itself is null.");
            return;
        }

        BudgetData budgetData = viewModel.getSharedData().getValue();
        String budgetDataString = gson.toJson(budgetData);
        byte[] budgetDataBytes = budgetDataString.getBytes(StandardCharsets.UTF_8);

        try (FileOutputStream outputStream = openFileOutput("data.json", Context.MODE_PRIVATE)) {
            outputStream.write(budgetDataBytes);
            Log.i(LOG_TAG, "The data was successfully saved.");
        } catch (IOException e) {
            Log.e(LOG_TAG, "An error occurred while writing to the data.json file.", e);
        }
    }

    private BudgetData loadData() {
        Log.i(LOG_TAG, "Loading data...");

        BudgetData defaultData = new BudgetData(
                new HashSet<>(),
                new HashSet<>()
        );

        // todo: clean up try-catch hell

        try (
                InputStream inputStream = this.openFileInput("data.json");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            String text = bufferedReader.lines()
                    .collect(Collectors.joining("\n"));

            Type typeToken = new TypeToken<BudgetData>(){}.getType();
            BudgetData data = gson.fromJson(text, typeToken);
            Log.i(LOG_TAG, String.format("Data was loaded from file. Category-Count: %s, Expenses-Count: %s", data.getCategorySet().size(), data.getExpenseSet().size()));
            return data;
        } catch (FileNotFoundException e) {
            Log.i(LOG_TAG, "No data.json found. Creating file and returning default data.");

            try {
                getFilesDir().mkdirs();
                new File(getFilesDir(), "data.json").createNewFile();

                try (FileOutputStream outputStream = openFileOutput("data.json", Context.MODE_PRIVATE)) {
                    String defaultDataJsonString = gson.toJson(defaultData);
                    byte[] defaultDataJsonBytes = defaultDataJsonString.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(defaultDataJsonBytes);
                } catch (Exception e3) {
                    Log.e(LOG_TAG, "Couldn't write default data into file.", e3);
                }

            } catch (IOException e2) {
                Log.e(LOG_TAG, "Couldn't create data.json.", e2);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Couldn't read data file.", e);
        }

        return defaultData;
    }

}