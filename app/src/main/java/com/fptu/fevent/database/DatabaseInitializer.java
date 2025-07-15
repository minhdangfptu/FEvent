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
import com.fptu.fevent.model.Task;
import com.fptu.fevent.model.Team;
import com.fptu.fevent.model.User;
import com.fptu.fevent.util.DateConverter;

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
    public static void initialize(Application application) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(application);

            // Seed các bảng không có khóa ngoại trước
            seedTeams(db.teamDao());
            seedPermissions(db.permissionDao());
            seedRoles(db.roleDao());

            // Seed User trước vì EventInfo phụ thuộc vào User
            seedUsers(db.userDao());

            // Sau đó mới seed EventInfo, Schedule và Task
            seedEvents(db.eventInfoDao());
            seedSchedules(db.scheduleDao());
            seedTasks(db.taskDao());

            int teamCount = db.teamDao().getCount();
            int roleCount = db.roleDao().getCount();
            int scheduleCount = db.scheduleDao().getCount();
            Log.d("SEED", "teamCount: " + teamCount + ", roleCount: " + roleCount + ", scheduleCount: " + scheduleCount);
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

                // --- Thay đổi ở đây: Sử dụng URL ảnh từ Cloudinary ---
                String avatarAdminUrl = "https://res.cloudinary.com/dn9txxtvm/image/upload/v1741673471/CarImages/uvvvpxxfl0t85dbtbwmn.png"; // Thay bằng URL ảnh admin thật
                String avatarLinhUrl = "https://res.cloudinary.com/dn9txxtvm/image/upload/v1741673471/CarImages/uvvvpxxfl0t85dbtbwmn.png";   // Thay bằng URL ảnh Linh thật
                String avatarSonUrl = "https://res.cloudinary.com/dn9txxtvm/image/upload/v1741673471/CarImages/uvvvpxxfl0t85dbtbwmn.png";     // Thay bằng URL ảnh Sơn thật
                // ----------------------------------------------------

                dao.insertAll(
                        new User(1, "ad", "ad@mail.com", "123", "Quản Trị Viên", avatarAdminUrl, // <-- Đã thay "image" bằng URL
                                dob1, "0900000001", "CLB IT", "TCNTT","Gám đốc", 1, 1),

                        new User(2, "linh.lead", "linh@fevent.vn", "pass456", "Lê Thị Linh", avatarLinhUrl, // <-- Đã thay "image" bằng URL
                                dob2, "0900000002", "CLB Truyền thông", "Marketing","Thành viên ban Hậu cần", 2, 2),

                        new User(3, "son.member", "son@fevent.vn", "pass789", "Nguyễn Sơn", avatarSonUrl, // <-- Đã thay "image" bằng URL
                                dob3, "0900000003", "CLB Âm nhạc", "Kinh tế","Trưởng ban Truyền thông", 3, 3)
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

    private static void seedSchedules(com.fptu.fevent.dao.ScheduleDao dao) {
        if (dao.getCount() == 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", new Locale("vi", "VN"));
                
                // Schedule 1: Họp ban Core - sắp diễn ra (để test reminder 1h)
                com.fptu.fevent.model.Schedule schedule1 = new com.fptu.fevent.model.Schedule();
                schedule1.title = "Họp ban Core - Chuẩn bị sự kiện Halloween";
                schedule1.start_time = sdf.parse("31-12-2024 14:00");
                schedule1.end_time = sdf.parse("31-12-2024 16:00");
                schedule1.location = "Phòng họp A101";
                schedule1.description = "Thảo luận kế hoạch tổ chức sự kiện Halloween 2024";
                schedule1.team_id = 1; // Đội Core
                
                // Schedule 2: Họp ban Truyền thông - hôm nay (để test reminder 1h)
                Calendar today = Calendar.getInstance();
                today.add(Calendar.HOUR, 1); // 1 giờ nữa
                com.fptu.fevent.model.Schedule schedule2 = new com.fptu.fevent.model.Schedule();
                schedule2.title = "Họp ban Truyền thông - Review content";
                schedule2.start_time = today.getTime();
                today.add(Calendar.HOUR, 2); // kết thúc sau 2 giờ
                schedule2.end_time = today.getTime();
                schedule2.location = "Phòng họp B202";
                schedule2.description = "Review nội dung truyền thông cho sự kiện sắp tới";
                schedule2.team_id = 2; // Ban Truyền thông
                
                // Schedule 3: Họp tổng - không có team cụ thể (để test thông báo cho tất cả)
                Calendar tomorrow = Calendar.getInstance();
                tomorrow.add(Calendar.DAY_OF_MONTH, 1);
                tomorrow.set(Calendar.HOUR_OF_DAY, 9);
                tomorrow.set(Calendar.MINUTE, 0);
                com.fptu.fevent.model.Schedule schedule3 = new com.fptu.fevent.model.Schedule();
                schedule3.title = "Họp tổng toàn thể - Kick-off Q1 2025";
                schedule3.start_time = tomorrow.getTime();
                tomorrow.add(Calendar.HOUR, 3);
                schedule3.end_time = tomorrow.getTime();
                schedule3.location = "Hội trường chính";
                schedule3.description = "Họp tổng toàn thể để kick-off quý 1 năm 2025";
                schedule3.team_id = null; // Không có team cụ thể - cho tất cả
                
                // Schedule 4: Họp ban Media - trong 30 phút (để test reminder ngay lập tức)
                Calendar soon = Calendar.getInstance();
                soon.add(Calendar.MINUTE, 30);
                com.fptu.fevent.model.Schedule schedule4 = new com.fptu.fevent.model.Schedule();
                schedule4.title = "Họp ban Media - Urgent design review";
                schedule4.start_time = soon.getTime();
                soon.add(Calendar.HOUR, 1);
                schedule4.end_time = soon.getTime();
                schedule4.location = "Phòng Design Studio";
                schedule4.description = "Review khẩn cấp các thiết kế poster cho sự kiện";
                schedule4.team_id = 3; // Ban Media - Design
                
                dao.insertAll(schedule1, schedule2, schedule3, schedule4);
                
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private static void seedTasks(com.fptu.fevent.dao.TaskDao dao) {
        if (dao.getCount() == 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("vi", "VN"));
                
                Date dueDate1 = sdf.parse("31-12-2024");
                Date dueDate2 = sdf.parse("15-01-2025");
                Date dueDate3 = sdf.parse("25-01-2025");

                dao.insertAll(
                        new Task("Thiết kế poster sự kiện", 
                                "Tạo poster quảng bá cho sự kiện Halloween FPT", 
                                "Chưa bắt đầu", dueDate1, 2, null),
                        
                        new Task("Chuẩn bị âm thanh", 
                                "Kiểm tra và setup hệ thống âm thanh cho sự kiện", 
                                "Đang thực hiện", dueDate2, null, 4),
                        
                        new Task("Liên hệ khách mời", 
                                "Gửi thư mời và xác nhận tham gia của các khách mời VIP", 
                                "Hoàn thành", dueDate3, 3, null)
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


}
