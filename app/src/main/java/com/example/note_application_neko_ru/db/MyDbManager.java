package com.example.note_application_neko_ru.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.note_application_neko_ru.adapter.RcvItemList;
import com.example.note_application_neko_ru.adapter.RcvTasksItem;
import com.example.note_application_neko_ru.adapter.SubjectItem;

import java.util.ArrayList;
import java.util.List;

public class MyDbManager {
    static public Integer reminders_current_id;
    private Context context;
    private MyDbHelper myDbHelper;
    private SQLiteDatabase db;
    public long reminder_current_id = 0;

    public MyDbManager(Context context){
        this.context = context;
        myDbHelper = new MyDbHelper(context);
    }


    public void OpenDb(){
        db = myDbHelper.getWritableDatabase();
    }

    public void InsertToDB(String title, String description, int subject, String uri){
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.COLUMN_NAME_TITLE,title);
        cv.put(MyConstants.COLUMN_NAME_DESCRIPTION,description);
        cv.put(MyConstants.COLUMN_NAME_URI,uri);
        cv.put(MyConstants.COLUMN_NAME_SUBJECT, subject);
        db.insert(MyConstants.TABLE_NAME,null,cv);
    }

    public void InsertToDB(String title, String description, String date, String time, String location,String state){
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.COLUMN_NAME_TITLE, title);
        cv.put(MyConstants.COLUMN_NAME_DESCRIPTION, description);
        cv.put(MyConstants.COLUMN_NAME_DATE, date);
        cv.put(MyConstants.COLUMN_NAME_TIME, time);
        cv.put(MyConstants.COLUMN_NAME_LOCATION, location);
        cv.put(MyConstants.COLUMN_NAME_STATE, state);
        reminder_current_id = db.insert(MyConstants.SECOND_TABLE_NAME, null, cv);
    }

    public void InsertToDB(String name){
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.COLUMN_NAME_SUBJECT_NAME, name);
        db.insert(MyConstants.THIRD_TABLE_NAME, null, cv);
    }


    public void GetNoteFromDB(String searchtitle, SubjectItem subject, OnDataRecived OnDataReceived){
        List<RcvItemList> TempList = new ArrayList<>();
        String selection;
        if (subject == null){
            selection = MyConstants.COLUMN_NAME_TITLE + " like ?";
        }
        else {
            selection = MyConstants.COLUMN_NAME_TITLE + " like ? and " + MyConstants.COLUMN_NAME_SUBJECT + " like " + subject.getId();
        }
        Cursor cursor = db.query(MyConstants.TABLE_NAME, null, selection, new String[]{"%" + searchtitle + "%"}, null, null, null);

        while (cursor.moveToNext()){
            RcvItemList tempitem = new RcvItemList();
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_TITLE));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_DESCRIPTION));
            @SuppressLint("Range") String uri = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_URI));
            @SuppressLint("Range") int sub = cursor.getInt(cursor.getColumnIndex(MyConstants.COLUMN_NAME_SUBJECT));
            @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex(MyConstants._ID));
            tempitem.setTitle(title);
            tempitem.setDescription(description);
            tempitem.setSubject(sub);
            tempitem.setUri(uri);
            tempitem.setId(_id);
            TempList.add(tempitem);
        }
        cursor.close();

        OnDataReceived.OnReceivedInterface(TempList);
    }

    public void GetTasksFromDB(String searchtitle, OnDataRecived onDataReceived){
        List<RcvTasksItem> TempList = new ArrayList<>();
        String selection = MyConstants.COLUMN_NAME_TITLE + " like ?";
        Cursor cursor = db.query(MyConstants.SECOND_TABLE_NAME, null, selection, new String[]{"%" + searchtitle + "%"}, null, null, null);

        while (cursor.moveToNext()){
            RcvTasksItem tempitem = new RcvTasksItem();
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_TITLE));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_DESCRIPTION));
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_DATE));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_TIME));
            @SuppressLint("Range") String loc = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_LOCATION));
            @SuppressLint("Range") String state = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_STATE));
            @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex(MyConstants._ID));
            tempitem.setTitle(title);
            tempitem.setDescription(description);
            tempitem.setDate(date);
            tempitem.setTime(time);
            tempitem.setLocation(loc);
            tempitem.setState(state);
            tempitem.setId(_id);
            TempList.add(tempitem);
        }
        cursor.close();

        onDataReceived.OnTaskReceivedInterface(TempList);
    }

    public RcvTasksItem GetTaskFromDB(int ID){
        String selection = MyConstants._ID + " like ?";
        Cursor cursor = db.query(MyConstants.SECOND_TABLE_NAME, null, null, null, null, null, null);

        RcvTasksItem tempitem = new RcvTasksItem();


        while (cursor.moveToNext() && tempitem.getId() != ID){
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_TITLE));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_DESCRIPTION));
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_DATE));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_TIME));
            @SuppressLint("Range") String loc = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_LOCATION));
            @SuppressLint("Range") String state = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_STATE));
            @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex(MyConstants._ID));
            tempitem.setTitle(title);
            tempitem.setDescription(description);
            tempitem.setDate(date);
            tempitem.setTime(time);
            tempitem.setLocation(loc);
            tempitem.setState(state);
            tempitem.setId(_id);
        }

        cursor.close();

        return tempitem;
    }

    public SubjectItem GetSubjectFromDB(int ID){
        Cursor cursor = db.query(MyConstants.THIRD_TABLE_NAME, null, null, null, null, null, null);

        SubjectItem tempitem = new SubjectItem();


        while (cursor.moveToNext() && tempitem.getId() != ID){
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_SUBJECT_NAME));
            @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex(MyConstants._ID));
            tempitem.setName(name);
            tempitem.setId(_id);
        }

        cursor.close();

        return tempitem;
    }

    public List<SubjectItem> GetAllSubjects(){
        List<SubjectItem> subjects = new ArrayList<>();
        Cursor cursor = db.query(MyConstants.THIRD_TABLE_NAME, null, null, null, null, null, null);

        SubjectItem frstsub = new SubjectItem();
        frstsub.setName("Без темы");
        frstsub.setId(0);
        subjects.add(frstsub);

        while (cursor.moveToNext()){
            SubjectItem tempSubject = new SubjectItem();
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_SUBJECT_NAME));
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MyConstants._ID));
            tempSubject.setName(name);
            Log.d("s",tempSubject.getName());
            tempSubject.setId(id);
            subjects.add(tempSubject);
        }
        return subjects;
    }

    public long CountNotes(){
        return DatabaseUtils.queryNumEntries(db,MyConstants.TABLE_NAME);
    }

    public long CountReminders(){
        return DatabaseUtils.queryNumEntries(db,MyConstants.SECOND_TABLE_NAME);
    }

    public long CountGpsReminders(){
        String selection = MyConstants.COLUMN_NAME_DATE + " = 'empty'";
        return DatabaseUtils.queryNumEntries(db,MyConstants.SECOND_TABLE_NAME, selection);
    }

    public long CountSubjects(){
        return DatabaseUtils.queryNumEntries(db,MyConstants.THIRD_TABLE_NAME);
    }

    public boolean noteHaveSameTitle(String title){
        String selection = MyConstants.COLUMN_NAME_TITLE + " like ?";

        Cursor cursor = db.query(MyConstants.TABLE_NAME, null, selection, new String[]{"%" + title + "%"}, null, null, null);

        while (cursor.moveToNext()){
            @SuppressLint("Range") String curtitle = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_TITLE));
            if (curtitle.equals(title)) return false;
        }
        cursor.close();
        return true;
    }

    public boolean reminderHaveSameTitle(String title){
        String selection = MyConstants.COLUMN_NAME_TITLE + " like ?";

        Cursor cursor = db.query(MyConstants.SECOND_TABLE_NAME, null, selection, new String[]{"%" + title + "%"}, null, null, null);

        while (cursor.moveToNext()){
            @SuppressLint("Range") String curtitle = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_TITLE));
            if (curtitle.equals(title)) return false;
        }
        cursor.close();
        return true;
    }

    public boolean subjectHaveSameName(String name){
        String selection = MyConstants.COLUMN_NAME_SUBJECT_NAME + " like ?";

        Cursor cursor = db.query(MyConstants.THIRD_TABLE_NAME, null, selection, new String[]{"%" + name + "%"}, null, null, null);

        while (cursor.moveToNext()){
            @SuppressLint("Range") String curname = cursor.getString(cursor.getColumnIndex(MyConstants.COLUMN_NAME_SUBJECT_NAME));
            if (curname.equals(name)) return false;
        }
        cursor.close();
        return true;
    }

    public void CloseDb(){
        myDbHelper.close();
    }

    public void DeleteFromDb(Integer pos, String table_name){
        String selection = MyConstants._ID + "=" + pos;
        db.delete(table_name, selection, null);
    }

    public void DeleteFromDb(Integer id){
        String selection = MyConstants._ID + "=" + id;
        db.delete(MyConstants.THIRD_TABLE_NAME,selection,null);

        selection = MyConstants.COLUMN_NAME_SUBJECT + "=" +  id;
        db.delete(MyConstants.TABLE_NAME,selection,null);
    }



    public void Update(String title, String description, int subject, String uri, int id){
        String selection = MyConstants._ID + "=" + id;
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.COLUMN_NAME_TITLE,title);
        cv.put(MyConstants.COLUMN_NAME_DESCRIPTION,description);
        cv.put(MyConstants.COLUMN_NAME_SUBJECT, subject);
        cv.put(MyConstants.COLUMN_NAME_URI,uri);
        db.update(MyConstants.TABLE_NAME, cv, selection, null);
    }

    public void Update(String title, String description, String date, String time, String loc, String state, int id){
        String selection = MyConstants._ID + "=" + id;
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.COLUMN_NAME_TITLE,title);
        cv.put(MyConstants.COLUMN_NAME_DESCRIPTION,description);
        cv.put(MyConstants.COLUMN_NAME_DATE,date);
        cv.put(MyConstants.COLUMN_NAME_TIME,time);
        cv.put(MyConstants.COLUMN_NAME_LOCATION,loc);
        cv.put(MyConstants.COLUMN_NAME_STATE, state);
        db.update(MyConstants.SECOND_TABLE_NAME, cv, selection, null);
    }
}
