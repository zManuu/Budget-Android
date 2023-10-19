package de.manu.budget.ui.dashboard;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.manu.budget.MainActivity;
import de.manu.budget.SharedViewModel;
import de.manu.budget.databinding.FragmentDashboardBinding;
import de.manu.budget.models.BudgetCategory;
import de.manu.budget.models.BudgetData;
import de.manu.budget.models.BudgetExpense;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DashboardFragment extends Fragment {

    private static final String LOG_TAG = DashboardFragment.class.getSimpleName();
    private FragmentDashboardBinding binding;
    private SharedViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        configureCharts();
        loadData();

        return binding.getRoot();
    }

    private void configureCharts() {
        binding.chartOverview.getDescription().setText("Category Overview");
        binding.chartOverview.getDescription().setTextSize(15);
        binding.chartOverview.setUsePercentValues(true);
        binding.chartOverview.setDragDecelerationFrictionCoef(0.95f);
        binding.chartOverview.setDrawHoleEnabled(true);
        binding.chartOverview.setHoleColor(Color.WHITE);
        binding.chartOverview.setTransparentCircleColor(Color.WHITE);
        binding.chartOverview.setTransparentCircleAlpha(110);
        binding.chartOverview.setHoleRadius(48f);
        binding.chartOverview.setTransparentCircleRadius(51f);
        binding.chartOverview.setDrawCenterText(true);
        binding.chartOverview.setRotationAngle(0);
        binding.chartOverview.setRotationEnabled(true);
        binding.chartOverview.setHighlightPerTapEnabled(true);
        binding.chartOverview.animateY(1400, Easing.EaseInOutQuad);
        binding.chartOverview.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);
        binding.chartOverview.setEntryLabelColor(Color.WHITE);
        binding.chartOverview.setEntryLabelTextSize(12f);
        binding.chartOverview.getLegend().setEnabled(false);

        List<IBarDataSet> dataSets = Arrays.asList(
                new BarDataSet(
                        Arrays.asList(
                                new BarEntry(0, 10),
                                new BarEntry(1, 10)
                        ),
                        "ABC"
                )
        );
        BarData d = new BarData(dataSets);
        binding.chartIndividual.setData(d);
        binding.chartIndividual.getDescription().setEnabled(false);
        binding.chartIndividual.getLegend().setEnabled(false);
    }

    private float getCategoryChartValue(BudgetCategory category, BudgetData data) {
        float targetedExpensesSum = data.getCategorySet()
                .stream()
                .map(BudgetCategory::getTargetExpenses)
                .reduce(0, Integer::sum);

        return category.getTargetExpenses() / targetedExpensesSum;
    }

    private String getCategoryChartName(BudgetCategory category, BudgetData data) {
        int expensesSum = data.getExpenseSet()
                .stream()
                .filter(e -> e.getCategoryUuid().equals(category.getUuid()))
                .filter(BudgetExpense::isInCurrentMonth)
                .map(BudgetExpense::getPrice)
                .reduce(0, Integer::sum);

        return MessageFormat.format(
                "{0} ({1}/{2}€)",
                category.getName(),
                expensesSum,
                category.getTargetExpenses()
        );
    }

    private void loadData() {
        BudgetData budgetData = viewModel.getSharedData().getValue();

        if (budgetData == null) {
            Log.e(LOG_TAG, "Couldn't load data into pie chart because the budget-data is null.");
            Toast.makeText(getContext(), "An error occurred.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<PieEntry> entries = budgetData.getCategorySet()
                .stream()
                .map(e -> new PieEntry(
                        getCategoryChartValue(e, budgetData),
                        getCategoryChartName(e, budgetData))
                )
                .collect(Collectors.toList());

        PieDataSet dataSet = new PieDataSet(entries, "Budget");

        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        List<Integer> colors = budgetData.getCategorySet()
                .stream()
                .map(BudgetCategory::getColorPrimary)
                .collect(Collectors.toList());

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        binding.chartOverview.setData(data);
        binding.chartOverview.highlightValues(null);
        binding.chartOverview.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel = null;
    }
}