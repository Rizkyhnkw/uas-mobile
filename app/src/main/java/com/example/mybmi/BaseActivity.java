package com.example.mybmi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

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
            setSupportActionBar(toolbar);
            loadProfileImageInToolbar();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            ImageView profileImageView = toolbar.findViewById(R.id.profileImageView);
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            String email = prefs.getString("email", null);
            if (email != null) {
                DbHelper db = new DbHelper(this);
                String photoUriString = db.getUserPhotoUri(email);
                if (photoUriString != null) {
                    Glide.with(this)
                            .load(Uri.parse(photoUriString))
                            .placeholder(R.drawable.user_profile) // Gambar default
                            .into(profileImageView);
                }
            }


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
    }

    private void loadProfileImageInToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) return;

        ImageView profileImageView = toolbar.findViewById(R.id.profileImageView);

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String email = prefs.getString("email", null);

        if (email != null) {
            DbHelper db = new DbHelper(this);
            String photoUriString = db.getUserPhotoUri(email);
            if (photoUriString != null) {
                Glide.with(this)
                        .load(Uri.parse(photoUriString))
                        .placeholder(R.drawable.user_profile) // Gambar default
                        .error(R.drawable.user_profile)
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.user_profile);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Muat ulang gambar setiap kali activity kembali aktif
        loadProfileImageInToolbar();
    }

    protected abstract void loadUserProfile();
}
