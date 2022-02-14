package com.example.note_application_neko_ru.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note_application_neko_ru.AlarmReceiver;
import com.example.note_application_neko_ru.EditActivity;
import com.example.note_application_neko_ru.GpsAlarmReceiver;
import com.example.note_application_neko_ru.GpsTaskEditActivity;
import com.example.note_application_neko_ru.R;
import com.example.note_application_neko_ru.TaskEditActivity;
import com.example.note_application_neko_ru.db.MyConstants;
import com.example.note_application_neko_ru.db.MyDbManager;

import java.util.ArrayList;
import java.util.List;

//Класс для заполнения и работы с элементами RecycleView списка Заметок
public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder>{

    private Context context;
    private List<RcvTasksItem> tasksArray;

    public TasksAdapter(Context context){

        this.context = context;
        tasksArray = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.tasks_list_item_layout, parent, false);
        return new MyViewHolder(view, context, tasksArray);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        MyDbManager myDbManager = new MyDbManager(context);
        myDbManager.OpenDb();
        if(tasksArray.get(position).getLocation().equals("empty")){
            holder.SetData(tasksArray.get(position).getTitle(),
                    tasksArray.get(position).getDate() + "    " + tasksArray.get(position).getTime(),
                    tasksArray.get(position).getState()
                    );
        }
        else{
            holder.SetData(tasksArray.get(position).getTitle(),
                    "Местоположение",
                    tasksArray.get(position).getState()
            );
        }

        myDbManager.CloseDb();
    }

    @Override
    public int getItemCount() {
        return tasksArray.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView RVitem,datelocview,stateview;
        private Context mvhcontext;
        private List<RcvTasksItem> MainList;

        public MyViewHolder(@NonNull View itemView,Context context,List<RcvTasksItem> MList) {
            super(itemView);
            this.MainList = MList;
            this.mvhcontext = context;
            RVitem = itemView.findViewById(R.id.task_item);
            datelocview = itemView.findViewById(R.id.taskinf);
            stateview = itemView.findViewById(R.id.StateView);
            RVitem.setOnClickListener(this);
        }

        public void SetData(String data, String taskinfo, String state){
            RVitem.setText(data);
            datelocview.setText(taskinfo);
            stateview.setText(state);
        }

        @Override
        public void onClick(View view) {

            if(MainList.get(getAdapterPosition()).getLocation().equals("empty")){
                Intent i = new Intent(mvhcontext, TaskEditActivity.class);
                i.putExtra(MyConstants.LIST_ITEM_INTENT, MainList.get(getAdapterPosition()));
                i.putExtra(MyConstants.EDIT_STATE, false);
                mvhcontext.startActivity(i);
            }
            else
            {
                Intent i = new Intent(mvhcontext, GpsTaskEditActivity.class);
                i.putExtra(MyConstants.LIST_ITEM_INTENT, MainList.get(getAdapterPosition()));
                i.putExtra(MyConstants.EDIT_STATE, false);
                mvhcontext.startActivity(i);
            }
        }
    }

    public void updateTasksAdapter(List<RcvTasksItem> List){

        tasksArray.clear();
        tasksArray.addAll(List);
        notifyDataSetChanged();
    }

    public void DeleteItem(int pos, MyDbManager dbManager){
        dbManager.DeleteFromDb(tasksArray.get(pos).getId(), MyConstants.SECOND_TABLE_NAME);
        tasksArray.remove(pos);
        notifyItemChanged(0,tasksArray.size());
        notifyItemRemoved(pos);
    }

    public void CancelAlarmWithDeleting(int pos, Context context){

        int _id = tasksArray.get(pos).getId();
        String location = tasksArray.get(pos).getLocation();
        Log.d("DeletingItemId", Integer.toString(_id));
        if (location.equals("Empty")){
            AlarmReceiver alarmReceiver = new AlarmReceiver();
            alarmReceiver.cancelAlarm(context,_id);
        }
        else{
            GpsAlarmReceiver alarmReceiver = new GpsAlarmReceiver();
            alarmReceiver.cancelAlarm(context,_id);
        }
    }

}
