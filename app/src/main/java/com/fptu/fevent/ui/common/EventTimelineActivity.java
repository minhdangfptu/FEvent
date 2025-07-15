package com.fptu.fevent.ui.common;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.model.Schedule;
import com.fptu.fevent.model.Task;
import com.fptu.fevent.repository.EventInfoRepository;
import com.fptu.fevent.repository.ScheduleRepository;
import com.fptu.fevent.repository.TaskRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventTimelineActivity extends AppCompatActivity implements CalendarAdapter.OnDateClickListener {

    // Calendar views
    private RecyclerView recyclerViewCalendar;
    private MaterialButton btnPrevMonth, btnNextMonth;
    private TextView tvMonthYear;
    
    // Event details views
    private RecyclerView recyclerViewDetails;
    private TextView tvSelectedDate;
    private View tvNoEvents;
    private MaterialCardView cardDetails;
    private MaterialButton btnBack;
    
    // Adapters
    private CalendarAdapter calendarAdapter;
    private EventDetailsAdapter detailsAdapter;
    
    // Data
    private TaskRepository taskRepository;
    private ScheduleRepository scheduleRepository;
    private EventInfoRepository eventInfoRepository;
    private ExecutorService executor;
    
    private List<Task> allTasks;
    private List<Schedule> allSchedules;
    private List<EventInfo> allEvents;
    private List<CalendarItem> selectedDateItems;
    private List<CalendarAdapter.CalendarDay> calendarDays;
    
    private int currentUserId;
    private Date selectedDate;
    private Calendar displayCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_timeline);
        
        // Initialize repositories
        taskRepository = new TaskRepository(getApplication());
        scheduleRepository = new ScheduleRepository(getApplication());
        eventInfoRepository = new EventInfoRepository(getApplication());
        executor = Executors.newSingleThreadExecutor();
        
        // Get current user
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        
        // Initialize data lists
        allTasks = new ArrayList<>();
        allSchedules = new ArrayList<>();
        allEvents = new ArrayList<>();
        selectedDateItems = new ArrayList<>();
        calendarDays = new ArrayList<>();
        
        // Initialize calendar
        displayCalendar = Calendar.getInstance();
        selectedDate = new Date();
        
        initViews();
        setupCalendar();
        setupRecyclerView();
        loadAllData();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        // Calendar views
        recyclerViewCalendar = findViewById(R.id.recyclerViewCalendar);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        
        // Event details views
        recyclerViewDetails = findViewById(R.id.recyclerViewDetails);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvNoEvents = findViewById(R.id.tvNoEvents);
        cardDetails = findViewById(R.id.cardDetails);
        btnBack = findViewById(R.id.btnBack);
        
        btnBack.setOnClickListener(v -> finish());
        
        updateSelectedDateDisplay();
        updateMonthYearDisplay();
    }

    private void setupCalendar() {
        // Setup calendar RecyclerView
        recyclerViewCalendar.setLayoutManager(new GridLayoutManager(this, 7));
        calendarAdapter = new CalendarAdapter(calendarDays, this);
        recyclerViewCalendar.setAdapter(calendarAdapter);
        
        // Setup month navigation
        btnPrevMonth.setOnClickListener(v -> {
            displayCalendar.add(Calendar.MONTH, -1);
            updateMonthYearDisplay();
            generateCalendarDays();
            calendarAdapter.notifyDataSetChanged();
        });
        
        btnNextMonth.setOnClickListener(v -> {
            displayCalendar.add(Calendar.MONTH, 1);
            updateMonthYearDisplay();
            generateCalendarDays();
            calendarAdapter.notifyDataSetChanged();
        });
        
        // Generate initial calendar
        generateCalendarDays();
    }

    private void setupRecyclerView() {
//        detailsAdapter = new EventDetailsAdapter(selectedDateItems);
        recyclerViewDetails.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDetails.setAdapter(detailsAdapter);
    }

    private void loadAllData() {
        executor.execute(() -> {
            // Load all data
            allTasks = taskRepository.getAll();
            allSchedules = scheduleRepository.getAll();
            allEvents = eventInfoRepository.getAll();
            
            runOnUiThread(() -> {
                // Update calendar with event data
                calendarAdapter.updateData(allTasks, allSchedules, allEvents);
                calendarAdapter.setSelectedDate(selectedDate);
                loadSelectedDateDetails();
            });
        });
    }

    private void generateCalendarDays() {
        calendarDays.clear();
        
        Calendar calendar = (Calendar) displayCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        
        // Get first day of week (Sunday = 1)
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        
        // Add empty cells for days before the first day of month
        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarDays.add(new CalendarAdapter.CalendarDay(0, null, false, false, false));
        }
        
        // Get today's date for comparison
        Calendar today = Calendar.getInstance();
        
        // Add all days of the month
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            Date date = calendar.getTime();
            
            boolean isToday = isSameDay(date, today.getTime());
            boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
            
            calendarDays.add(new CalendarAdapter.CalendarDay(day, date, true, isToday, isSunday));
        }
        
        // Fill remaining cells to complete the grid (42 cells = 6 rows × 7 days)
        while (calendarDays.size() < 42) {
            calendarDays.add(new CalendarAdapter.CalendarDay(0, null, false, false, false));
        }
    }

    private void updateMonthYearDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("vi", "VN"));
        tvMonthYear.setText(sdf.format(displayCalendar.getTime()));
    }

    @Override
    public void onDateClick(Date date) {
        selectedDate = date;
        calendarAdapter.setSelectedDate(selectedDate);
        updateSelectedDateDisplay();
        loadSelectedDateDetails();
    }

    private void loadSelectedDateDetails() {
        if (selectedDate == null) return;
        
        executor.execute(() -> {
            List<CalendarItem> items = new ArrayList<>();
            
            // Add tasks due on this date
            for (Task task : allTasks) {
                if (task.due_date != null && isSameDay(task.due_date, selectedDate)) {
                    items.add(new CalendarItem(
                        CalendarItem.TYPE_TASK,
                        task.title,
                        task.description,
                        task.due_date,
                        null,
                        getTaskStatusColor(task.status),
                        "Công việc • " + getTaskStatusText(task.status)
                    ));
                }
            }
            
            // Add schedules on this date
            for (Schedule schedule : allSchedules) {
                if (schedule.start_time != null && isSameDay(schedule.start_time, selectedDate)) {
                    items.add(new CalendarItem(
                        CalendarItem.TYPE_SCHEDULE,
                        schedule.title,
                        schedule.description,
                        schedule.start_time,
                        schedule.end_time,
                        R.color.green_500,
                        "Lịch trình • " + (schedule.location != null ? schedule.location : "Không có địa điểm")
                    ));
                }
            }
            
            // Add events on this date
            for (EventInfo event : allEvents) {
                if (event.start_time != null && isSameDay(event.start_time, selectedDate)) {
                    items.add(new CalendarItem(
                        CalendarItem.TYPE_EVENT,
                        event.name,
                        event.description,
                        event.start_time,
                        event.end_time,
                        R.color.purple_500,
                        "Sự kiện • " + (event.location != null ? event.location : "Không có địa điểm")
                    ));
                }
            }
            
            runOnUiThread(() -> {
                selectedDateItems.clear();
                selectedDateItems.addAll(items);
                detailsAdapter.notifyDataSetChanged();
                
                // Show/hide empty state
                if (items.isEmpty()) {
                    tvNoEvents.setVisibility(View.VISIBLE);
                    recyclerViewDetails.setVisibility(View.GONE);
                } else {
                    tvNoEvents.setVisibility(View.GONE);
                    recyclerViewDetails.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private int getTaskStatusColor(String status) {
        if (status == null) return R.color.gray_500;
        
        switch (status.toLowerCase()) {
            case "completed":
            case "hoàn thành":
                return R.color.green_500;
            case "in_progress":
            case "đang thực hiện":
                return R.color.blue_500;
            case "overdue":
            case "quá hạn":
                return R.color.red_500;
            default:
                return R.color.gray_500;
        }
    }

    private String getTaskStatusText(String status) {
        if (status == null) return "Chưa bắt đầu";
        
        switch (status.toLowerCase()) {
            case "completed":
            case "hoàn thành":
                return "Hoàn thành";
            case "in_progress":
            case "đang thực hiện":
                return "Đang thực hiện";
            case "overdue":
            case "quá hạn":
                return "Quá hạn";
            default:
                return "Chưa bắt đầu";
        }
    }

    private void updateSelectedDateDisplay() {
        if (selectedDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
            tvSelectedDate.setText(sdf.format(selectedDate));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }

    // Calendar Item class to represent different types of items
    public static class CalendarItem {
        public static final int TYPE_TASK = 1;
        public static final int TYPE_SCHEDULE = 2;
        public static final int TYPE_EVENT = 3;
        
        public int type;
        public String title;
        public String description;
        public Date startTime;
        public Date endTime;
        public int colorRes;
        public String subtitle;
        
        public CalendarItem(int type, String title, String description, Date startTime, Date endTime, int colorRes, String subtitle) {
            this.type = type;
            this.title = title;
            this.description = description;
            this.startTime = startTime;
            this.endTime = endTime;
            this.colorRes = colorRes;
            this.subtitle = subtitle;
        }
    }
} 