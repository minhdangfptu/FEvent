package com.fptu.fevent.database;

import android.app.Application;
import android.util.Log;

import androidx.room.TypeConverters;

import com.fptu.fevent.dao.EventInfoDao;
import com.fptu.fevent.dao.PermissionDao;
import com.fptu.fevent.dao.RoleDao;
import com.fptu.fevent.dao.TeamDao;
import com.fptu.fevent.dao.UserDao;
import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.model.Permission;
import com.fptu.fevent.model.Role;
import com.fptu.fevent.model.Team;
import com.fptu.fevent.model.User;
import com.fptu.fevent.util.DateConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TypeConverters({DateConverter.class})
public class DatabaseInitializer {
    public static void initialize(Application application) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(application);

            seedTeams(db.teamDao());
            seedPermissions(db.permissionDao());
            seedRoles(db.roleDao());
            seedEvents(db.eventInfoDao());

            int teamCount = db.teamDao().getCount();
            int roleCount = db.roleDao().getCount();
            Log.d("SEED", "teamCount: " + teamCount + ", roleCount: " + roleCount);

            if (teamCount > 0 && roleCount > 0) {
                seedUsers(db.userDao());
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
        }
    }

    private static void seedUsers(UserDao dao) {
        if (dao.getCount() == 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("vi", "VN"));

                Date dob1 = sdf.parse("26-10-2004");
                Date dob2 = sdf.parse("18-08-2005");
                Date dob3 = sdf.parse("20-10-2004");

                dao.insertAll(
                        new User(1, "ad", "ad@mail.com", "123", "Quản Trị Viên",
                                dob1, "0900000001", "CLB IT", "TCNTT","Gám dốc", 1, 1),

                        new User(2, "linh.lead", "linh@fevent.vn", "pass456", "Lê Thị Linh",
                                dob2, "0900000002", "CLB Truyền thông", "Marketing","Thành viên ban Hậu cần", 2, 2),

                        new User(3, "son.member", "son@fevent.vn", "pass789", "Nguyễn Sơn",
                                dob3, "0900000003", "CLB Âm nhạc", "Kinh tế","Trưởng ban Truyền thông", 3, 3)
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private static void seedEvents(EventInfoDao dao) {
        if (dao.getCount() == 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("vi", "VN"));
                dao.insertAll(
                        new EventInfo(1, "Sự kiện 1", sdf.parse("01-01-2025 09:00"), sdf.parse("01-01-2025 17:00"), "Hà Nội", "Mô tả sự kiện 1", 1),
                        new EventInfo(2, "Sự kiện 2", sdf.parse("02-01-2025 09:00"), sdf.parse("02-01-2025 17:00"), "TP.HCM", "Mô tả sự kiện 2", 2)
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


}
