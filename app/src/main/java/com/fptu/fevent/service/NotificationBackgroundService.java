package com.fptu.fevent.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class NotificationBackgroundService extends Service {
    private static final String TAG = "NotificationBgService";
    private static final long CHECK_INTERVAL = 60 * 60 * 1000; // 1 hour
    
    private Handler handler;
    private Runnable checkRunnable;
    private NotificationService notificationService;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        
        notificationService = new NotificationService(getApplication());
        handler = new Handler();
        
        checkRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Running periodic notification check");
                
                // Check for task delays and create notifications
                notificationService.checkTaskDelaysAndNotify();
                
                // Clean up old notifications
                notificationService.cleanupOldNotifications();
                
                // Schedule next check
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        
        // Start periodic checking
        handler.post(checkRunnable);
        
        // Return START_STICKY to restart service if killed
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        
        if (handler != null && checkRunnable != null) {
            handler.removeCallbacks(checkRunnable);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
} 