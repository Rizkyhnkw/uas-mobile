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

        edtBerat.setMinValue(1);
        edtBerat.setMaxValue(200);
        edtTinggi.setMinValue(1);
        edtTinggi.setMaxValue(250);
        edtUsia.setMinValue(1);
        edtUsia.setMaxValue(100);



        btnHitung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float berat = edtBerat.getValue();
                float tinggiCm = edtTinggi.getValue();

                if (tinggiCm == 0) {
                    Toast.makeText(MainActivity.this, "Tinggi tidak boleh nol!", Toast.LENGTH_SHORT).show();
                    return;
                }

                float tinggiM = tinggiCm / 100.0f; // pake 100.0f untuk memastikan pembagian float

                float bmi = berat / (tinggiM * tinggiM);

                String kategori;
                String penjelasan;

                if (bmi < 18.5) {
                    kategori = "Kurus";
                    penjelasan = "Berat badan Anda di bawah normal. Cobalah mengonsumsi makanan bergizi seimbang dan tingkatkan asupan kalori harian.";
                } else if (bmi < 24.9) {
                    kategori = "Normal";
                    penjelasan = "Berat badan Anda ideal. Pertahankan pola makan sehat dan tetap aktif secara fisik.";
                } else if (bmi < 29.9) {
                    kategori = "Gemuk";
                    penjelasan = "Berat badan Anda sedikit berlebih. Disarankan untuk memperbaiki pola makan dan meningkatkan aktivitas fisik.";
                } else {
                    kategori = "Obesitas";
                    penjelasan = "Anda termasuk dalam kategori obesitas. Segera konsultasikan dengan ahli gizi atau dokter.";
                }

                // store ke db
                if (userEmail != null) {
                    boolean isSaved = db.addBmiHistory(userEmail, bmi, kategori);
                    if (!isSaved) {
                        Toast.makeText(MainActivity.this, "Gagal menyimpan riwayat", Toast.LENGTH_SHORT).show();
                    }
                }

                txtBmiValue.setText(String.format("%.1f", bmi));
                txtBmiCategory.setText(kategori);
                txtBmiSuggestion.setText(penjelasan);
            }
        });


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
                edtBerat.setValue(1);
                edtTinggi.setValue(1);
                edtUsia.setValue(1);
                txtBmiValue.setText("0.0");
                txtBmiCategory.setText("Kategori");
                txtBmiSuggestion.setText("Saran akan muncul di sini.");
                edtBerat.requestFocus();
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
}
