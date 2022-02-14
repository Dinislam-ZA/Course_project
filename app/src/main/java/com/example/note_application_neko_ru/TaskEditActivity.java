package com.example.note_application_neko_ru;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.note_application_neko_ru.adapter.RcvTasksItem;
import com.example.note_application_neko_ru.db.AppExecutor;
import com.example.note_application_neko_ru.db.MyConstants;
import com.example.note_application_neko_ru.db.MyDbManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskEditActivity extends AppCompatActivity {


    private EditText task_title, task_desctiption;
    private EditText task_date;
    private TextView task_time;
    int hour,minute;
    DatePickerDialog.OnDateSetListener setListener;
    private FloatingActionButton add_task_button;
    private MyDbManager myDbManager;
    private RcvTasksItem item;
    public AlarmReceiver mAlarmReceiver;
    public AlarmManager alarmManager;
    public PendingIntent pendingIntent;
    private boolean IsEditState = true;
    private Calendar mCalendar = Calendar.getInstance();
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int task_id = -1;

    public static final String EXTRA_TASK_ID = "Task_id";

    private void init(){
        myDbManager = new MyDbManager(this);
        task_title = findViewById(R.id.task_title);
        task_desctiption = findViewById(R.id.task_description);
        task_date = findViewById(R.id.task_date);
        task_time = findViewById(R.id.task_timepicker);
        add_task_button = findViewById(R.id.add_task_button);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        init();
        setTitle("Добавление напоминания");
        GetMyIntents();
        CreateNotificationChannel();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        task_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TaskEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1+1;
                        String date = i2 + "/" + i1 + "/" + i;
                        task_date.setText(date);
                        mYear = i;
                        mMonth = i1 - 1;
                        mDay = i2;
                        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
                        mCalendar.set(Calendar.MONTH, mMonth);
                        mCalendar.set(Calendar.YEAR,mYear);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        task_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        TaskEditActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                hour = i;
                                minute = i1;
                                mHour = i;
                                mMinute = i1;
                                mCalendar.set(Calendar.MINUTE,mMinute);
                                mCalendar.set(Calendar.HOUR, mHour);
                                if(i > 12){
                                    mCalendar.set(Calendar.AM_PM, Calendar.PM);
                                }
                                else
                                {
                                    mCalendar.set(Calendar.AM_PM, Calendar.AM);
                                }
                                String time = hour + ":" + minute;
                                SimpleDateFormat f24Hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24Hours.parse(time);
                                    SimpleDateFormat f12format = new SimpleDateFormat(
                                            "HH:mm aa"
                                    );
                                    task_time.setText(f12format.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(hour,minute);
                timePickerDialog.show();
            }
        });

    }

    private void CreateNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            CharSequence name = "ReminderNotificationChannel";
            String desc = "Channel for Alarm manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("TaskChannel",name,importance);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void OnClickAdd(View view){
        if(InsertValidation()){
            if(IsEditState){
                if(myDbManager.reminderHaveSameTitle(task_title.getText().toString())){
                    if(IsEditState){
                        myDbManager.InsertToDB(task_title.getText().toString(),
                                task_desctiption.getText().toString(),
                                task_date.getText().toString(),
                                task_time.getText().toString(), "empty", "Ожидание срабатывания");
                        task_id = (int) myDbManager.reminder_current_id;
                        setAlarm(this,mCalendar);
                        Log.d("Setted_time",mCalendar.toString());
                    }
                    else
                    {
                        myDbManager.Update(task_title.getText().toString(),
                                task_desctiption.getText().toString(),
                                task_date.getText().toString(),
                                task_time.getText().toString(), "empty", "Ожидание срабатывания",
                                item.getId());
                        mAlarmReceiver = new AlarmReceiver();
                        mAlarmReceiver.cancelAlarm(this,task_id);
                        setAlarm(this,mCalendar);
                    }
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(this,"Напоминание с таким заголовком уже существует!" , Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                if(IsEditState){
                    myDbManager.InsertToDB(task_title.getText().toString(),
                            task_desctiption.getText().toString(),
                            task_date.getText().toString(),
                            task_time.getText().toString(), "empty", "Ожидание срабатывания");
                    task_id = (int) myDbManager.reminder_current_id;
                    setAlarm(this,mCalendar);
                    Log.d("Setted_time",mCalendar.toString());
                }
                else
                {
                    myDbManager.Update(task_title.getText().toString(),
                            task_desctiption.getText().toString(),
                            task_date.getText().toString(),
                            task_time.getText().toString(), "empty", "Ожидание срабатывания",
                            item.getId());
                    mAlarmReceiver = new AlarmReceiver();
                    mAlarmReceiver.cancelAlarm(this,task_id);
                    setAlarm(this,mCalendar);
                }
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish();
            }

        }
        else
        {
            Toast.makeText(this, R.string.TaskInsertValidation, Toast.LENGTH_SHORT).show();
        }
    }


        public void setAlarm(Context context, Calendar calendar){
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Log.d("id", Integer.toString(task_id));
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(TaskEditActivity.EXTRA_TASK_ID, Integer.toString(task_id));
            pendingIntent = PendingIntent.getBroadcast(context, task_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        }


    public void cancelAlarm(){

    }

    public boolean InsertValidation(){
        if(task_title.getText().toString().trim().equals("")
                ||task_desctiption.getText().toString().trim().equals("")
                ||task_date.getText().toString().trim().equals("")
                ||task_time.getText().toString().trim().equals("")){
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myDbManager.OpenDb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDbManager.CloseDb();
    }

    private void GetMyIntents(){
        Intent i = getIntent();
        if(i != null){
            item = (RcvTasksItem) i.getSerializableExtra(MyConstants.LIST_ITEM_INTENT);
            IsEditState = i.getBooleanExtra(MyConstants.EDIT_STATE, true);

            if(!IsEditState){
                setTitle("Редактирование напомиания");
                task_id = item.getId();
                task_title.setText(item.getTitle());
                task_desctiption.setText(item.getDescription());
                task_date.setText(item.getDate());
                task_time.setText(item.getTime());
            };
        }
    }
}