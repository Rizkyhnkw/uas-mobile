package com.example.mybmi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private NumberPicker edtBerat;
    private NumberPicker edtTinggi;
    private Button btnHitung;
    private TextView txtBmiValue, txtBmiCategory, txtBmiSuggestion;
    private Button btnReset;
    private NumberPicker edtUsia;
    private RadioGroup radioGender;
    private RadioButton radioPria, radioWanita;
    private Button btnLogout;
    private SharedPreferences sharedPreferences;
    private String userEmail;
    private DbHelper db;
    private  Button btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi UI SETELAH setContentView
        edtBerat = findViewById(R.id.edtBerat);
        edtTinggi = findViewById(R.id.edtTinggi);
        btnHitung = findViewById(R.id.btnHitung);
        txtBmiValue = findViewById(R.id.txtBmiValue);
        txtBmiCategory = findViewById(R.id.txtBmiCategory);
        txtBmiSuggestion = findViewById(R.id.txtBmiSuggestion);
        btnReset = findViewById(R.id.btnReset);
        edtUsia = findViewById(R.id.edtUsia);
        radioGender = findViewById(R.id.radioGender);
        radioPria = findViewById(R.id.radioPria);
        radioWanita = findViewById(R.id.radioWanita);
        btnLogout = findViewById(R.id.btnLogout);
        db = new DbHelper(this);
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("email", null);
        btnHistory = findViewById(R.id.btnHistory);

        edtUsia.setMinValue(10);
        edtUsia.setMaxValue(100);
        edtUsia.setValue(25);

        edtTinggi.setMinValue(100);
        edtTinggi.setMaxValue(250);
        edtTinggi.setValue(170);

        edtBerat.setMinValue(30);
        edtBerat.setMaxValue(200);
        edtBerat.setValue(65);
        addManualHistory();



        btnHitung.setOnClickListener(v -> calculateBmi() );


        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtUsia.setValue(25);
                edtTinggi.setValue(170);
                edtBerat.setValue(65);
                txtBmiValue.setText("0.0");
                txtBmiCategory.setText("Kategori");
                txtBmiSuggestion.setText("Saran akan muncul di sini.");
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void calculateBmi() {
        float berat = edtBerat.getValue();
        float tinggiCm = edtTinggi.getValue();

        if (tinggiCm <= 0) {
            Toast.makeText(this, "Tinggi badan harus lebih dari 0!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Formula BMI yang benar
        float tinggiM = tinggiCm / 100.0f;
        float bmi = berat / (tinggiM * tinggiM);

        // Logika kategori dan saran
        String kategori;
        String penjelasan;

        if (bmi < 18.5) {
            kategori = "Kurus";
            penjelasan = "Berat badan Anda di bawah normal. Cobalah mengonsumsi makanan bergizi seimbang.";
        } else if (bmi < 24.9) {
            kategori = "Normal";
            penjelasan = "Berat badan Anda ideal. Pertahankan pola makan sehat dan tetap aktif.";
        } else if (bmi < 29.9) {
            kategori = "Gemuk";
            penjelasan = "Berat badan Anda sedikit berlebih. Perbaiki pola makan dan tingkatkan aktivitas fisik.";
        } else {
            kategori = "Obesitas";
            penjelasan = "Anda termasuk kategori obesitas. Segera konsultasikan dengan ahli gizi.";
        }

        // Tampilkan hasil
        txtBmiValue.setText(String.format("%.1f", bmi));
        txtBmiCategory.setText(kategori);
        txtBmiSuggestion.setText(penjelasan);

        // Simpan ke database
        if (userEmail != null) {
            db.addBmiHistory(userEmail, bmi, kategori);
        }
    }
//    func sementara
    private void addManualHistory() {
        if (userEmail == null) {
            Toast.makeText(this, "Tidak ada user yang login!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ManualHistory", "Mencoba menambahkan data manual untuk: " + userEmail);

        // Data dummy (BMI, Kategori, Tanggal)
        db.addBmiHistoryWithDate(userEmail, 22.5f, "Normal", "2025-07-01 10:00:00");
        db.addBmiHistoryWithDate(userEmail, 23.1f, "Normal", "2025-07-02 11:30:00");
        db.addBmiHistoryWithDate(userEmail, 24.0f, "Normal", "2025-07-03 09:00:00");
        db.addBmiHistoryWithDate(userEmail, 25.5f, "Gemuk", "2025-07-04 15:00:00");

        Toast.makeText(this, "Data riwayat manual berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
    }
}
