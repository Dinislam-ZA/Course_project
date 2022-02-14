package com.example.note_application_neko_ru;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.renderscript.Float3;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.note_application_neko_ru.adapter.RcvItemList;
import com.example.note_application_neko_ru.adapter.SubjectItem;
import com.example.note_application_neko_ru.db.MyConstants;
import com.example.note_application_neko_ru.db.MyDbManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private final int PICK_IMAGE_CODE = 123;
    private ConstraintLayout ImageContainer;
    private EditText editTitle;
    private EditText editdescription;
    private Spinner subjectSpinner;
    private FloatingActionButton savebutton, addimagebutton;
    private MyDbManager mydbmanager;
    private ImageButton EditImage, DeleteImage;
    private ImageView ImageNote;
    private String URI = "empty";
    private String subject = "No subject";
    private String deletingSubject;
    private int subjectId = 0;
    private RcvItemList item;
    private boolean IsEditState = true;

    public void init(){
        mydbmanager = new MyDbManager(this);
        ImageContainer = findViewById(R.id.ImageContainer);
        editTitle = findViewById(R.id.edit_title);
        editdescription = findViewById(R.id.ed_description);
        savebutton = findViewById(R.id.add_button);
        addimagebutton = findViewById(R.id.fbAddImage);
        EditImage = findViewById(R.id.EditImageButton);
        DeleteImage = findViewById(R.id.DeleteImageButton);
        ImageNote = findViewById(R.id.imageNote);
        subjectSpinner = findViewById(R.id.subject_spinner);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE_CODE && data != null){

            getContentResolver().takePersistableUriPermission(data.getData(),Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ImageNote.setImageURI(data.getData());
            URI = data.getData().toString();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setTitle("Добавление заметки");
        init();
        GetMyIntents();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mydbmanager.OpenDb();
        setSpinner();
    }

    private void setSpinner(){
        List<SubjectItem> sub = mydbmanager.GetAllSubjects();
        ArrayList<String> str = new ArrayList<String>();
        sub.forEach((n) -> str.add(n.getName()));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, str);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(arrayAdapter);
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subject = adapterView.getSelectedItem().toString();
                subjectId = i;
                deletingSubject = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void onClickAddSubject(View view){
        showCreateSubjectDialog();
    }

    public void onClickSave(View view){
        if(InsertValidation()){
            if(IsEditState){
                if(!mydbmanager.noteHaveSameTitle(editTitle.getText().toString())){
                    Toast.makeText(this, "Заметка с таким заголовком уже существует", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!IsEditState == false){
                        mydbmanager.InsertToDB(editTitle.getText().toString(),editdescription.getText().toString(), subjectId,URI);
                    }
                    else {
                        mydbmanager.Update(editTitle.getText().toString(),editdescription.getText().toString(), subjectId,URI,item.getId());
                    }
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else{
                if(!IsEditState == false){
                    mydbmanager.InsertToDB(editTitle.getText().toString(),editdescription.getText().toString(), subjectId,URI);
                }
                else {
                    mydbmanager.Update(editTitle.getText().toString(),editdescription.getText().toString(), subjectId,URI,item.getId());
                }
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else{
            Toast.makeText(this, R.string.IOTitleAndDescValid, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean InsertValidation(){
        if(editTitle.getText().toString().trim().equals("")
                || editdescription.getText().toString().trim().equals("")){
            return false;
        }
        return true;

    }



    public void showCreateSubjectDialog(){
        AlertDialog.Builder createSubjectDialaog = new AlertDialog.Builder(this);
        final AlertDialog createSubDialogVal;
        createSubjectDialaog.setTitle("Создание новой темы для заметки");
        ConstraintLayout cl = (ConstraintLayout) getLayoutInflater().inflate(R.layout.create_subject_dialog_layout,null);
        EditText subjectName = cl.findViewById(R.id.subject_name);
        createSubjectDialaog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        createSubjectDialaog.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean validsub = true;
                boolean emptvalidsub = true;
                if (!subjectName.getText().toString().equals("")){
                    if (mydbmanager.subjectHaveSameName(subjectName.getText().toString())){
                        mydbmanager.InsertToDB(subjectName.getText().toString());
                        setSpinner();
                    }
                    else{
                        validsub = false;
                    }
                }
                else
                {
                    emptvalidsub = false;
                }
                CheckSameValidSub(validsub);
                CheckEmptyValidSub(emptvalidsub);
            }
        });
        createSubjectDialaog.setView(cl);
        createSubDialogVal = createSubjectDialaog.show();
    }

    private void CheckSameValidSub(boolean valid){
        if (!valid){
            Toast.makeText(this, "Тема с таким названием уже существует!", Toast.LENGTH_SHORT).show();
        }
    }

    private void CheckEmptyValidSub(boolean valid){
        if (!valid){
            Toast.makeText(this, "Поле названия темы не должно быть пустым!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mydbmanager.CloseDb();
    }

    public void OnClickAddImage(View view){

        ImageContainer.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
    }

    public void OnClickDeleteImage(View view){
        ImageNote.setImageResource(R.drawable.image_icon);
        URI = "empty";
        ImageContainer.setVisibility(View.GONE);
        addimagebutton.setVisibility(View.VISIBLE);
    }

    public void OnClickEditImage(View view){
        Intent chooser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooser.setType("image/*");
        startActivityForResult(chooser, PICK_IMAGE_CODE);
    }

    private void GetMyIntents(){
        Intent i = getIntent();
        if (i != null){


            item = (RcvItemList) i.getSerializableExtra(MyConstants.LIST_ITEM_INTENT);
            IsEditState = i.getBooleanExtra(MyConstants.EDIT_STATE, true);

            if (IsEditState == false){

                setTitle("Редактирование заметки");
                editTitle.setText(item.getTitle());
                editdescription.setText(item.getDescription());
                if (!item.getUri().equals("empty")){
                    URI = item.getUri();
                    ImageContainer.setVisibility(View.VISIBLE);
                    addimagebutton.setVisibility(View.GONE);
                    //EditImage.setVisibility(View.GONE);
                    //DeleteImage.setVisibility(View.GONE);
                    ImageNote.setImageURI(Uri.parse(item.getUri()));
                }
            }
        }

    }
}