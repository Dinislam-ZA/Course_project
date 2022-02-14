package com.example.note_application_neko_ru;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Switch;

import com.example.note_application_neko_ru.adapter.MainAdapter;
import com.example.note_application_neko_ru.adapter.RcvItemList;
import com.example.note_application_neko_ru.db.AppExecutor;
import com.example.note_application_neko_ru.db.MyDbManager;
import com.example.note_application_neko_ru.db.OnDataRecived;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    private MyDbManager mydbmanager;
    private EditText edTitle,edDisc;
    public RecyclerView RCview;
    private MainAdapter myadapter;
    public FloatingActionButton addButton;
    private BottomNavigationView bottomnav;
    private NavController navcontroller;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomnav = findViewById(R.id.bottomNavigationView2);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView5,new Notes_list_fragment()).commit();
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()){
                    case R.id.notes_list_fragment:
                        fragment = new Notes_list_fragment();
                        break;
                    case R.id.reminder_list_fragment:
                        fragment = new Reminder_list_fragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView5,fragment).commit();
                return true;
            }
        });



    }
}