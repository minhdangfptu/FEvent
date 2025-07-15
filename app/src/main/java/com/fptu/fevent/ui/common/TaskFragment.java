package com.fptu.fevent.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.Task;
import com.fptu.fevent.repository.TaskRepository;
import com.fptu.fevent.ui.task.CreateTaskActivity;
import com.fptu.fevent.ui.task.TaskAdapter;
import com.fptu.fevent.ui.task.TaskDetailActivity;
import com.fptu.fevent.ui.task.TaskSummaryActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private FloatingActionButton fabCreateTask;
    private MaterialButton btnTaskSummary;
    private TaskRepository taskRepository;
    private List<Task> taskList;
    private ExecutorService executor;
    private int currentUserRole;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        // Initialize repositories
        Activity activity = getActivity();
        if (activity != null) {
            taskRepository = new TaskRepository(activity.getApplication());
            executor = Executors.newSingleThreadExecutor();
        } else {
            return view; // Thoát sớm nếu không có Activity
        }

        // Get current user role
        SharedPreferences prefs = activity.getSharedPreferences("user_prefs", activity.MODE_PRIVATE);
        currentUserRole = prefs.getInt("role_id", 4); // Default là Member

        // Initialize data list
        taskList = new ArrayList<>();

        initViews(view);
        setupRecyclerView();
        setupFAB();
        setupSummaryButton();
        loadTasks();

        // Xử lý insets
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewTasks);
        fabCreateTask = view.findViewById(R.id.fabCreateTask);
        btnTaskSummary = view.findViewById(R.id.btnTaskSummary);
    }

    private void setupRecyclerView() {
        if (recyclerView != null) {
            taskAdapter = new TaskAdapter(taskList, this::onTaskClick);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(taskAdapter);
        }
    }

    private void setupFAB() {
        if (fabCreateTask != null) {
            // Chỉ hiển thị nút tạo công việc cho admin (1), leader (3), leader_ban (2)
            if (currentUserRole == 1 || currentUserRole == 2 || currentUserRole == 3) {
                fabCreateTask.setVisibility(View.VISIBLE);
                fabCreateTask.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), CreateTaskActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                fabCreateTask.setVisibility(View.GONE);
            }
        }
    }

    private void setupSummaryButton() {
        if (btnTaskSummary != null) {
            // Chỉ hiển thị nút tổng hợp cho admin (1), leader (3), leader_ban (2)
            if (currentUserRole == 1 || currentUserRole == 2 || currentUserRole == 3) {
                btnTaskSummary.setVisibility(View.VISIBLE);
                btnTaskSummary.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), TaskSummaryActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                btnTaskSummary.setVisibility(View.GONE);
            }
        }
    }

    private void loadTasks() {
        if (executor != null) {
            executor.execute(() -> {
                List<Task> tasks = taskRepository.getAll();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (taskList != null && taskAdapter != null) {
                            taskList.clear();
                            taskList.addAll(tasks);
                            taskAdapter.notifyDataSetChanged();

                            // Show/hide empty state
                            View emptyState = getView().findViewById(R.id.emptyState);
                            if (emptyState != null) {
                                if (tasks.isEmpty()) {
                                    emptyState.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                } else {
                                    emptyState.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    private void onTaskClick(Task task) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
            intent.putExtra("task_id", task.id);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh danh sách khi quay lại từ CreateTaskActivity
        loadTasks();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}