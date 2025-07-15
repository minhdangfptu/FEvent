package com.fptu.fevent.ui.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.fptu.fevent.R;
import com.fptu.fevent.repository.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.fptu.fevent.model.User; // Import model User để lấy image URL hiện có
import com.fptu.fevent.dto.UserWithDetails; // Import UserWithDetails

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserEditProfileActivity extends AppCompatActivity {

    private EditText edtUserName, edtUserEmail, edtFullname, edtDob, edtClub, edtDepartment, edtPosition, edtPhoneNum;
    private ImageView btnBack;
    private MaterialButton btnSave;
    private ShapeableImageView imgUserProfile;
    private FloatingActionButton fabAddImage; // Giữ lại FloatingActionButton
    // private Uri selectedImageUri; // Không cần nếu không cho phép chọn ảnh mới
    private UserRepository userRepo;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final int MAX_LENGTH = 50;

    // private ActivityResultLauncher<Intent> pickImageLauncher; // Không cần launcher nếu không chọn ảnh mới

    private String currentImageUrl = null; // Biến để lưu URL ảnh hiện tại của người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_edit_profile);

        userRepo = new UserRepository(getApplication());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        // Không gọi setupImagePicker() nữa
        loadUser();
    }

    // Xóa hoàn toàn phương thức setupImagePicker() vì không cần
    // Xóa hoàn toàn phương thức openImagePicker() vì không cần

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save_profile);

        edtUserName = findViewById(R.id.edt_user_name);
        edtUserEmail = findViewById(R.id.edt_user_email);
        edtFullname = findViewById(R.id.edt_fullname);
        edtPhoneNum = findViewById(R.id.edt_phoneNum);
        edtDob = findViewById(R.id.edt_dob);
        edtDob.setOnClickListener(v -> showDatePickerDialog());
        edtClub = findViewById(R.id.edt_club);
        edtDepartment = findViewById(R.id.edt_department);
        edtPosition = findViewById(R.id.edt_position);
        imgUserProfile = findViewById(R.id.img_user_profile);
        fabAddImage = findViewById(R.id.fab_add_image); // Giữ lại tham chiếu đến FloatingActionButton

        btnBack.setOnClickListener(v -> onBackPressed());
        btnSave.setOnClickListener(v -> saveUser());

        // Thay đổi listener cho fabAddImage để hiển thị Toast "đang phát triển"
        fabAddImage.setOnClickListener(v -> Toast.makeText(this, "Chức năng chỉnh sửa ảnh đang phát triển", Toast.LENGTH_SHORT).show());

        // LƯU Ý: Nếu có ImageView nào khác (ví dụ imgUserProfile) mà bạn muốn nhấn vào cũng hiện Toast
        // bạn có thể thêm listener tương tự ở đây:
        // imgUserProfile.setOnClickListener(v -> Toast.makeText(this, "Chức năng chỉnh sửa ảnh đang phát triển", Toast.LENGTH_SHORT).show());
    }

    private void loadUser() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy user_id", Toast.LENGTH_SHORT).show();
            return;
        }

        userRepo.getUserDetailsWithNames(userId, userDetails -> {
            if (userDetails == null) {
                runOnUiThread(() -> Toast.makeText(this, "Không có người dùng", Toast.LENGTH_SHORT).show());
                return;
            }

            runOnUiThread(() -> {
                edtUserName.setText(userDetails.name);
                edtUserEmail.setText(userDetails.email);
                edtFullname.setText(getSafe(userDetails.fullname));
                edtDob.setText(formatDate(userDetails.date_of_birth));
                edtClub.setText(getSafe(userDetails.club));
                edtPhoneNum.setText(getSafe(userDetails.phone_number));
                edtDepartment.setText(getSafe(userDetails.department));
                edtPosition.setText(getSafe(userDetails.position));

                // Lấy URL ảnh hiện tại từ userDetails và lưu vào currentImageUrl
                currentImageUrl = userDetails.image;

                // Load ảnh đại diện người dùng
                if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(currentImageUrl) // Load URL ảnh hiện tại
                            .placeholder(R.drawable.avatar_default) // Ảnh mặc định khi đang tải
                            .error(R.drawable.avatar_default)     // Ảnh mặc định nếu lỗi
                            .into(imgUserProfile);
                } else {
                    // Nếu không có URL ảnh, hiển thị ảnh mặc định
                    imgUserProfile.setImageResource(R.drawable.avatar_default);
                }
            });
        });
    }

    private void saveUser() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy user_id", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateInput()) return;

        String name = edtUserName.getText().toString().trim();
        String email = edtUserEmail.getText().toString().trim();
        String fullname = edtFullname.getText().toString().trim();
        String phoneNum = edtPhoneNum.getText().toString().trim();
        String dobStr = edtDob.getText().toString().trim();
        String club = edtClub.getText().toString().trim();
        String department = edtDepartment.getText().toString().trim();
        String position = edtPosition.getText().toString().trim();

        Date dob;
        try {
            dob = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dobStr);
        } catch (Exception e) {
            Toast.makeText(this, "Ngày sinh không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Truyền currentImageUrl (URL ảnh hiện tại) xuống UserRepository
        // Không có logic upload ảnh mới ở đây
        userRepo.updateUserById(userId, name, email, fullname, dob, phoneNum, club, department, position, success -> {
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private boolean validateInput() {
        edtUserName.setError(null);
        edtUserEmail.setError(null);
        edtFullname.setError(null);
        edtPhoneNum.setError(null);
        edtDob.setError(null);
        edtClub.setError(null);
        edtDepartment.setError(null);
        edtPosition.setError(null);

        String username = edtUserName.getText().toString().trim();
        if (username.isEmpty()) {
            edtUserName.setError("Tên đăng nhập không được để trống");
            return false;
        }

        String email = edtUserEmail.getText().toString().trim();
        if (email.isEmpty()) {
            edtUserEmail.setError("Email không được để trống");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtUserEmail.setError("Email không hợp lệ");
            return false;
        }

        String fullname = edtFullname.getText().toString().trim();
        if (fullname.isEmpty()) {
            edtFullname.setError("Họ tên không được để trống");
            return false;
        }


        String dobStr = edtDob.getText().toString().trim();
        if (dobStr.isEmpty()) {
            edtDob.setError("Ngày sinh không được để trống");
            return false;
        } else {
            try {
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dobStr);
            } catch (Exception e) {
                edtDob.setError("Định dạng ngày sinh phải là dd/MM/yyyy");
                return false;
            }
        }

        if (edtClub.getText().toString().trim().length() > MAX_LENGTH) {
            edtClub.setError("Tên CLB quá dài (tối đa " + MAX_LENGTH + " ký tự)");
            return false;
        }

        if (edtDepartment.getText().toString().trim().length() > MAX_LENGTH) {
            edtDepartment.setError("Tên ban trong CLB quá dài (tối đa " + MAX_LENGTH + " ký tự)");
            return false;
        }

        if (edtPosition.getText().toString().trim().length() > MAX_LENGTH) {
            edtPosition.setError("Tên chức danh quá dài (tối đa " + MAX_LENGTH + " ký tự)");
            return false;
        }

        return true;
    }

    private String getSafe(Object value) {
        return value != null ? value.toString() : "";
    }

    private String formatDate(Date date) {
        return date != null ? dateFormat.format(date) : "";
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        // Nếu đã có ngày -> hiển thị lại ngày cũ
        String currentDate = edtDob.getText().toString().trim();
        if (!currentDate.isEmpty()) {
            try {
                Date parsedDate = dateFormat.parse(currentDate);
                calendar.setTime(parsedDate);
            } catch (Exception ignored) {
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format lại ngày theo dd/MM/yyyy
                    String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    edtDob.setText(formattedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

}