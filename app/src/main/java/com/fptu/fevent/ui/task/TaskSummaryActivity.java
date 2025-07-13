package com.fptu.fevent.ui.task;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;
import com.fptu.fevent.model.Task;
import com.fptu.fevent.model.Team;
import com.fptu.fevent.model.User;
import com.fptu.fevent.repository.TaskRepository;
import com.fptu.fevent.repository.TeamRepository;
import com.fptu.fevent.repository.UserRepository;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskSummaryActivity extends AppCompatActivity {

    private TextView tvTotalTasks, tvCompletionRate;
    private TextView tvNotStartedCount, tvInProgressCount, tvCompletedCount, tvOverdueCount;
    private PieChart pieChartStatus, pieChartAssignment;
    private LinearLayout statusLegend, assignmentLegend;
    private MaterialButton btnBack;
    
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private TeamRepository teamRepository;
    private ExecutorService executor;
    
    private List<Task> allTasks;
    private int currentUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_summary);
        
        // Khởi tạo repositories
        taskRepository = new TaskRepository(getApplication());
        userRepository = new UserRepository(getApplication());
        teamRepository = new TeamRepository(getApplication());
        executor = Executors.newSingleThreadExecutor();
        
        // Lấy thông tin user hiện tại
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserRole = prefs.getInt("role_id", 4);
        
        // Kiểm tra quyền truy cập
        if (currentUserRole != 1 && currentUserRole != 2 && currentUserRole != 3) {
            Toast.makeText(this, "Bạn không có quyền truy cập trang này", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupCharts();
        loadData();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        tvTotalTasks = findViewById(R.id.tvTotalTasks);
        tvCompletionRate = findViewById(R.id.tvCompletionRate);
        tvNotStartedCount = findViewById(R.id.tvNotStartedCount);
        tvInProgressCount = findViewById(R.id.tvInProgressCount);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        tvOverdueCount = findViewById(R.id.tvOverdueCount);
        
        pieChartStatus = findViewById(R.id.pieChartStatus);
        pieChartAssignment = findViewById(R.id.pieChartAssignment);
        statusLegend = findViewById(R.id.statusLegend);
        assignmentLegend = findViewById(R.id.assignmentLegend);
        
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupCharts() {
        // Setup Status Pie Chart
        setupPieChart(pieChartStatus, "Trạng thái Công việc");
        
        // Setup Assignment Pie Chart
        setupPieChart(pieChartAssignment, "Phân công Công việc");
    }

    private void setupPieChart(PieChart pieChart, String centerText) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText(centerText);
        pieChart.setCenterTextSize(12f);
        
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        
        pieChart.animateY(1400);
        
        // Disable legend (we'll create custom legend)
        pieChart.getLegend().setEnabled(false);
    }

    private void loadData() {
        executor.execute(() -> {
            allTasks = taskRepository.getAll();
            
            runOnUiThread(() -> {
                updateOverviewStats();
                updateStatusChart();
                updateAssignmentChart();
            });
        });
    }

    private void updateOverviewStats() {
        int totalTasks = allTasks.size();
        int completedTasks = 0;
        int notStartedTasks = 0;
        int inProgressTasks = 0;
        int overdueTasks = 0;
        
        Date currentDate = new Date();
        
        for (Task task : allTasks) {
            String status = normalizeStatus(task.status);
            
            switch (status) {
                case "completed":
                    completedTasks++;
                    break;
                case "in_progress":
                    inProgressTasks++;
                    break;
                case "not_started":
                    notStartedTasks++;
                    break;
            }
            
            // Check for overdue tasks
            if (task.due_date != null && task.due_date.before(currentDate) && 
                !status.equals("completed")) {
                overdueTasks++;
            }
        }
        
        // Update UI
        tvTotalTasks.setText(String.valueOf(totalTasks));
        
        float completionRate = totalTasks > 0 ? (float) completedTasks / totalTasks * 100 : 0;
        tvCompletionRate.setText(String.format("%.1f%%", completionRate));
        
        tvNotStartedCount.setText(String.valueOf(notStartedTasks));
        tvInProgressCount.setText(String.valueOf(inProgressTasks));
        tvCompletedCount.setText(String.valueOf(completedTasks));
        tvOverdueCount.setText(String.valueOf(overdueTasks));
    }

    private void updateStatusChart() {
        Map<String, Integer> statusCounts = new HashMap<>();
        statusCounts.put("Chưa bắt đầu", 0);
        statusCounts.put("Đang thực hiện", 0);
        statusCounts.put("Hoàn thành", 0);
        statusCounts.put("Quá hạn", 0);
        
        Date currentDate = new Date();
        
        for (Task task : allTasks) {
            String status = normalizeStatus(task.status);
            
            // Check if task is overdue
            boolean isOverdue = task.due_date != null && task.due_date.before(currentDate) && 
                              !status.equals("completed");
            
            if (isOverdue) {
                statusCounts.put("Quá hạn", statusCounts.get("Quá hạn") + 1);
            } else {
                switch (status) {
                    case "completed":
                        statusCounts.put("Hoàn thành", statusCounts.get("Hoàn thành") + 1);
                        break;
                    case "in_progress":
                        statusCounts.put("Đang thực hiện", statusCounts.get("Đang thực hiện") + 1);
                        break;
                    case "not_started":
                        statusCounts.put("Chưa bắt đầu", statusCounts.get("Chưa bắt đầu") + 1);
                        break;
                }
            }
        }
        
        // Create pie entries
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        
        if (statusCounts.get("Chưa bắt đầu") > 0) {
            entries.add(new PieEntry(statusCounts.get("Chưa bắt đầu"), "Chưa bắt đầu"));
            colors.add(ContextCompat.getColor(this, R.color.gray_500));
        }
        
        if (statusCounts.get("Đang thực hiện") > 0) {
            entries.add(new PieEntry(statusCounts.get("Đang thực hiện"), "Đang thực hiện"));
            colors.add(ContextCompat.getColor(this, R.color.blue_500));
        }
        
        if (statusCounts.get("Hoàn thành") > 0) {
            entries.add(new PieEntry(statusCounts.get("Hoàn thành"), "Hoàn thành"));
            colors.add(ContextCompat.getColor(this, R.color.green_500));
        }
        
        if (statusCounts.get("Quá hạn") > 0) {
            entries.add(new PieEntry(statusCounts.get("Quá hạn"), "Quá hạn"));
            colors.add(ContextCompat.getColor(this, R.color.red_500));
        }
        
        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "Không có dữ liệu"));
            colors.add(ContextCompat.getColor(this, R.color.gray_300));
        }
        
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new PercentFormatter(pieChartStatus));
        
        PieData data = new PieData(dataSet);
        pieChartStatus.setData(data);
        pieChartStatus.invalidate();
        
        // Update custom legend
        updateStatusLegend(statusCounts, colors);
    }

    private void updateAssignmentChart() {
        Map<String, Integer> assignmentCounts = new HashMap<>();
        assignmentCounts.put("Cá nhân", 0);
        assignmentCounts.put("Nhóm", 0);
        assignmentCounts.put("Chưa phân công", 0);
        
        for (Task task : allTasks) {
            if (task.assigned_to != null) {
                assignmentCounts.put("Cá nhân", assignmentCounts.get("Cá nhân") + 1);
            } else if (task.team_id != null) {
                assignmentCounts.put("Nhóm", assignmentCounts.get("Nhóm") + 1);
            } else {
                assignmentCounts.put("Chưa phân công", assignmentCounts.get("Chưa phân công") + 1);
            }
        }
        
        // Create pie entries
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        
        if (assignmentCounts.get("Cá nhân") > 0) {
            entries.add(new PieEntry(assignmentCounts.get("Cá nhân"), "Cá nhân"));
            colors.add(ContextCompat.getColor(this, R.color.blue_600));
        }
        
        if (assignmentCounts.get("Nhóm") > 0) {
            entries.add(new PieEntry(assignmentCounts.get("Nhóm"), "Nhóm"));
            colors.add(ContextCompat.getColor(this, R.color.purple_500));
        }
        
        if (assignmentCounts.get("Chưa phân công") > 0) {
            entries.add(new PieEntry(assignmentCounts.get("Chưa phân công"), "Chưa phân công"));
            colors.add(ContextCompat.getColor(this, R.color.gray_400));
        }
        
        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "Không có dữ liệu"));
            colors.add(ContextCompat.getColor(this, R.color.gray_300));
        }
        
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new PercentFormatter(pieChartAssignment));
        
        PieData data = new PieData(dataSet);
        pieChartAssignment.setData(data);
        pieChartAssignment.invalidate();
        
        // Update custom legend
        updateAssignmentLegend(assignmentCounts, colors);
    }

    private void updateStatusLegend(Map<String, Integer> statusCounts, ArrayList<Integer> colors) {
        statusLegend.removeAllViews();
        
        String[] statuses = {"Chưa bắt đầu", "Đang thực hiện", "Hoàn thành", "Quá hạn"};
        int[] statusColors = {
            ContextCompat.getColor(this, R.color.gray_500),
            ContextCompat.getColor(this, R.color.blue_500),
            ContextCompat.getColor(this, R.color.green_500),
            ContextCompat.getColor(this, R.color.red_500)
        };
        
        for (int i = 0; i < statuses.length; i++) {
            int count = statusCounts.get(statuses[i]);
            if (count > 0) {
                addLegendItem(statusLegend, statuses[i], count, statusColors[i]);
            }
        }
    }

    private void updateAssignmentLegend(Map<String, Integer> assignmentCounts, ArrayList<Integer> colors) {
        assignmentLegend.removeAllViews();
        
        String[] assignments = {"Cá nhân", "Nhóm", "Chưa phân công"};
        int[] assignmentColors = {
            ContextCompat.getColor(this, R.color.blue_600),
            ContextCompat.getColor(this, R.color.purple_500),
            ContextCompat.getColor(this, R.color.gray_400)
        };
        
        for (int i = 0; i < assignments.length; i++) {
            int count = assignmentCounts.get(assignments[i]);
            if (count > 0) {
                addLegendItem(assignmentLegend, assignments[i], count, assignmentColors[i]);
            }
        }
    }

    private void addLegendItem(LinearLayout parent, String label, int count, int color) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, 8, 0, 8);
        
        // Color indicator
        View colorIndicator = new View(this);
        LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(24, 24);
        colorParams.setMarginEnd(12);
        colorIndicator.setLayoutParams(colorParams);
        colorIndicator.setBackgroundColor(color);
        
        // Label
        TextView labelView = new TextView(this);
        labelView.setText(label + " (" + count + ")");
        labelView.setTextSize(12);
        labelView.setTextColor(ContextCompat.getColor(this, R.color.gray_700));
        
        item.addView(colorIndicator);
        item.addView(labelView);
        parent.addView(item);
    }

    private String normalizeStatus(String status) {
        if (status == null) return "not_started";
        
        switch (status.toLowerCase()) {
            case "completed":
            case "hoàn thành":
                return "completed";
            case "in_progress":
            case "đang thực hiện":
                return "in_progress";
            case "overdue":
            case "quá hạn":
                return "overdue";
            default:
                return "not_started";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
} 