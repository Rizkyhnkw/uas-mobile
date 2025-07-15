package com.example.mybmi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbar();
    }

    // func mencari dan mengatur Toolbar setelah layout di-set
    protected void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            // TAMBAHKAN BARIS INI
            setSupportActionBar(toolbar);

            // Anda bisa menghilangkan logo/judul default jika ada
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            ImageView profileImageView = toolbar.findViewById(R.id.profileImageView);
            profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(BaseActivity.this instanceof ProfileActivity)) {
                        Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }}
