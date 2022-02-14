package com.example.note_application_neko_ru;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.note_application_neko_ru.adapter.MainAdapter;
import com.example.note_application_neko_ru.adapter.RcvItemList;
import com.example.note_application_neko_ru.adapter.RcvTasksItem;
import com.example.note_application_neko_ru.adapter.TasksAdapter;
import com.example.note_application_neko_ru.db.AppExecutor;
import com.example.note_application_neko_ru.db.MyDbManager;
import com.example.note_application_neko_ru.db.OnDataRecived;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class Reminder_list_fragment extends Fragment implements OnDataRecived{

    private MyDbManager mydbmanager;
    private TasksAdapter myAdapter;
    private RecyclerView tasksRcv;
    private FloatingActionButton add_task_button;
    private AlarmReceiver alarmReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reminder_list_fragment, container, false);
        setHasOptionsMenu(true);
        mydbmanager = new MyDbManager(getActivity());
        myAdapter = new TasksAdapter(getActivity());
        tasksRcv = v.findViewById(R.id.tasks_rcv);
        tasksRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        tasksRcv.setAdapter(myAdapter);
        getItemTouchHelper().attachToRecyclerView(tasksRcv);
        add_task_button = (FloatingActionButton) v.findViewById(R.id.task_add_button);
        add_task_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog();
//                Intent intent = new Intent(getActivity(), TaskEditActivity.class);
//                startActivity(intent);
            }
        });
        return v;
    }

    public void showDialog(){
        AlertDialog.Builder ChooseDialaog = new AlertDialog.Builder(getActivity());
        final AlertDialog chooseDialogVal;
        ChooseDialaog.setTitle("Выберете тип напомиания");
        ConstraintLayout cl = (ConstraintLayout) getLayoutInflater().inflate(R.layout.choose_dialog_layout,null);
        ChooseDialaog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        Button gpsChooseButton = (Button) cl.findViewById(R.id.gps_choose_button);
        Button dateChooseButton = (Button) cl.findViewById(R.id.date_choose_button);
        ChooseDialaog.setView(cl);
        chooseDialogVal = ChooseDialaog.show();
        gpsChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mydbmanager.CountGpsReminders()>5){
                    Toast.makeText(getActivity(), "Слишком много напомианий этого типа!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(getActivity(), GpsTaskEditActivity.class);
                    startActivity(intent);
                }
                chooseDialogVal.dismiss();
            }
        });
        dateChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mydbmanager.CountReminders()>4){
                    Toast.makeText(getActivity(), "Слишком много напоминаний этого типа!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(getActivity(), TaskEditActivity.class);
                    startActivity(intent);
                }
                chooseDialogVal.dismiss();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.note_menu,menu);
        MenuItem item = menu.findItem(R.id.Search_view);
        SearchView sv = (SearchView) item.getActionView();
        //  SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {

                ReadFromDB(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private ItemTouchHelper getItemTouchHelper(){
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                myAdapter.CancelAlarmWithDeleting(viewHolder.getAdapterPosition(), getActivity());
                myAdapter.DeleteItem(viewHolder.getAdapterPosition(),mydbmanager);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mydbmanager.OpenDb();
        ReadFromDB("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mydbmanager.CloseDb();
    }

    private void ReadFromDB(final String text){

        AppExecutor.getInstance().getSubFlow().execute(new Runnable() {
            @Override
            public void run() {
                mydbmanager.GetTasksFromDB(text, Reminder_list_fragment.this);

            }
        });
    }

    @Override
    public void OnTaskReceivedInterface(List<RcvTasksItem> list) {
        AppExecutor.getInstance().getMainFlow().execute(new Runnable() {
            @Override
            public void run() {
                myAdapter.updateTasksAdapter(list);
            }
        });
    }

    @Override
    public void OnReceivedInterface(List<RcvItemList> list) {

    }
}