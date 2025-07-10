package com.fptu.fevent.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fptu.fevent.R;

public class AddEditTeamActivity extends AppCompatActivity {
    public static final String EXTRA_TEAM_ID = "com.fptu.fevent.EXTRA_TEAM_ID";
    public static final String EXTRA_TEAM_NAME = "com.fptu.fevent.EXTRA_TEAM_NAME";
    public static final String EXTRA_TEAM_DESCRIPTION = "com.fptu.fevent.EXTRA_TEAM_DESCRIPTION";

    private EditText editTextName;
    private EditText editTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Đặt theme NoActionBar trước khi setContentView.
        // Điều này đảm bảo Activity không có ActionBar mặc định,
        // cho phép chúng ta sử dụng Toolbar tùy chỉnh.
        setTheme(R.style.Theme_FEvent_NoActionBar);

        setContentView(R.layout.activity_add_edit_team);

        // 1. Tìm và thiết lập Toolbar tùy chỉnh
        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_team);
        setSupportActionBar(toolbar);

        // 2. Kích hoạt và đặt icon cho nút "Up" (nút quay lại)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24);
        }

        // 3. Ánh xạ các View còn lại
        editTextName = findViewById(R.id.edit_text_team_name);
        editTextDescription = findViewById(R.id.edit_text_team_description);
        Button buttonSave = findViewById(R.id.button_save_team);

        // 4. Kiểm tra Intent để xác định chế độ "Thêm" hay "Sửa"
        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_TEAM_ID)) {
            // Chế độ "Sửa"
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Chỉnh sửa Ban");
            }
            editTextName.setText(intent.getStringExtra(EXTRA_TEAM_NAME));
            editTextDescription.setText(intent.getStringExtra(EXTRA_TEAM_DESCRIPTION));
        } else {
            // Chế độ "Thêm"
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thêm ban mới");
            }
        }

        // 5. Thiết lập sự kiện click cho nút Lưu
        buttonSave.setOnClickListener(v -> saveTeam());
    }

    /**
     * Phương thức này được gọi khi người dùng nhấn vào một item trên action bar (bao gồm cả nút back "Up").
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Kiểm tra xem item được nhấn có phải là nút "Up" (android.R.id.home) không
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity hiện tại và quay về màn hình trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Xử lý logic khi nhấn nút Lưu.
     */
    private void saveTeam() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên ban", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo một Intent để chứa dữ liệu trả về
        Intent data = new Intent();
        data.putExtra(EXTRA_TEAM_NAME, name);
        data.putExtra(EXTRA_TEAM_DESCRIPTION, description);

        // Nếu là chế độ "Sửa", gửi trả cả ID về
        int id = getIntent().getIntExtra(EXTRA_TEAM_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_TEAM_ID, id);
        }

        // Đặt kết quả là OK và gửi Intent dữ liệu về cho Activity đã gọi nó
        setResult(RESULT_OK, data);
        // Đóng Activity hiện tại
        finish();
    }
}