package com.fptu.fevent.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.fptu.fevent.dao.*;
import com.fptu.fevent.model.*;
import com.fptu.fevent.util.PermissionConverter;

@Database(
        entities = {
                User.class, Role.class, Permission.class,
                Team.class, EventInfo.class, Schedule.class,
                Task.class, EventFeedback.class, UserFeedback.class
        },
        version = 1
)
@TypeConverters({ PermissionConverter.class })
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
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserDao userDao();

    public abstract RoleDao roleDao();

    public abstract PermissionDao permissionDao();

    public abstract TeamDao teamDao();

    public abstract EventInfoDao eventInfoDao();

    public abstract ScheduleDao scheduleDao();

    public abstract TaskDao taskDao();

    public abstract EventFeedbackDao eventFeedbackDao();

    public abstract UserFeedbackDao userFeedbackDao();
}
