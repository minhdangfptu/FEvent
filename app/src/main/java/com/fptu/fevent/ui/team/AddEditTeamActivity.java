package com.fptu.fevent.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fptu.fevent.R;

public class AddEditTeamActivity extends AppCompatActivity {
    public static final String EXTRA_TEAM_NAME = "com.fptu.fevent.EXTRA_TEAM_NAME";
    public static final String EXTRA_TEAM_DESCRIPTION = "com.fptu.fevent.EXTRA_TEAM_DESCRIPTION";

    private EditText editTextName;
    private EditText editTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_team);

        editTextName = findViewById(R.id.edit_text_team_name);
        editTextDescription = findViewById(R.id.edit_text_team_description);
        Button buttonSave = findViewById(R.id.button_save_team);

        setTitle("Thêm ban mới");

        buttonSave.setOnClickListener(v -> saveTeam());
    }

    private void saveTeam() {
        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();

        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên ban", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TEAM_NAME, name);
        data.putExtra(EXTRA_TEAM_DESCRIPTION, description);

        setResult(RESULT_OK, data);
        finish();
    }
}