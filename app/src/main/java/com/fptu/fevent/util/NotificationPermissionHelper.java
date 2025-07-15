package com.fptu.fevent.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NotificationPermissionHelper {
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;
    
    public static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) 
                == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Trước Android 13 không cần permission
    }
    
    public static void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasNotificationPermission(activity)) {
                ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION
                );
            }
        }
    }
    
    public static void handlePermissionResult(Activity activity, int requestCode, 
            String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Đã cấp quyền thông báo thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, 
                    "Cần cấp quyền thông báo để nhận thông báo từ FEvent", 
                    Toast.LENGTH_LONG).show();
            }
        }
    }
    
    public static void showPermissionRationale(Activity activity) {
        Toast.makeText(activity, 
            "FEvent cần quyền thông báo để gửi các thông báo quan trọng về công việc và lịch họp", 
            Toast.LENGTH_LONG).show();
    }
} 