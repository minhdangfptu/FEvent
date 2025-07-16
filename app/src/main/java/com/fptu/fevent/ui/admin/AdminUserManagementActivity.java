package com.fptu.fevent.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.fevent.R;
import com.fptu.fevent.model.User;
import com.fptu.fevent.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminUserManagementActivity extends AppCompatActivity implements UserAdapter.UserActionListener {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private UserRepository userRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_management);

        recyclerView = findViewById(R.id.recycler_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRepository = new UserRepository(getApplication());

        adapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(adapter);

        loadUsers();
        setupSearchView();
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.btn_filter).setOnClickListener(v -> showFilterDialog());
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.search_view);

    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.search_view);
        ImageView btnSearch = findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(v -> {
            if (searchView.getVisibility() == View.GONE) {
                searchView.setVisibility(View.VISIBLE);
                searchView.setIconified(false);
                searchView.requestFocus();
            } else {
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchView.setVisibility(View.GONE);
                loadUsers();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return true;
            }
        });
    }

    private void filterUsers(String query) {
        userRepository.searchUsers(query, filteredList -> runOnUiThread(() -> {
            adapter.updateList(filteredList);
        }));
    }

    private void showFilterDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_filter_user, null);
        RadioGroup radioStatus = view.findViewById(R.id.radio_status);

        new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Lọc theo trạng thái")
                .setPositiveButton("Lọc", (dialog, which) -> {
                    int checkedId = radioStatus.getCheckedRadioButtonId();
                    List<User> filtered = new ArrayList<>();

                    for (User user : userList) {
                        if (checkedId == R.id.radio_active && user.getDeactivated_until() == null) {
                            filtered.add(user);
                        } else if (checkedId == R.id.radio_deactivated && user.getDeactivated_until() != null) {
                            filtered.add(user);
                        } else if (checkedId == R.id.radio_all) {
                            filtered.add(user);
                        }
                    }

                    adapter = new UserAdapter(filtered, this);
                    recyclerView.setAdapter(adapter);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }


    private void loadUsers() {
        userRepository.getAllUsers(users -> runOnUiThread(() -> {
            userList.clear();
            userList.addAll(users);
            adapter.notifyDataSetChanged();
        }));
    }

    @Override
    public void onDisable(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận vô hiệu hóa")
                .setMessage("Bạn có chắc chắn muốn vô hiệu hóa tài khoản của " + user.getFullName() + " trong 30 ngày?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_MONTH, 30);

                    userRepository.deactivateUser(user.getId(), cal.getTime());
                    Toast.makeText(this, "Đã vô hiệu hóa tài khoản 30 ngày", Toast.LENGTH_SHORT).show();
                    loadUsers();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDelete(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa tài khoản")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản của " + user.getFullName() + "?\nHành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    userRepository.deleteUserById(user.getId(), success -> runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(this, "Tài khoản đã được xóa", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        } else {
                            Toast.makeText(this, "Xóa tài khoản thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }));
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    //    @Override
//    public void onAssignRole(User user) {
//        Toast.makeText(this, "Phân quyền: " + user.getFullName(), Toast.LENGTH_SHORT).show();
//
//    }
    @Override
    public void onAssignRole(User user) {
        // Inflate the custom dialog layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_assign_role, null);
        TextView tvAssignRoleTitle = view.findViewById(R.id.tv_assign_role_title);
        RadioGroup radioRoleGroup = view.findViewById(R.id.radio_role_group);

        // Set the title with the user's name
        tvAssignRoleTitle.setText("Phân quyền cho: " + user.getFullName());

        // Pre-select the current role of the user
        if (user.getRole_id() != null) {
            for (int i = 0; i < radioRoleGroup.getChildCount(); i++) {
                View child = radioRoleGroup.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) child;
                    // Compare the tag (which holds role_id as String) with current role_id
                    if (radioButton.getTag() != null && String.valueOf(user.getRole_id()).equals(radioButton.getTag().toString())) {
                        radioButton.setChecked(true);
                        break;
                    }
                }
            }
        }


        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    int selectedId = radioRoleGroup.getCheckedRadioButtonId();
                    if (selectedId != -1) {
                        RadioButton selectedRadioButton = view.findViewById(selectedId);
                        String selectedRoleTag = selectedRadioButton.getTag().toString();
                        try {
                            int newRoleId = Integer.parseInt(selectedRoleTag);

                            // Check if the role is actually changing to avoid unnecessary updates
                            if (user.getRole_id() == null || user.getRole_id() != newRoleId) {
                                userRepository.updateUserRole(user.getId(), newRoleId, success -> runOnUiThread(() -> {
                                    if (success) {
                                        Toast.makeText(AdminUserManagementActivity.this, "Đã cập nhật vai trò cho " + user.getFullName(), Toast.LENGTH_SHORT).show();
                                        loadUsers(); // Refresh the list to show updated role
                                    } else {
                                        Toast.makeText(AdminUserManagementActivity.this, "Cập nhật vai trò thất bại.", Toast.LENGTH_SHORT).show();
                                    }
                                }));
                            } else {
                                Toast.makeText(AdminUserManagementActivity.this, "Vai trò không thay đổi.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(AdminUserManagementActivity.this, "Lỗi: Không thể đọc ID vai trò.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(AdminUserManagementActivity.this, "Vui lòng chọn một vai trò.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onAssignTeam(User user) {
        Toast.makeText(this, "Phân nhóm: " + user.getFullName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewInfo(User user) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user_info, null);

        ((TextView) view.findViewById(R.id.tv_info_fullname)).setText(user.getFullName());
        ((TextView) view.findViewById(R.id.tv_info_email)).setText(user.getEmail());
        ((TextView) view.findViewById(R.id.tv_info_role)).setText(mapRole(user.getRole_id()));
        ((TextView) view.findViewById(R.id.tv_info_team)).setText(mapTeam(user.getTeam_id()));
        ((TextView) view.findViewById(R.id.tv_info_status)).setText(user.getDeactivated_until() == null
                ? "Đang hoạt động"
                : "Vô hiệu đến: " + formatDate(user.getDeactivated_until()));

        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Đóng", null)
                .show();
    }

    private String mapRole(Integer roleId) {
        if (roleId == null) return "Không xác định";
        switch (roleId) {
            case 1:
                return "Quản trị viên";
            case 2:
                return "Trưởng ban Tổ chức";
            case 3:
                return "Trưởng ban";
            case 4:
                return "Thành viên";
            default:
                return "Không xác định";
        }
    }

    private String mapTeam(Integer teamId) {
        if (teamId == null) return "Không thuộc nhóm";
        switch (teamId) {
            case 1:
                return "Đội Core – Phụ trách điều hành sự kiện";
            case 2:
                return "Ban Truyền thông – Quản lý truyền thông và quảng bá";
            case 3:
                return "Ban Media - Design – Quản lý ấn phẩm và thiết kế";
            case 4:
                return "Ban Hậu cần - Thiết công – Quản lý hậu cần và thiết công";
            case 5:
                return "Ban Takecare – Quản lý hỗ trợ nhân sự và hoạt động";
            case 6:
                return "Ban Nhân sự - HR – Quản lý nhân lực và tuyển chọn";
            case 7:
                return "Ban Nội dung – Quản lý tổ chức nội dung";
            case 8:
                return "Ban Nhà ma – Quản lý xây dựng và vận hành nhà ma";
            case 9:
                return "Ban Văn thể – Quản lý hoạt động văn nghệ và thể thao";
            case 10:
                return "Ban Đối ngoại – Quản lý đối ngoại và hoạt động ngoại giao";
            default:
                return "Team #" + teamId;
        }
    }

    private String formatDate(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}
