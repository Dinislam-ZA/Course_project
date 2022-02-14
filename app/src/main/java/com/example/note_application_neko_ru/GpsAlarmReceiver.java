package com.example.note_application_neko_ru;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.note_application_neko_ru.adapter.RcvTasksItem;
import com.example.note_application_neko_ru.db.MyConstants;
import com.example.note_application_neko_ru.db.MyDbManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

//Класс получения широковещательных сообщений и их обработки для напоминаний по времени
public class GpsAlarmReceiver extends BroadcastReceiver implements LocListenerInterface {
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Location location;
    Location tasklocation;
    private Context cont;
    double distance = 100.0d;
    private LocationManager locationManager;
    Myloclistener myloclistener;
    RcvTasksItem task;
    int ReceivedTask;
    double taskLatitude;
    double taskLongitude;

    @Override
    public void onReceive(Context context, Intent intent) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        myloclistener = new Myloclistener();
        myloclistener.setLocListenerInterface(this);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, myloclistener);
        }
        else Log.d("GPS state", "GPS is unable");

        this.cont = context;

        ReceivedTask = Integer.parseInt(intent.getStringExtra("task id"));
        Log.d("ID in receiver", Integer.toString(ReceivedTask));

        taskLatitude = intent.getDoubleExtra("Lat", 0);
        taskLongitude = intent.getDoubleExtra("Lng", 0);
        Log.d("Task latitude", Double.toString(taskLatitude));
        tasklocation = new Location(LocationManager.GPS_PROVIDER);
        tasklocation.setLatitude(taskLatitude);
        tasklocation.setLongitude(taskLongitude);


        MyDbManager myDbManager = new MyDbManager(context);
        myDbManager.OpenDb();
        task = myDbManager.GetTaskFromDB(ReceivedTask);
        myDbManager.CloseDb();



    }

    private boolean checkDistance(double a,double b, double c, double d, double R){



        return a * c * c + b * d * d <= R * R;
    }

    public void cancelAlarm(Context context, int ID) {
        //      locationManager.removeUpdates(myloclistener);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        pendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, GpsAlarmReceiver.class), 0);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void OnLocationChange(Location loc) {

        location = loc;


        Log.d("Loc in onLocationChange", loc.toString());
        Log.d("Location in onLocationChange", location.toString());
        double currentLatidtude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        Log.d("current lat", Double.toString(currentLatidtude));
        Log.d("current lng",Double.toString(currentLongitude));
        Log.d("Distance",Double.toString(location.distanceTo(tasklocation)));
        if(location.distanceTo(tasklocation)<=distance){
            locationManager.removeUpdates(myloclistener);

            Intent TaskEditIntent = new Intent(cont, GpsTaskEditActivity.class);
            TaskEditIntent.putExtra(MyConstants.LIST_ITEM_INTENT, task);
            TaskEditIntent.putExtra(MyConstants.EDIT_STATE, false);
            PendingIntent mClick = PendingIntent.getActivity(cont,ReceivedTask,TaskEditIntent,0);

            MyDbManager myDbManager = new MyDbManager(cont);
            myDbManager.OpenDb();
            myDbManager.Update(task.getTitle(),
                    task.getDescription(),
                    task.getDate(), task.getTime(),
                    task.getLocation(),
                    "Сработано",
                    task.getId());
            myDbManager.CloseDb();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(cont,"TaskChannel")
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(task.getTitle())
                    .setContentText(task.getDescription())
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(mClick);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(cont);
            notificationManagerCompat.notify(123, builder.build());
        }
    }
}
