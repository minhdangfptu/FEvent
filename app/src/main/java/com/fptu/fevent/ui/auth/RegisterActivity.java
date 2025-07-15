package com.fptu.fevent.ui.auth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy; // Import này cần thiết
import com.cloudinary.android.MediaManager;
import com.fptu.fevent.R;
import com.fptu.fevent.model.Team;
import com.fptu.fevent.model.User;
import com.fptu.fevent.repository.TeamRepository;
import com.fptu.fevent.repository.UserRepository;
import com.fptu.fevent.util.CloudinaryUtil;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private ImageView imgTogglePassword, imgToggleConfirm, imgProfile, imgChooseImage;
    private CheckBox cbTerms;
    private TextView tvError, tvUploadStatus;
    private CardView errorContainer;
    private ProgressBar progressBarUpload;
    private MaterialAutoCompleteTextView actvTeam;

    private UserRepository userRepo;
    private List<Team> teams;
    private Uri selectedImageUri; // Uri gốc của ảnh được chọn/chụp
    private String currentPhotoPath; // Đường dẫn tuyệt đối cho ảnh chụp từ camera
    private boolean isImageUploading = false;
    private String uploadedImageUrl = null;
    private File tempImageFile; // File tạm để lưu ảnh đã xử lý (nén)

    private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;

    public static final String EXTRA_REGISTER_EMAIL = "extra_register_email";
    private String prefilledEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo Cloudinary sớm nhất có thể.
        // Tốt nhất nên đặt trong lớp Application của bạn, nhưng nếu bắt buộc ở đây thì đặt ngay sau super.onCreate()
        CloudinaryUtil.initCloudinary(this.getApplicationContext());
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);
        imgToggleConfirm = findViewById(R.id.imgToggleConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        tvError = findViewById(R.id.tvError);
        tvUploadStatus = findViewById(R.id.tvUploadStatus);
        tvUploadStatus.setVisibility(View.GONE); // Mặc định ẩn trạng thái upload
        errorContainer = findViewById(R.id.errorContainer);
        TextView tvTerms = findViewById(R.id.tvTerms);
        TextView tvSignIn = findViewById(R.id.tvSignIn);
        TextView btnRegister = findViewById(R.id.btnRegister);
        actvTeam = findViewById(R.id.actvTeam);
        imgProfile = findViewById(R.id.imgProfile);
        imgChooseImage = findViewById(R.id.imgChooseImage);
        progressBarUpload = findViewById(R.id.progressBarUpload);
        if (progressBarUpload != null) {
            progressBarUpload.setVisibility(View.GONE); // Mặc định ẩn ProgressBar
        }

        userRepo = new UserRepository(getApplication());
        loadTeams();

        if (getIntent().getBooleanExtra("termsAccepted", false)) cbTerms.setChecked(true);

        tvTerms.setOnClickListener(v -> startActivity(new Intent(this, TermsOfUseActivity.class)));
        tvSignIn.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        imgTogglePassword.setOnClickListener(v -> togglePassword(edtPassword, imgTogglePassword));
        imgToggleConfirm.setOnClickListener(v -> togglePassword(edtConfirmPassword, imgToggleConfirm));
        imgChooseImage.setOnClickListener(v -> checkPermissionsAndShowImageSourceDialog());

        btnRegister.setOnClickListener(v -> attemptRegister());

        initActivityResultLaunchers();
        // Lấy email đã xác thực từ Intent nếu có
        prefilledEmail = getIntent().getStringExtra(EXTRA_REGISTER_EMAIL);
        if (prefilledEmail != null && !prefilledEmail.isEmpty()) {
            edtEmail.setText(prefilledEmail);
            edtEmail.setEnabled(false);
        }
    }

    private void attemptRegister() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString();
        String passAgain = edtConfirmPassword.getText().toString();
        String teamName = actvTeam.getText().toString().trim();

        int matchedTeamId = -1;
        if (teams != null) { // Đảm bảo teams đã được load
            for (Team t : teams) {
                if (t.name.equals(teamName)) {
                    matchedTeamId = t.id;
                    break;
                }
            }
        }
        final int finalMatchedTeamId = matchedTeamId;

        // 1. Kiểm tra dữ liệu nhập
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || passAgain.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Địa chỉ email không hợp lệ");
            return;
        }
        if (!pass.equals(passAgain)) {
            showError("Mật khẩu không khớp");
            return;
        }
        if (pass.length() < 6) {
            showError("Mật khẩu phải từ 6 ký tự");
            return;
        }
        if (!cbTerms.isChecked()) {
            showError("Bạn phải đồng ý Điều khoản sử dụng");
            return;
        }
        if (finalMatchedTeamId == -1) {
            showError("Vui lòng chọn Ban tham gia");
            return;
        }
        // Kiểm tra xem đã có ảnh được chọn/chụp và lưu tạm chưa
        if (tempImageFile == null || !tempImageFile.exists()) {
            showError("Vui lòng chọn ảnh đại diện");
            return;
        }
        if (isImageUploading) {
            showError("Ảnh đang được tải lên, vui lòng đợi...");
            return;
        }
        // Nếu uploadedImageUrl vẫn null ở đây, có nghĩa là ảnh chưa được upload lên Cloudinary.
        // Cần phải upload trước khi đăng ký.
        // Logic này đã được xử lý bằng cách gọi uploadImageToCloudinary với callback.

        // Nếu mọi kiểm tra ban đầu đều OK, tiến hành upload ảnh và sau đó đăng ký
        uploadImageToCloudinary(() -> {
            // Callback này sẽ chạy SAU KHI ảnh đã upload thành công
            // 2. Kiểm tra email trùng
            userRepo.isEmailExists(email, exists -> runOnUiThread(() -> {
                if (exists) {
                    showError("Email đã được sử dụng");
                } else {
                    // 3. Insert user
                    User newUser = new User(name, email, pass);
                    newUser.team_id = finalMatchedTeamId;
                    newUser.role_id = 4;
                    newUser.name = email.split("@")[0]; // Sử dụng phần trước @ của email làm tên mặc định
                    newUser.image = uploadedImageUrl; // Gán URL ảnh đã tải lên

                    userRepo.insertAsync(newUser, id -> runOnUiThread(() -> {
                        if (id > 0) {
                            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        } else {
                            showError("Đăng ký thất bại – thử lại!");
                        }
                    }));
                }
            }));
        });
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.VISIBLE);
        tvUploadStatus.setVisibility(View.GONE); // Ẩn trạng thái upload khi có lỗi
    }

    private void togglePassword(EditText edt, ImageView img) {
        boolean passwordVisible = edt.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        int type = passwordVisible
                ? (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                : (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        edt.setInputType(type);
        edt.setSelection(edt.length());
        img.setImageResource(passwordVisible ? R.drawable.baseline_remove_red_eye_24 : R.drawable.baseline_visibility_off_24);
    }

    private void loadTeams() {
        TeamRepository teamRepo = new TeamRepository(getApplication());
        teamRepo.getAllAsync(teams -> runOnUiThread(() -> {
            this.teams = teams;
            if (teams == null || teams.isEmpty()) {
                Toast.makeText(this, "Không có dữ liệu Ban", Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> teamNames = new ArrayList<>();
            for (Team t : teams) teamNames.add(t.name);
            actvTeam.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, teamNames));
        }));
    }

    private void initActivityResultLaunchers() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri selectedGalleryUri = result.getData().getData();
                if (selectedGalleryUri != null) {
                    saveImageToTempStorage(selectedGalleryUri);
                }
            }
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result && selectedImageUri != null) { // selectedImageUri đã được gán bởi openCamera()
                saveImageToTempStorage(selectedImageUri);
            } else {
                // Xử lý khi người dùng hủy chụp hoặc chụp thất bại
                if (currentPhotoPath != null) {
                    File photoFile = new File(currentPhotoPath);
                    if (photoFile.exists()) {
                        photoFile.delete(); // Xóa ảnh tạm nếu người dùng hủy chụp
                        Log.d("RegisterActivity", "Deleted temp camera file: " + currentPhotoPath);
                    }
                }
                selectedImageUri = null;
                tempImageFile = null;
                uploadedImageUrl = null; // Reset URL ảnh đã upload
                // Đặt lại imgProfile về ảnh mặc định
                imgProfile.setImageResource(R.drawable.baseline_account_circle_24);
                tvUploadStatus.setVisibility(View.GONE);
                if (progressBarUpload != null) progressBarUpload.setVisibility(View.GONE);
                showError("Bạn chưa chọn ảnh đại diện."); // Thông báo cho người dùng
            }
        });

        requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            boolean allGranted = permissions.values().stream().allMatch(Boolean::booleanValue);
            if (allGranted) {
                showImageSourceDialog();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền truy cập để chọn ảnh.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissionsAndShowImageSourceDialog() {
        List<String> permissionsToRequest = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissionsToRequest.add(Manifest.permission.CAMERA);

        if (permissionsToRequest.isEmpty()) {
            showImageSourceDialog();
        } else {
            requestPermissionsLauncher.launch(permissionsToRequest.toArray(new String[0]));
        }
    }

    private void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Chọn ảnh đại diện")
                .setItems(new String[]{"Chọn từ thư viện", "Chụp ảnh mới"}, (dialog, which) -> {
                    if (which == 0) openGallery();
                    else openCamera();
                }).show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFileForCamera(); // Sử dụng hàm tạo file riêng cho camera
                if (photoFile != null) {
                    selectedImageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                    takePictureLauncher.launch(selectedImageUri);
                }
            } catch (IOException e) {
                Log.e("RegisterActivity", "Error creating image file for camera: " + e.getMessage());
                Toast.makeText(this, "Không thể tạo file ảnh", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm tạo file tạm cho ảnh chụp từ camera, lưu đường dẫn vào currentPhotoPath
    private File createImageFileForCamera() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_CAMERA_" + timeStamp + "_"; // Tên file rõ ràng hơn
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath(); // Lưu đường dẫn cho ảnh chụp
        Log.d("RegisterActivity", "Created camera temp file: " + currentPhotoPath);
        return image;
    }

    // Hàm này được gọi sau khi chọn ảnh từ thư viện hoặc chụp ảnh từ camera
    private void saveImageToTempStorage(Uri sourceUri) {
        try {
            // Xóa file tạm cũ trước khi tạo file mới để tránh cache và rác
            if (tempImageFile != null && tempImageFile.exists()) {
                boolean deleted = tempImageFile.delete();
                Log.d("RegisterActivity", "Deleted old temp image file: " + deleted);
            }

            // Lấy Bitmap từ Uri nguồn
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sourceUri);

            // Tạo thư mục tạm nếu chưa tồn tại
            File tempDir = new File(getFilesDir(), "temp_images");
            if (!tempDir.exists()) tempDir.mkdirs();

            // Tạo file tạm mới với tên duy nhất (timestamp)
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            tempImageFile = new File(tempDir, "profile_" + timeStamp + ".jpg"); // Tên file duy nhất
            FileOutputStream fos = new FileOutputStream(tempImageFile);

            // Nén ảnh (ví dụ: JPEG, chất lượng 80%)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();

            Log.d("RegisterActivity", "Saved new temp image to: " + tempImageFile.getAbsolutePath());

            // Hiển thị ảnh đã lưu tạm
            displayImageFromTempFile();

            // Đặt lại trạng thái upload
            isImageUploading = false;
            uploadedImageUrl = null;
            tvUploadStatus.setVisibility(View.GONE);
            if (progressBarUpload != null) progressBarUpload.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE); // Clear any previous error messages
            errorContainer.setVisibility(View.GONE);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lưu ảnh tạm thất bại", Toast.LENGTH_SHORT).show();
            tempImageFile = null;
            selectedImageUri = null; // Reset nếu lưu tạm thất bại
            uploadedImageUrl = null;
            // Đặt lại ảnh profile về mặc định nếu lỗi
            imgProfile.setImageResource(R.drawable.baseline_account_circle_24);
        }
    }

    // Hàm riêng để hiển thị ảnh từ tempImageFile
    private void displayImageFromTempFile() {
        if (tempImageFile != null && tempImageFile.exists()) {
            Log.d("RegisterActivity", "Displaying image from temp: " + tempImageFile.getAbsolutePath());
            Glide.with(this)
                    .load(tempImageFile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Bắt buộc Glide không dùng cache đĩa
                    .skipMemoryCache(true) // Bắt buộc Glide không dùng cache bộ nhớ
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .error(R.drawable.baseline_account_circle_24)
                    .into(imgProfile);
            imgProfile.setVisibility(View.VISIBLE); // Đảm bảo ImageView hiển thị
        } else {
            // Nếu không có file tạm, hiển thị ảnh mặc định
            imgProfile.setImageResource(R.drawable.baseline_account_circle_24);
        }
    }


    private void uploadImageToCloudinary(Runnable onSuccessCallback) {
        if (tempImageFile == null || !tempImageFile.exists()) {
            showError("Không có ảnh để tải lên.");
            return;
        }

        isImageUploading = true;
        uploadedImageUrl = null; // Đặt lại URL ảnh trước khi tải lên mới
        if (progressBarUpload != null) {
            progressBarUpload.setProgress(0);
            progressBarUpload.setVisibility(View.VISIBLE);
        }
        tvUploadStatus.setText("Đang tải ảnh lên...");
        tvUploadStatus.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE); // Ẩn lỗi cũ khi bắt đầu upload
        errorContainer.setVisibility(View.GONE);


        Uri uploadUri = Uri.fromFile(tempImageFile); // Luôn upload từ file tạm đã được quản lý
        MediaManager.get().upload(uploadUri)
                .option("folder", "user_avatars") // Thư mục lưu trên Cloudinary
                .callback(new com.cloudinary.android.callback.UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Upload Started: " + requestId);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        if (progressBarUpload != null) {
                            int progress = (int) ((double) bytes / totalBytes * 100);
                            progressBarUpload.setProgress(progress);
                            tvUploadStatus.setText("Đang tải: " + progress + "%");
                        }
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        isImageUploading = false;
                        uploadedImageUrl = (String) resultData.get("secure_url");
                        Log.d("Cloudinary", "Upload Success: " + uploadedImageUrl);

                        if (progressBarUpload != null) {
                            progressBarUpload.setVisibility(View.GONE);
                            progressBarUpload.setProgress(0);
                        }
                        runOnUiThread(() -> {
                            tvUploadStatus.setText("Tải ảnh lên thành công!");
                            tvUploadStatus.postDelayed(() -> tvUploadStatus.setVisibility(View.GONE), 3000); // Tự động ẩn sau 3 giây
                            Toast.makeText(RegisterActivity.this, "Tải ảnh lên thành công!", Toast.LENGTH_SHORT).show();
                            tvError.setVisibility(View.GONE); // Ẩn lỗi nếu có
                            errorContainer.setVisibility(View.GONE);
                            onSuccessCallback.run(); // Gọi callback để tiếp tục quá trình đăng ký
                        });
                    }

                    @Override
                    public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        isImageUploading = false;
                        uploadedImageUrl = null;
                        if (progressBarUpload != null) {
                            progressBarUpload.setVisibility(View.GONE);
                            progressBarUpload.setProgress(0);
                        }
                        Log.e("Cloudinary", "Upload Error: " + error.getDescription());
                        runOnUiThread(() -> {
                            showError("Tải ảnh lên thất bại: " + error.getDescription());
                            tvUploadStatus.setVisibility(View.GONE); // Ẩn trạng thái upload khi lỗi
                        });
                    }

                    @Override
                    public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        Log.w("Cloudinary", "Upload Rescheduled: " + error.getDescription());
                    }
                })
                .dispatch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Xóa tất cả file tạm trong thư mục temp_images khi Activity bị hủy để tránh rác
        File tempDir = new File(getFilesDir(), "temp_images");
        if (tempDir.exists() && tempDir.isDirectory()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.delete()) {
                        Log.d("RegisterActivity", "Deleted temp file on destroy: " + file.getName());
                    } else {
                        Log.e("RegisterActivity", "Failed to delete temp file on destroy: " + file.getName());
                    }
                }
            }
        }
        // Cũng xóa file ảnh chụp tạm thời nếu có
        if (currentPhotoPath != null) {
            File cameraTempFile = new File(currentPhotoPath);
            if (cameraTempFile.exists()) {
                boolean deleted = cameraTempFile.delete();
                Log.d("RegisterActivity", "Deleted camera temp file on destroy: " + deleted);
            }
        }
    }

}