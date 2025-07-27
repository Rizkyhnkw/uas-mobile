package com.example.mybmi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 3;
//    Users
    private static final String TABLE_USER = "user";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHOTO_URI = "photo_uri";
//    History
    private static final String TABLE_BMI_HISTORY = "bmi_history";
    private static final String COLUMN_HISTORY_ID = "id";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_BMI_RESULT = "bmi_result";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_DATE = "date";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT," // Tambahkan koma di sini
                + COLUMN_PHOTO_URI + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_BMI_HISTORY_TABLE = "CREATE TABLE " + TABLE_BMI_HISTORY + "("
                + COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_BMI_RESULT + " REAL,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_DATE + " TEXT" + ")";
        db.execSQL(CREATE_BMI_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
//    addusers
    public boolean addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USER, null, values);
        db.close();
        return result != -1;
    }
//    validate users
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_EMAIL + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }
//add history
    public boolean addBmiHistory(String email, float bmi, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

//        date otomatis
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date());
//        return addBmiHistoryWithDate(email, bmi, category, currentDateTime); sementara


        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_BMI_RESULT, bmi);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_DATE, currentDateTime);

        long result = db.insert(TABLE_BMI_HISTORY, null, values);
        db.close();
        return result != -1;

    }
    public Cursor getBmiHistory(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BMI_HISTORY + " WHERE " + COLUMN_USER_EMAIL + " = ?", new String[]{email});
          }

    // Metode untuk menyimpan/memperbarui URI foto
    public boolean updateUserPhoto(String email, String photoUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHOTO_URI, photoUri);
        int rows = db.update(TABLE_USER, values, COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();
        return rows > 0;
    }

    // Metode untuk mendapatkan URI foto
    public String getUserPhotoUri(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_PHOTO_URI}, COLUMN_EMAIL + " = ?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String uri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI));
            cursor.close();
            db.close();
            return uri;
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }

    // function sementara buat generate chart

//    public boolean addBmiHistoryWithDate(String email, float bmi, String category, String date) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_USER_EMAIL, email);
//        values.put(COLUMN_BMI_RESULT, bmi);
//        values.put(COLUMN_CATEGORY, category);
//        values.put(COLUMN_DATE, date); // Menggunakan tanggal dari parameter
//
//        long result = db.insert(TABLE_BMI_HISTORY, null, values);
//        db.close();
//        return result != -1;
//    }
//
    public boolean updateUserPassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        // Memperbarui baris berdasarkan email
        int rowsAffected = db.update(TABLE_USER, values, COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();
        // return true jika ada baris yang terpengaruh (berhasil di-update)
        return rowsAffected > 0;
    }

    // Metode untuk fetch nama pengguna berdasarkan email
    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_NAME}, COLUMN_EMAIL + " = ?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            cursor.close();
            db.close();
            return name;
        }
        db.close();
        return null;
    }
}
