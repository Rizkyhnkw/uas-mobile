package com.example.mybmi;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private Cursor cursor;
    public HistoryAdapter(Cursor cursor) {
        this.cursor = cursor;
    }
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }
        // Ambil data dari cursor
        float bmiValue = cursor.getFloat(cursor.getColumnIndexOrThrow("bmi_result"));
        String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

        // Set teks seperti biasa
        holder.txtHistoryBmi.setText(String.format(Locale.getDefault(), "BMI: %.1f", bmiValue));
        holder.txtHistoryCategory.setText("Kategori: " + category);
        holder.txtHistoryDate.setText("Tanggal: " + date);

        // Set warna indikator berdasarkan kategori
        int colorResId;
        switch (category.toLowerCase()) {
            case "kurus":
                colorResId = R.color.category_kurus;
                break;
            case "gemuk":
                colorResId = R.color.category_gemuk;
                break;
            case "obesitas":
                colorResId = R.color.category_obesitas;
                break;
            case "normal":
            default:
                colorResId = R.color.category_normal;
                break;
        }
        holder.categoryIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), colorResId));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView txtHistoryBmi, txtHistoryCategory, txtHistoryDate;
        View categoryIndicator;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHistoryBmi = itemView.findViewById(R.id.txtHistoryBmi);
            txtHistoryCategory = itemView.findViewById(R.id.txtHistoryCategory);
            txtHistoryDate = itemView.findViewById(R.id.txtHistoryDate);
            categoryIndicator = itemView.findViewById(R.id.category_indicator);
        }
    }
}
