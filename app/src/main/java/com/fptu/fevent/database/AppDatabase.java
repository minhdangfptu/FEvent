
package com.fptu.fevent.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.fptu.fevent.model.*;

@Database(
    entities = {
        User.class, Role.class, Permission.class, PermissionRole.class,
        Team.class, EventInfo.class, Schedule.class,
        Task.class, EventFeedback.class, UserFeedback.class
    },
    version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract RoleDao roleDao();
    public abstract PermissionDao permissionDao();
    public abstract PermissionRoleDao permissionRoleDao();
    public abstract TeamDao teamDao();
    public abstract EventInfoDao eventInfoDao();
    public abstract ScheduleDao scheduleDao();
    public abstract TaskDao taskDao();
    public abstract EventFeedbackDao eventFeedbackDao();
    public abstract UserFeedbackDao userFeedbackDao();
}
