package com.fptu.fevent.database;

import android.app.Application;
import android.util.Log;

import androidx.room.TypeConverters;

import com.fptu.fevent.dao.*;
import com.fptu.fevent.model.*;
import com.fptu.fevent.util.DateConverter;
import com.fptu.fevent.util.IntegerListConverter;
import com.fptu.fevent.util.PermissionConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TypeConverters({DateConverter.class})
public class DatabaseInitializer {

    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd-MM-yyyy", new Locale("vi", "VN"));
    private static final SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("vi", "VN"));

    public static void initialize(Application application) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(application);

            seedTeams(db.teamDao());
            seedPermissions(db.permissionDao());
            seedRoles(db.roleDao());

            int teamCount = db.teamDao().getCount();
            int roleCount = db.roleDao().getCount();

            if (teamCount > 0 && roleCount > 0) {
                seedUsers(db.userDao());
                seedEvents(db.eventInfoDao());
                seedSchedules(db.scheduleDao());
                seedTasks(db.taskDao());
            } else {
                Log.e("SEED", "Không thể seed user vì thiếu Team hoặc Role");
            }
        });
    }

    private static void seedTeams(TeamDao dao) {
        if (dao.getCount() == 0) {
            dao.insertAll(
                    new Team(1, "Đội Core", "Phụ trách điều hành sự kện"),
                    new Team(2, "Ban Truyền thông", "Quản lý truyền thông và quảng bá"),
                    new Team(3, "Ban Media - Design", "Quản lý ấn phẩm và thiết kế"),
                    new Team(4, "Ban Hậu cần - Thiết công", "Quản lý hậu cần và thiết công"),
                    new Team(5, "Ban Takecare", "Quản lý hỗ trợ nhân sự và hoạt động"),
                    new Team(6, "Ban Nhân sự - HR", "Quản lý nhân lực và tuyển chọn"),
                    new Team(7, "Ban Nội dung", "Quản lý tổ chức nội dung"),
                    new Team(8, "Ban Nhà ma", "Quản lý xây dựng và vận hành nhà ma"),
                    new Team(9, "Ban Văn thể", "Quản lý hoạt động văn nghệ và thể thao"),
                    new Team(10, "Ban Đối ngoại", "Quản lý đối ngoại và hoạt động ngoại giao")
            );
            Log.d("SEED", "Seeded Teams");
        }
    }

    private static void seedPermissions(PermissionDao dao) {
        if (dao.getCount() == 0) {
            dao.insertAll(
                    new Permission(1, "create_task", "Tạo nhiệm vụ", "TASK"),
                    new Permission(2, "edit_schedule", "Chỉnh sửa lịch", "SCHEDULE"),
                    new Permission(3, "manage_user", "Quản lý người dùng", "USER"),
                    new Permission(4, "view_event", "Xem sự kiện", "EVENT")
            );
            Log.d("SEED", "Seeded Permissions");
        }
    }

    private static void seedRoles(RoleDao dao) {
        if (dao.getCount() == 0) {
            dao.insertAll(
                    new Role(1, "Admin", "Toàn quyền", Arrays.asList(1, 2, 3, 4)),
                    new Role(2, "Head of Organizing Committee", "Trưởng ban Tổ chức", Arrays.asList(1, 2, 4)),
                    new Role(3, "Leader", "Trưởng ban", Arrays.asList(1, 2, 4)),
                    new Role(4, "Member", "Thành viên", Arrays.asList(4))
            );
            Log.d("SEED", "Seeded Roles");
        }
    }

    private static void seedUsers(UserDao dao) {
        if (dao.getCount() == 0) {
            try {
                Date dob1 = SDF_DATE.parse("26-10-2004");
                Date dob2 = SDF_DATE.parse("18-08-2005");
                Date dob3 = SDF_DATE.parse("20-10-2004");

                String avatarUrl = "https://res.cloudinary.com/dn9txxtvm/image/upload/v1741673471/CarImages/uvvvpxxfl0t85dbtbwmn.png";

                dao.insertAll(
                        new User(1, "ad", "ad@mail.com", "123", "Quản Trị Viên", avatarUrl, dob1, "0900000001", "CLB IT", "TCNTT","Gám đốc", 1, 1),
                        new User(2, "linh.lead", "linh@fevent.vn", "pass456", "Lê Thị Linh", avatarUrl, dob2, "0900000002", "CLB Truyền thông", "Marketing","Thành viên ban Hậu cần", 2, 2),
                        new User(3, "son.member", "son@fevent.vn", "pass789", "Nguyễn Sơn", avatarUrl, dob3, "0900000003", "CLB Âm nhạc", "Kinh tế","Trưởng ban Truyền thông", 3, 3)
                );
                Log.d("SEED", "Seeded Users");
            } catch (ParseException e) {
                Log.e("SEED", "Error parsing date in seedUsers", e);
            }
        }
    }

    private static void seedEvents(EventInfoDao dao) {
        if (dao.getCount() == 0) {
            try {
                dao.insertAll(
                        new EventInfo(1, "Sự kiện 1", SDF_DATETIME.parse("01-01-2025 09:00"), SDF_DATETIME.parse("01-01-2025 17:00"), "Hà Nội", "Mô tả sự kiện 1", 1),
                        new EventInfo(2, "Sự kiện 2", SDF_DATETIME.parse("02-01-2025 09:00"), SDF_DATETIME.parse("02-01-2025 17:00"), "TP.HCM", "Mô tả sự kiện 2", 2)
                );
                Log.d("SEED", "Seeded Events");
            } catch (ParseException e) {
                Log.e("SEED", "Error parsing date in seedEvents", e);
            }
        }
    }

    private static void seedSchedules(ScheduleDao dao) {
        if (dao.getCount() == 0) {
            try {
                Schedule s1 = new Schedule("Họp ban Core", SDF_DATETIME.parse("31-12-2024 14:00"), SDF_DATETIME.parse("31-12-2024 16:00"), "Phòng A101", "Chuẩn bị Halloween", 1);
                Calendar now = Calendar.getInstance();
                now.add(Calendar.HOUR, 1);
                Date s2start = now.getTime();
                now.add(Calendar.HOUR, 2);
                Date s2end = now.getTime();
                Schedule s2 = new Schedule("Họp Truyền thông", s2start, s2end, "Phòng B202", "Review nội dung", 2);

                dao.insertAll(s1, s2);
                Log.d("SEED", "Seeded Schedules");
            } catch (ParseException e) {
                Log.e("SEED", "Error parsing date in seedSchedules", e);
            }
        }
    }

    private static void seedTasks(TaskDao dao) {
        if (dao.getCount() == 0) {
            try {
                dao.insertAll(
                        new Task("Thiết kế poster", "Tạo poster Halloween", "Chưa bắt đầu", SDF_DATE.parse("31-12-2024"), 2, null),
                        new Task("Chuẩn bị âm thanh", "Setup âm thanh", "Đang thực hiện", SDF_DATE.parse("15-01-2025"), null, 4),
                        new Task("Liên hệ khách", "Mời khách VIP", "Hoàn thành", SDF_DATE.parse("25-01-2025"), 3, null)
                );
                Log.d("SEED", "Seeded Tasks");
            } catch (ParseException e) {
                Log.e("SEED", "Error parsing date in seedTasks", e);
            }
        }
    }
}
