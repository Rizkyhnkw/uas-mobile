package com.example.mybmi;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerViewHistory;
    private DbHelper db;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        db = new DbHelper(this);
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");
        Cursor cursor = db.getBmiHistory(userEmail);
        adapter = new HistoryAdapter(cursor);
        recyclerViewHistory.setAdapter(adapter);

    }
}