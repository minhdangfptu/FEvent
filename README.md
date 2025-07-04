# 📱 F Event – Ứng dụng quản lý sự kiện di động

F Event là một ứng dụng Android được thiết kế nhằm tối ưu hóa quy trình quản lý sự kiện dành cho sinh viên Đại học FPT. Ứng dụng giúp các ban tổ chức sự kiện vận hành hiệu quả hơn nhờ khả năng tập trung hóa dữ liệu, giao tiếp nội bộ mượt mà và hỗ trợ ra quyết định thông minh.

---

## 📌 Giới thiệu

Ứng dụng F Event được xây dựng trên nền tảng **Android (Java)** với kiến trúc **MVVM** và sử dụng **SQLite thông qua thư viện Room**. Mục tiêu của ứng dụng là thay thế các phương pháp quản lý thủ công như Google Sheet/Excel bằng một hệ thống hiện đại, dễ sử dụng và mở rộng.

---

## 🎯 Mục tiêu dự án

- Giải quyết tình trạng phân tán dữ liệu trong công tác tổ chức sự kiện
- Tăng cường hiệu quả giao tiếp giữa các bộ phận
- Tự động hóa các tác vụ lập kế hoạch, phân công, theo dõi tiến độ và tài chính
- Hỗ trợ đa sự kiện, đa câu lạc bộ với phân quyền rõ ràng

---

## 🔑 Các chức năng chính

### ✅ Chức năng cốt lõi

- **Quản lý nhân sự**: Tạo, phân quyền, giao việc, theo dõi tiến độ
- **Quản lý tài chính**: Lập ngân sách, thống kê thu chi minh bạch
- **Lập kế hoạch sự kiện**: Xây dựng timeline, theo dõi và cảnh báo chậm trễ
- **Giao tiếp nội bộ**: Hệ thống chat, thông báo sự kiện
- **Hỗ trợ đa sự kiện/câu lạc bộ**: Quản lý song song nhiều sự kiện với phân quyền riêng
- **Báo cáo và đánh giá**: Tổng hợp thông tin từ nhân sự, tài chính đến kết quả thực tế

### 👤 Chức năng theo vai trò

| Vai trò               | Quyền hạn chính |
|----------------------|-----------------|
| **Admin**            | Quản lý toàn bộ hệ thống, phê duyệt, giám sát hoạt động |
| **Trưởng BTC**       | Khởi tạo sự kiện, phân công ban, giám sát tiến độ và tài chính |
| **Trưởng ban**       | Quản lý thành viên ban, giao nhiệm vụ, báo cáo tiến độ |
| **Thành viên**       | Nhận việc, báo cáo tiến độ, theo dõi lịch và thông báo sự kiện |

---

## 🧱 Kiến trúc hệ thống

Ứng dụng tuân theo mô hình **MVVM**, giúp tách biệt xử lý dữ liệu với giao diện người dùng.

- **Model**: Các entity như `User`, `Event`, `Task`, `FinanceEntry`, `Club`, `Notification`, `Chat`
- **DAO**: Xử lý truy vấn database với Room (`@Insert`, `@Query`, ...)
- **Repository**: Lớp trung gian giữa ViewModel và DAO, xử lý logic và luồng dữ liệu
- **ViewModel**: Quản lý dữ liệu cho UI, không phụ thuộc trực tiếp vào View
- **View (Activity/Fragment)**: Giao diện người dùng, quan sát LiveData từ ViewModel

---

## 🗃️ Triển khai Database với Room

1. **Entity**: Định nghĩa các bảng dữ liệu
2. **DAO**: Interface chứa các thao tác thêm/sửa/xóa/truy vấn
3. **AppDatabase**: Lớp tổng hợp các DAO
4. **Repository**: Đóng gói logic xử lý dữ liệu, thao tác trong `ExecutorService`
5. **ViewModel**: Quản lý trạng thái dữ liệu, cung cấp LiveData cho View

---

## 💻 Giao diện và ViewModel

Mỗi thực thể có một cặp ViewModel và Activity tương ứng:

- `EventViewModel` + `EventListActivity`, `AddEditEventActivity`
- `UserViewModel` + `UserListActivity`, `AddEditUserActivity`
- `TaskViewModel`, `FinanceEntryViewModel`, `ClubViewModel`, `ChatViewModel`, `NotificationViewModel`…

Các Adapter (ví dụ: `EventAdapter`) dùng để hiển thị danh sách trong RecyclerView.

---

## 🚀 Hướng phát triển tiếp theo

- [ ] Thiết kế giao diện người dùng hoàn chỉnh
- [ ] Thêm logic nghiệp vụ chi tiết (nhắc nhở, phân quyền nâng cao, chat thời gian thực)
- [ ] Tích hợp thư viện UI nâng cao và quản lý thời gian (ex: JodaTime, Material UI)
- [ ] Kiểm thử và debug toàn diện
- [ ] Tối ưu hóa hiệu suất và UX/UI

---

## 🔧 Công nghệ sử dụng

- **Ngôn ngữ:** Java
- **Database:** SQLite + Room
- **Kiến trúc:** MVVM
- **LiveData & ViewModel:** Android Jetpack
- **UI Components:** RecyclerView, Fragment, Material Design

---

## 🏁 Kết luận

F Event là một bước tiến quan trọng trong việc hiện đại hóa công tác tổ chức sự kiện tại Đại học FPT. Với nền tảng kỹ thuật vững chắc, dự án hứa hẹn sẽ mở rộng mạnh mẽ và trở thành công cụ hỗ trợ thiết yếu cho các CLB và sinh viên trong tương lai.

---

### 📬 Liên hệ phát triển

> 📧 Email: minhddhe180032@fpt.edu.vn
> 📚 Trường Đại học FPT  
> 💡 Ứng dụng quản lý sự kiện - FEvent

