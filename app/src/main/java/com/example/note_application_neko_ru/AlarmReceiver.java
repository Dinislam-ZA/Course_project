package com.example.note_application_neko_ru;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.renderscript.RenderScript;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.note_application_neko_ru.adapter.RcvTasksItem;
import com.example.note_application_neko_ru.db.MyConstants;
import com.example.note_application_neko_ru.db.MyDbManager;

import java.util.Calendar;

//Класс получения широковещательных сообщений и их обработки для напоминаний по времени
public class AlarmReceiver extends BroadcastReceiver {
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        int ReceivedTask = Integer.parseInt(intent.getStringExtra(TaskEditActivity.EXTRA_TASK_ID));
        Log.d("ID in receiver", Integer.toString(ReceivedTask));

        MyDbManager myDbManager = new MyDbManager(context);
        myDbManager.OpenDb();
        RcvTasksItem task = myDbManager.GetTaskFromDB(ReceivedTask);
        String mTitle = task.getTitle();
        myDbManager.Update(task.getTitle(),
                task.getDescription(),
                task.getDate(), task.getTime(),
                task.getLocation(),
                "Сработано",
                task.getId());
        myDbManager.CloseDb();

        Intent TaskEditIntent = new Intent(context, TaskEditActivity.class);
        TaskEditIntent.putExtra(MyConstants.LIST_ITEM_INTENT, task);
        TaskEditIntent.putExtra(MyConstants.EDIT_STATE, false);
        PendingIntent mClick = PendingIntent.getActivity(context,ReceivedTask,TaskEditIntent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"TaskChannel")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(task.getTitle())
                .setContentText(task.getDescription())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(mClick);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());
    }


    public void cancelAlarm(Context context, int ID) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Отмена ожидающего намерения с помощью ID заметки
        pendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, AlarmReceiver.class), 0);
        alarmManager.cancel(pendingIntent);


    }
}
