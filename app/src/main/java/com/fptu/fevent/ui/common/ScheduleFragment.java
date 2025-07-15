package com.fptu.fevent.ui.common;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
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

public class ScheduleFragment extends Fragment implements CalendarAdapter.OnDateClickListener {

    private RecyclerView recyclerViewCalendar;
    private MaterialButton btnPrevMonth, btnNextMonth;
    private TextView tvMonthYear;
    private RecyclerView recyclerViewDetails;
    private TextView tvSelectedDate;
    private LinearLayout tvNoEvents;

    private MaterialCardView cardDetails;
    private MaterialButton btnBack;

    private CalendarAdapter calendarAdapter;
    private EventDetailsAdapter detailsAdapter;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Initialize repositories
        Activity activity = getActivity();
        if (activity != null) {
            taskRepository = new TaskRepository(activity.getApplication());
            scheduleRepository = new ScheduleRepository(activity.getApplication());
            eventInfoRepository = new EventInfoRepository(activity.getApplication());
            executor = Executors.newSingleThreadExecutor();
        } else {
            return view; // Thoát sớm nếu không có Activity
        }

        // Get current user
        SharedPreferences prefs = activity.getSharedPreferences("user_prefs", activity.MODE_PRIVATE);
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

        initViews(view);
        setupCalendar();
        setupRecyclerView();
        loadAllData();

        // Xử lý insets
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        return view;
    }

    private void initViews(View view) {
        recyclerViewCalendar = view.findViewById(R.id.recyclerViewCalendar);
        btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        recyclerViewDetails = view.findViewById(R.id.recyclerViewDetails);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvNoEvents = view.findViewById(R.id.tvNoEvents);
        cardDetails = view.findViewById(R.id.cardDetails);
        btnBack = view.findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (cardDetails != null && cardDetails.getVisibility() == View.VISIBLE) {
                    cardDetails.setVisibility(View.GONE);
                } else if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        updateSelectedDateDisplay();
        updateMonthYearDisplay();
    }

    private void setupCalendar() {
        if (recyclerViewCalendar != null) {
            recyclerViewCalendar.setLayoutManager(new GridLayoutManager(getContext(), 7));
            calendarAdapter = new CalendarAdapter(calendarDays, this);
            recyclerViewCalendar.setAdapter(calendarAdapter);

            if (btnPrevMonth != null) {
                btnPrevMonth.setOnClickListener(v -> {
                    if (displayCalendar != null) {
                        displayCalendar.add(Calendar.MONTH, -1);
                        updateMonthYearDisplay();
                        generateCalendarDays();
                        if (calendarAdapter != null) {
                            calendarAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            if (btnNextMonth != null) {
                btnNextMonth.setOnClickListener(v -> {
                    if (displayCalendar != null) {
                        displayCalendar.add(Calendar.MONTH, 1);
                        updateMonthYearDisplay();
                        generateCalendarDays();
                        if (calendarAdapter != null) {
                            calendarAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            generateCalendarDays();
        }
    }

    private void setupRecyclerView() {
        if (recyclerViewDetails != null) {
            detailsAdapter = new EventDetailsAdapter(selectedDateItems);
            recyclerViewDetails.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewDetails.setAdapter(detailsAdapter);
        }
    }

    private void loadAllData() {
        if (executor != null) {
            executor.execute(() -> {
                allTasks = taskRepository.getAll();
                allSchedules = scheduleRepository.getAll();
                allEvents = eventInfoRepository.getAll();

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (calendarAdapter != null) {
                            calendarAdapter.updateData(allTasks, allSchedules, allEvents);
                            calendarAdapter.setSelectedDate(selectedDate);
                            loadSelectedDateDetails();
                        }
                    });
                }
            });
        }
    }

    private void generateCalendarDays() {
        if (calendarDays != null) {
            calendarDays.clear();

            if (displayCalendar != null) {
                Calendar calendar = (Calendar) displayCalendar.clone();
                calendar.set(Calendar.DAY_OF_MONTH, 1);

                int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                for (int i = 1; i < firstDayOfWeek; i++) {
                    calendarDays.add(new CalendarAdapter.CalendarDay(0, null, false, false, false));
                }

                Calendar today = Calendar.getInstance();

                int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int day = 1; day <= daysInMonth; day++) {
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    Date date = calendar.getTime();

                    boolean isToday = isSameDay(date, today.getTime());
                    boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;

                    calendarDays.add(new CalendarAdapter.CalendarDay(day, date, true, isToday, isSunday));
                }

                while (calendarDays.size() < 42) {
                    calendarDays.add(new CalendarAdapter.CalendarDay(0, null, false, false, false));
                }
            }
        }
    }

    private void updateMonthYearDisplay() {
        if (tvMonthYear != null && displayCalendar != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("vi", "VN"));
            tvMonthYear.setText(sdf.format(displayCalendar.getTime()));
        }
    }

    @Override
    public void onDateClick(Date date) {
        if (date != null) {
            selectedDate = date;
            if (calendarAdapter != null) {
                calendarAdapter.setSelectedDate(selectedDate);
            }
            updateSelectedDateDisplay();
            if (cardDetails != null) {
                cardDetails.setVisibility(View.VISIBLE);
            }
            loadSelectedDateDetails();
        }
    }

    private void loadSelectedDateDetails() {
        if (selectedDate == null || executor == null) return;

        executor.execute(() -> {
            List<CalendarItem> items = new ArrayList<>();

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

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (selectedDateItems != null && detailsAdapter != null) {
                        selectedDateItems.clear();
                        selectedDateItems.addAll(items);
                        detailsAdapter.notifyDataSetChanged();

                        if (tvNoEvents != null && recyclerViewDetails != null) {
                            if (items.isEmpty()) {
                                tvNoEvents.setVisibility(View.VISIBLE);
                                recyclerViewDetails.setVisibility(View.GONE);
                            } else {
                                tvNoEvents.setVisibility(View.GONE);
                                recyclerViewDetails.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        });
    }

    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
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
        if (tvSelectedDate != null && selectedDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
            tvSelectedDate.setText(sdf.format(selectedDate));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }

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