package com.fptu.fevent.ui.user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fptu.fevent.R;

import java.util.HashMap;

public class PermissionSettingsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    private HashMap<String, String> permissionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_permission_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initPermissionMap();
        setupPermissionToggles();
    }

    private void initPermissionMap() {
        permissionMap = new HashMap<>();
        permissionMap.put("switch_camera", Manifest.permission.CAMERA);
        permissionMap.put("switch_location", Manifest.permission.ACCESS_FINE_LOCATION);
        permissionMap.put("switch_storage", Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionMap.put("switch_microphone", Manifest.permission.RECORD_AUDIO);
        permissionMap.put("switch_call", Manifest.permission.CALL_PHONE);
        permissionMap.put("switch_sms", Manifest.permission.SEND_SMS);
        permissionMap.put("switch_contacts", Manifest.permission.READ_CONTACTS);
        permissionMap.put("switch_calendar", Manifest.permission.READ_CALENDAR);
        permissionMap.put("switch_sensors", Manifest.permission.BODY_SENSORS);
        permissionMap.put("switch_internet", Manifest.permission.INTERNET); // Luôn granted
    }

    private void setupPermissionToggles() {
        LinearLayout permissionList = findViewById(R.id.permission_container); // ✅ đúng ID

        for (int i = 0; i < permissionList.getChildCount(); i++) {
            View item = permissionList.getChildAt(i);
            if (item.getTag() != null) {
                String tag = item.getTag().toString(); // "Tên quyền|switch_id"
                String[] parts = tag.split("\\|");

                if (parts.length == 2) {
                    String permissionLabel = parts[0];
                    String switchKey = parts[1];
                    String permission = permissionMap.get(switchKey);

                    if (permission == null) continue;

                    TextView tvName = item.findViewById(R.id.permission_name);
                    Switch sw = item.findViewById(R.id.permission_switch);
                    tvName.setText(permissionLabel);

                    boolean granted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
                    sw.setChecked(granted);

                    sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE);
                        } else {
                            Toast.makeText(this, "Vui lòng tắt quyền trong phần Cài đặt của hệ thống", Toast.LENGTH_LONG).show();
                            buttonView.setChecked(true);
                        }
                    });
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            String msg = granted ? "Quyền đã được cấp" : "Quyền bị từ chối";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
