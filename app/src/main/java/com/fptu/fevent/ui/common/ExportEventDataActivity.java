package com.fptu.fevent.ui.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.fevent.R;
import com.fptu.fevent.model.EventInfo;
import com.fptu.fevent.model.Task;
import com.fptu.fevent.model.User;
import com.fptu.fevent.repository.EventInfoRepository;
import com.fptu.fevent.repository.TaskRepository;
import com.fptu.fevent.repository.UserRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExportEventDataActivity extends AppCompatActivity {
    private UserRepository userRepository;
    private EventInfoRepository eventInfoRepository;
    private TaskRepository taskRepository;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_event_data);

        userRepository = new UserRepository(getApplication());
        eventInfoRepository = new EventInfoRepository(getApplication());
        taskRepository = new TaskRepository(getApplication());
        dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

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

                // Sheet Events
                Sheet eventSheet = workbook.createSheet("Events");
                Row eventHeader = eventSheet.createRow(0);
                eventHeader.createCell(0).setCellValue("ID");
                eventHeader.createCell(1).setCellValue("Event Name");
                eventHeader.createCell(2).setCellValue("Start Date");
                eventHeader.createCell(3).setCellValue("End Date");
                eventHeader.createCell(4).setCellValue("Location");
                eventHeader.createCell(5).setCellValue("Description");

                List<EventInfo> events = eventInfoRepository.getAllEventsSync();
                int rowNum = 1;
                for (EventInfo event : events) {
                    Row row = eventSheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(event.id);
                    row.createCell(1).setCellValue(event.name);
                    row.createCell(2).setCellValue(event.start_time != null ? dateFormat.format(event.start_time) : "");
                    row.createCell(3).setCellValue(event.end_time != null ? dateFormat.format(event.end_time) : "");
                    row.createCell(4).setCellValue(event.location);
                    row.createCell(5).setCellValue(event.description);
                }

                // Sheet Users
                Sheet userSheet = workbook.createSheet("Users");
                Row userHeader = userSheet.createRow(0);
                userHeader.createCell(0).setCellValue("User ID");
                userHeader.createCell(1).setCellValue("Full Name");
                userHeader.createCell(2).setCellValue("Email");
                userHeader.createCell(3).setCellValue("Role ID");

                List<User> users = userRepository.getAllUsersSync();
                rowNum = 1;
                for (User user : users) {
                    Row row = userSheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(user.getId());
                    row.createCell(1).setCellValue(user.getFullName());
                    row.createCell(2).setCellValue(user.getEmail());
                    row.createCell(3).setCellValue(user.getRole_id());
                }

                // Sheet Tasks
                Sheet taskSheet = workbook.createSheet("Tasks");
                Row taskHeader = taskSheet.createRow(0);
                taskHeader.createCell(0).setCellValue("Task ID");
                taskHeader.createCell(1).setCellValue("Title");
                taskHeader.createCell(2).setCellValue("Description");
                taskHeader.createCell(3).setCellValue("Team ID");

                List<Task> tasks = taskRepository.getAllTasksSync();
                rowNum = 1;
                for (Task task : tasks) {
                    Row row = taskSheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(task.id);
                    row.createCell(1).setCellValue(task.title);
                    row.createCell(2).setCellValue(task.description);
                    row.createCell(3).setCellValue(task.team_id);
                }

                // Auto-size columns
                for (int i = 0; i <= 5; i++) eventSheet.autoSizeColumn(i);
                for (int i = 0; i <= 3; i++) userSheet.autoSizeColumn(i);
                for (int i = 0; i <= 3; i++) taskSheet.autoSizeColumn(i);

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

                document.add(new Paragraph("Event Data Report").setBold().setFontSize(16));
                document.add(new Paragraph(" "));

                // Events
                document.add(new Paragraph("EVENTS").setBold().setFontSize(14));
                List<EventInfo> events = eventInfoRepository.getAllEventsSync();
                for (EventInfo event : events) {
                    document.add(new Paragraph(String.format("ID: %d", event.id)));
                    document.add(new Paragraph("Event Name: " + event.name));
                    document.add(new Paragraph("Start Date: " + (event.start_time != null ? dateFormat.format(event.start_time) : "N/A")));
                    document.add(new Paragraph("End Date: " + (event.end_time != null ? dateFormat.format(event.end_time) : "N/A")));
                    document.add(new Paragraph("Location: " + event.location));
                    document.add(new Paragraph("Description: " + event.description));
                    document.add(new Paragraph("----------------------"));
                }

                document.add(new Paragraph(" "));

                // Users
                document.add(new Paragraph("USERS").setBold().setFontSize(14));
                List<User> users = userRepository.getAllUsersSync();
                for (User user : users) {
                    document.add(new Paragraph(String.format("ID: %d - Name: %s - Email: %s - Role: %d",
                            user.getId(), user.getFullName(), user.getEmail(), user.getRole_id())));
                }

                document.add(new Paragraph(" "));

                // Tasks
                document.add(new Paragraph("TASKS").setBold().setFontSize(14));
                List<Task> tasks = taskRepository.getAllTasksSync();
                for (Task task : tasks) {
                    document.add(new Paragraph(String.format("ID: %d - Title: %s", task.id, task.title)));
                    document.add(new Paragraph("Description: " + task.description));
                    document.add(new Paragraph("Team ID: " + task.team_id));
                    document.add(new Paragraph("----------------------"));
                }

                document.close();
                runOnUiThread(() -> Toast.makeText(this, "Xuất PDF thành công: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Lỗi khi xuất PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
