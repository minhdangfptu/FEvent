package com.fptu.fevent.ui.common;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fptu.fevent.R;
import com.fptu.fevent.model.Task;
import com.fptu.fevent.model.User;
import com.fptu.fevent.repository.UserRepository;
import com.fptu.fevent.repository.EventInfoRepository;
import com.fptu.fevent.repository.TaskRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ExportEventDataActivity extends AppCompatActivity {
    private UserRepository userRepository;
    private TaskRepository taskRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_event_data);

        userRepository = new UserRepository(getApplication());
        EventInfoRepository eventInfoRepository = new EventInfoRepository(getApplication());
        taskRepository = new TaskRepository(getApplication());

        int userId = getIntent().getIntExtra("userId", -1);

        Button btnExportExcel = findViewById(R.id.btn_export_excel);
        Button btnExportPdf = findViewById(R.id.btn_export_pdf);
        Button btnBack = findViewById(R.id.btn_back);

        userRepository.getUserById(userId, user -> {
            if (user != null && user.getRole_id() == 2) {
                btnExportExcel.setOnClickListener(v -> exportToExcel());
                btnExportPdf.setOnClickListener(v -> exportToPdf());
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Bạn không có quyền xuất dữ liệu!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void exportToExcel() {
        new Thread(() -> {
            try {
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Event Data");

                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("User Name");
                header.createCell(1).setCellValue("Email");
                header.createCell(2).setCellValue("Task Name");
                header.createCell(3).setCellValue("Task Description");

                List<User> users = userRepository.getAllUsersSync();
                List<Task> tasks = taskRepository.getAllTasksSync();
                int rowNum = 1;
                for (User user : users) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(user.getFullName());
                    row.createCell(1).setCellValue(user.getEmail());
                }
                for (Task task : tasks) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(2).setCellValue(task.title);
                    row.createCell(3).setCellValue(task.description);
                }

                File file = new File(Environment.getExternalStorageDirectory(), "EventData.xlsx");
                FileOutputStream fos = new FileOutputStream(file);
                workbook.write(fos);
                fos.close();
                workbook.close();

                runOnUiThread(() -> Toast.makeText(this, "Xuất Excel thành công: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi xuất Excel: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void exportToPdf() {
        new Thread(() -> {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "EventData.pdf");
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                document.add(new Paragraph("Event Data Report"));
                List<User> users = userRepository.getAllUsersSync();
                for (User user : users) {
                    document.add(new Paragraph("Name: " + user.getFullName() + ", Email: " + user.getEmail()));
                }

                List<Task> tasks = taskRepository.getAllTasksSync();
                for (Task task : tasks) {
                    document.add(new Paragraph("Task: " + task.title + ", Description: " + task.description));
                }

                document.close();
                runOnUiThread(() -> Toast.makeText(this, "Xuất PDF thành công: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi xuất PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}