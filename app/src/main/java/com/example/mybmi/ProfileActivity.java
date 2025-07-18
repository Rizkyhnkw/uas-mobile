package com.example.mybmi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity {

    private TextView txtName, txtEmail;
    private EditText edtNewPassword;
    private Button btnUpdatePassword;
    private DbHelper db;
    private SharedPreferences sharedPreferences;
    private CircleImageView profileImage;
    private Button btnChangePhoto;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

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
        profileImage = findViewById(R.id.profilImage);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            saveAndLoadImage(selectedImageUri);
                        }
                    }
                }
        );

        // Muat dan tampilkan data pengguna
        loadUserProfile();

        btnChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });
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

    private void saveAndLoadImage(Uri imageUri) {
        String email = sharedPreferences.getString("email", null);
        if (email != null) {
            // Simpan URI ke database
            db.updateUserPhoto(email, imageUri.toString());
            // Muat gambar ke ImageView menggunakan Glide
            Glide.with(this).load(imageUri).into(profileImage);
            Toast.makeText(this, "Foto profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
        }}
        @Override
        protected void loadUserProfile() { // Override metode ini jika ada di Base
            String email = sharedPreferences.getString("email", "Tidak Ditemukan");
            String name = db.getUserName(email);
            // ... kode untuk memuat nama dan email


            txtEmail.setText(email);
            if (name != null) {
                txtName.setText(name);
            } else {
                txtName.setText("Nama Tidak Ditemukan");
            }
            // Muat foto profil
            String photoUriString = db.getUserPhotoUri(email);
            if (photoUriString != null) {
                Uri photoUri = Uri.parse(photoUriString);
                Glide.with(this).load(photoUri).placeholder(R.drawable.user_profile).into(profileImage);
            }
        }
}