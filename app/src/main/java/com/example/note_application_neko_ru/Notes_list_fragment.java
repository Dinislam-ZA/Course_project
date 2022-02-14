package com.example.note_application_neko_ru;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.note_application_neko_ru.adapter.MainAdapter;
import com.example.note_application_neko_ru.adapter.RcvItemList;
import com.example.note_application_neko_ru.adapter.RcvTasksItem;
import com.example.note_application_neko_ru.adapter.SubjectItem;
import com.example.note_application_neko_ru.db.AppExecutor;
import com.example.note_application_neko_ru.db.MyDbManager;
import com.example.note_application_neko_ru.db.OnDataRecived;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class Notes_list_fragment extends Fragment implements OnDataRecived{


    private MyDbManager mydbmanager;
    public RecyclerView RCview;
    private MainAdapter myadapter;
    private Spinner subjectSpinner;
    public FloatingActionButton addButton;
    private Button deleteSubjectButton;
    private String sub = "No subject";
    private String searchtxt = "";
    private int subjectId;
    private SubjectItem subject;

    public void init(){
        mydbmanager = new MyDbManager(getActivity());
        myadapter = new MainAdapter(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.note_menu,menu);
        MenuItem item = menu.findItem(R.id.Search_view);
        SearchView sv = (SearchView) item.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {

                searchtxt = s;
                ReadFromDB(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        init();
        View v = inflater.inflate(R.layout.fragment_notes_list_fragment, container, false);
        addButton = v.findViewById(R.id.add_note_button);
        subjectSpinner = v.findViewById(R.id.spinner_subject_list);
        deleteSubjectButton = v.findViewById(R.id.delete_subject_button);
        setHasOptionsMenu(true);
        RCview = v.findViewById(R.id.rcView);

        RCview.setLayoutManager(new LinearLayoutManager(getActivity()));
        RCview.setAdapter(myadapter);
        getItemTouchHelper().attachToRecyclerView(RCview);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mydbmanager.CountNotes()>3){
                    Toast.makeText(getActivity(), "Слишком много заметок!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(getActivity(), EditActivity.class);
                    startActivity(intent);
                }
            }
        });
        deleteSubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subject != null){
                    if (subject.getId() != 0) {

                        showDeleteDialog();
                    }
                    else {
                        Toast.makeText(getActivity(), "Нельзя удалить эту тему", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "Нельзя удалить эту тему", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return v;
    }

    public void showDeleteDialog(){
        AlertDialog.Builder DeleteSubjectDialog = new AlertDialog.Builder(getActivity());

        DeleteSubjectDialog.setTitle("Соотвествующие заметки также удалятся.");
        DeleteSubjectDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        DeleteSubjectDialog.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                myadapter.DeleteItems(subject, mydbmanager);
                setSpinner();
            }
        });
        DeleteSubjectDialog.show();
    }


    private void setSpinner(){
        List<SubjectItem> subs = mydbmanager.GetAllSubjects();
        subject = subs.get(0);
        ArrayList<String> str = new ArrayList<String>();
        subs.forEach(n -> str.add(n.getName()));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, str);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(arrayAdapter);
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sub = adapterView.getSelectedItem().toString();
                subjectId = i;
                subject = subs.get(i);
                if(sub.equals("Без темы")){
                    subject = null;
                }
                ReadFromDB(searchtxt);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        mydbmanager.OpenDb();
        ReadFromDB("");
        setSpinner();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mydbmanager.CloseDb();
    }

    private void ReadFromDB(final String text){

        if (subject != null){
            Log.d("sub",subject.getName());
            Log.d("subid", Integer.toString(subject.getId()));
        }
        AppExecutor.getInstance().getSubFlow().execute(new Runnable() {
            @Override
            public void run() {
                mydbmanager.GetNoteFromDB(text, subject, Notes_list_fragment.this);

            }
        });
    }



    private ItemTouchHelper getItemTouchHelper(){
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                myadapter.DeleteItem(viewHolder.getAdapterPosition(),mydbmanager);
            }
        });
    }

    @Override
    public void OnReceivedInterface(List<RcvItemList> list) {

        AppExecutor.getInstance().getMainFlow().execute(new Runnable() {
            @Override
            public void run() {
                myadapter.updateNotesAdapter(list);
            }
        });
    }

    @Override
    public void OnTaskReceivedInterface(List<RcvTasksItem> list) {

    }
}