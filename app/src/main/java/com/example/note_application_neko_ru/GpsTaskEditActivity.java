package com.example.note_application_neko_ru;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note_application_neko_ru.adapter.RcvTasksItem;
import com.example.note_application_neko_ru.db.MyConstants;
import com.example.note_application_neko_ru.db.MyDbManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

public class GpsTaskEditActivity extends AppCompatActivity {

    private Button chooseLocationButton, saveButton;
    private TextView locationView;
    private double lat = 0;
    private RcvTasksItem item;
    private double lng = 0;
    private GpsAlarmReceiver mAlarmReceiver;
    private EditText task_title, task_desctiption;
    private MyDbManager myDbManager;
    private boolean IsEditState = true;
    private int task_id;
    PendingIntent pendingIntent;

    ActivityResultLauncher<Intent> GetLocationActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent intent = result.getData();
                lat = intent.getDoubleExtra("Lat", 0);
                lng = intent.getDoubleExtra("Lng", 0);
                locationView.setText("Локация была установлена");
            }
        }
    });

    private void init(){
        myDbManager = new MyDbManager(this);
        chooseLocationButton = findViewById(R.id.gps_choose_button);
        locationView = findViewById(R.id.locationView);
        task_title = findViewById(R.id.gps_task_title);
        task_desctiption = findViewById(R.id.gps_task_description);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_task_edit);
        init();
        setTitle("Добавление напоминания");
        GetMyIntents();
        CreateNotificationChannel();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},44);
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

    public void OnChooseButtonClick(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        GetLocationActivity.launch(intent);
    }



    public void OnSaveButtonClick(View view){

        if(InsertValidation()) {
            if(IsEditState){
                if(myDbManager.reminderHaveSameTitle(task_title.getText().toString())){
                    if (!CheckPermissions()) {
                        //            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                    } else {

                        if(IsEditState){
                            myDbManager.InsertToDB(task_title.getText().toString(),
                                    task_desctiption.getText().toString(),
                                    "empty",
                                    "empty", Double.toString(lng)+" "+Double.toString(lat), "Ожидание срабатывания");
                            task_id = (int) myDbManager.reminder_current_id;
                            setAlarm(this);
                        }
                        else{
                            myDbManager.Update(task_title.getText().toString(),
                                    task_desctiption.getText().toString(),
                                    "empty",
                                    "empty", Double.toString(lng)+" "+Double.toString(lat), "Ожидание срабатывания",
                                    item.getId());
                            mAlarmReceiver = new GpsAlarmReceiver();
                            mAlarmReceiver.cancelAlarm(this,task_id);
                            setAlarm(this);
                        }

                        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(this, "Напоминание с таким заголовком уже существует!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                if (!CheckPermissions()) {
                    //            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                } else {

                    if(IsEditState){
                        myDbManager.InsertToDB(task_title.getText().toString(),
                                task_desctiption.getText().toString(),
                                "empty",
                                "empty", Double.toString(lng)+" "+Double.toString(lat), "Ожидание срабатывания");
                        task_id = (int) myDbManager.reminder_current_id;
                        setAlarm(this);
                    }
                    else{
                        myDbManager.Update(task_title.getText().toString(),
                                task_desctiption.getText().toString(),
                                "empty",
                                "empty", Double.toString(lng)+" "+Double.toString(lat), "Ожидание срабатывания",
                                item.getId());
                        mAlarmReceiver = new GpsAlarmReceiver();
                        mAlarmReceiver.cancelAlarm(this,task_id);
                        setAlarm(this);
                    }

                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
        else{
            Toast.makeText(this, R.string.TaskInsertValidation, Toast.LENGTH_SHORT).show();
        }
    }

    public void setAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        Log.d("Current time",calendar.toString());
        calendar.add(Calendar.SECOND,10);
        Log.d("Setted time",calendar.toString());

        Log.d("id", Integer.toString(task_id));
        Intent intent = new Intent(context, GpsAlarmReceiver.class);
        intent.putExtra("task id", Integer.toString(task_id));
        intent.putExtra("Lat", lat);
        intent.putExtra("Lng", lng);
        pendingIntent = PendingIntent.getBroadcast(context, task_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10000, pendingIntent);

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

    public boolean InsertValidation(){
        if(task_title.getText().toString().trim().equals("")
                ||task_desctiption.getText().toString().trim().equals("")
                ||lat == 0
                ||lng == 0
        ){
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean CheckPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private void GetMyIntents(){
        Intent i = getIntent();
        if(i != null){
            item = (RcvTasksItem) i.getSerializableExtra(MyConstants.LIST_ITEM_INTENT);
            IsEditState = i.getBooleanExtra(MyConstants.EDIT_STATE, true);

            if(!IsEditState){
                setTitle("Редактирование напоминания");
                task_id = item.getId();
                task_title.setText(item.getTitle());
                task_desctiption.setText(item.getDescription());
                locationView.setText("Местположение установлено");

            };
        }
    }

}