package com.fptu.fevent.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.fptu.fevent.dao.*;
import com.fptu.fevent.model.*;
import com.fptu.fevent.util.DateConverter;
import com.fptu.fevent.util.IntegerListConverter;
import com.fptu.fevent.util.PermissionConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                User.class, Role.class, Permission.class,
                Team.class, EventInfo.class, Schedule.class,
                Task.class, EventFeedback.class, UserFeedback.class,
                Notification.class
        },
        version = 3
)
@TypeConverters({PermissionConverter.class, DateConverter.class, IntegerListConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "FEvent_Database"
                            )
                            .fallbackToDestructiveMigration() // ✅ Nếu bạn chưa dùng Migration, giữ dòng này
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    public abstract UserDao userDao();
    public abstract RoleDao roleDao();
    public abstract PermissionDao permissionDao();
    public abstract TeamDao teamDao();
    public abstract EventInfoDao eventInfoDao();
    public abstract ScheduleDao scheduleDao();
    public abstract TaskDao taskDao();
    public abstract EventFeedbackDao eventFeedbackDao();
    public abstract UserFeedbackDao userFeedbackDao();
    public abstract NotificationDao notificationDao();
}
