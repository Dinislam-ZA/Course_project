package com.example.note_application_neko_ru.db;

public class MyConstants {
    public static final String EDIT_STATE = "edit_state";
    public static final String LIST_ITEM_INTENT = "list_item_intent";

    public static final String TABLE_NAME = "notes";
    public static final String _ID = "ID";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_DESCRIPTION = "description";
    public static final String COLUMN_NAME_SUBJECT = "subject";
    public static final String COLUMN_NAME_URI = "uri";

    public static final String SECOND_TABLE_NAME = "reminders";
    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_LOCATION = "location" ;
    public static final String COLUMN_NAME_STATE = "state" ;


    public static final String THIRD_TABLE_NAME = "subjects";
    public static final String COLUMN_NAME_SUBJECT_NAME = "name";


    public static final String DB_NAME = "MyDB.db";
    public static final int DB_VERSION = 6;



    public static final String SQL_CREATE_NOTES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TITLE + " TEXT," +
                    COLUMN_NAME_DESCRIPTION + " TEXT," +
                    COLUMN_NAME_SUBJECT + " INTEGER," +
                    COLUMN_NAME_URI + " TEXT)";

    public static final String SQL_CREATE_REMINDS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SECOND_TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TITLE + " TEXT," +
                    COLUMN_NAME_DESCRIPTION + " TEXT," +
                    COLUMN_NAME_DATE + " TEXT," +
                    COLUMN_NAME_TIME + " TEXT," +
                    COLUMN_NAME_STATE + " TEXT," +
                    COLUMN_NAME_LOCATION + " TEXT)";

    public static final String SQL_CREATE_SUBJECTS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + THIRD_TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_SUBJECT_NAME
                    + " TEXT)";

    public static final String SQL_DELETE_NOTES_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String SQL_DELETE_REMINDS_TABLE =
            "DROP TABLE IF EXISTS " + SECOND_TABLE_NAME;

    public static final String SQL_DELETE_SUBJECTS_TABLE =
            "DROP TABLE IF EXISTS " + THIRD_TABLE_NAME;
}
