package com.example.note_application_neko_ru.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDbHelper extends SQLiteOpenHelper {

    public MyDbHelper(@Nullable Context context) {
        super(context, MyConstants.DB_NAME, null, MyConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MyConstants.SQL_CREATE_NOTES_TABLE);
        db.execSQL(MyConstants.SQL_CREATE_REMINDS_TABLE);
        db.execSQL(MyConstants.SQL_CREATE_SUBJECTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(MyConstants.SQL_DELETE_NOTES_TABLE);
        db.execSQL(MyConstants.SQL_DELETE_REMINDS_TABLE);
        db.execSQL(MyConstants.SQL_DELETE_SUBJECTS_TABLE);
        onCreate(db);
    }
}
