package com.example.mybmi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends BaseActivity {

    private TextView txtName, txtEmail;
    private EditText edtNewPassword;
    private Button btnUpdatePassword;
    private DbHelper db;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        db = new DbHelper(this);
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);

        // Muat dan tampilkan data pengguna
        loadUserProfile();

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });
    }
    private void loadUserProfile() {
        String email = sharedPreferences.getString("email", "Tidak Ditemukan");
        String name = db.getUserName(email);

        txtEmail.setText(email);
        if (name != null) {
            txtName.setText(name);
        } else {
            txtName.setText("Nama Tidak Ditemukan");
        }
    }

    private void updatePassword() {
        String email = sharedPreferences.getString("email", null);
        String newPassword = edtNewPassword.getText().toString().trim();

        if (email == null) {
            Toast.makeText(this, "Sesi pengguna tidak ditemukan, silakan login kembali.", Toast.LENGTH_LONG).show();
            return;
        }

        if (newPassword.isEmpty() || newPassword.length() < 6) {
            Toast.makeText(this, "Password baru tidak boleh kosong dan minimal 6 karakter.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUpdated = db.updateUserPassword(email, newPassword);

        if (isUpdated) {
            Toast.makeText(this, "Password berhasil diperbarui!", Toast.LENGTH_SHORT).show();
            edtNewPassword.setText(""); // Kosongkan field
        } else {
            Toast.makeText(this, "Gagal memperbarui password.", Toast.LENGTH_SHORT).show();
        }
    }
}