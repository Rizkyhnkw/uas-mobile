package com.example.mybmi;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends BaseActivity {
    private RecyclerView recyclerViewHistory;
    private DbHelper db;
    private HistoryAdapter adapter;
    private LineChart lineChart;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = new DbHelper(this);
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        lineChart = findViewById(R.id.lineChart);

        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");
        Cursor cursor = db.getBmiHistory(userEmail);
        adapter = new HistoryAdapter(cursor);
        recyclerViewHistory.setAdapter(adapter);

//        tampilkan chart
        setupLineChart();
        loadChartData(cursor);

    }
    @Override
    protected void loadUserProfile() {
    }

    public void setupLineChart() {
        lineChart.getDescription().setEnabled(false); // Menghilangkan deskripsi
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
    }
    private void loadChartData(Cursor cursor) {
        ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> xLabels = new ArrayList<>();
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat chartLabelFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
//pindah cursor ke awal buat read data
        if (cursor.moveToFirst()) {
            int i = 0;
            do {
                float bmi = cursor.getFloat(cursor.getColumnIndexOrThrow("bmi_result"));
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                entries.add(new Entry(i, bmi));

                // Format tanggal untuk label
                try {
                    Date date = dbFormat.parse(dateStr);
                    xLabels.add(chartLabelFormat.format(date));
                } catch (ParseException e) {
                    xLabels.add(""); // Tambah label kosong jika ada error
                }
                i++;
            } while (cursor.moveToNext());
        }
//        cek data minimum
        if ((entries.size() < 2)){
            lineChart.clear();
            lineChart.setNoDataText("Minimal 2 data untuk menampilkan grafik");
            lineChart.invalidate();
            return;
        }

//        set value X-Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Pastikan value adalah index yang valid
                if (value >= 0 && value < xLabels.size()) {
                    return xLabels.get((int) value);
                }
                return "";
            }
        });

        LineDataSet dataSet = new LineDataSet(entries, "Nilai BMI");
//        Styling dulu
        dataSet.setColor(ContextCompat.getColor(this, R.color.bt));
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.bt));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(ContextCompat.getColor(this, R.color.bt));
        dataSet.setFillAlpha(50);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); //refresh chart
    }
}
