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

import com.example.note_application_neko_ru.EditActivity;
import com.example.note_application_neko_ru.R;
import com.example.note_application_neko_ru.db.MyConstants;
import com.example.note_application_neko_ru.db.MyDbManager;

import java.util.ArrayList;
import java.util.List;

//Класс для заполнения и работы с элементами RecycleView списка Заметок
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    private Context context;
    private List<RcvItemList> notesArray;


    public MainAdapter(Context context){

        this.context = context;
        notesArray = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_list_layout, parent, false);
        return new MyViewHolder(view, context, notesArray);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(notesArray.get(position).getSubject()==0){
            holder.SetData(notesArray.get(position).getTitle(),"Без темы");

        }
        else
        {
            MyDbManager myDbManager = new MyDbManager(context);
            myDbManager.OpenDb();
            holder.SetData(notesArray.get(position).getTitle(),myDbManager.GetSubjectFromDB(notesArray.get(position).getSubject()).getName());
            myDbManager.CloseDb();
        }
    }

    @Override
    public int getItemCount() {
        return notesArray.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView RVitem,subjectTextView;
        private Context mvhcontext;
        private List<RcvItemList> MainList;

        public MyViewHolder(@NonNull View itemView,Context context,List<RcvItemList> MList) {

            super(itemView);
            this.MainList = MList;
            RVitem = itemView.findViewById(R.id.RVitem);
            subjectTextView = itemView.findViewById(R.id.Subject);
            itemView.setOnClickListener(this);
            this.mvhcontext = context;

        }

        public void SetData(String data, String subject){

            RVitem.setText(data);
            subjectTextView.setText(subject);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(mvhcontext, EditActivity.class);
            i.putExtra(MyConstants.LIST_ITEM_INTENT, MainList.get(getAdapterPosition()));
            i.putExtra(MyConstants.EDIT_STATE, false);
            mvhcontext.startActivity(i);

        }
    }

    public void updateNotesAdapter(List<RcvItemList> newList){

        notesArray.clear();
        notesArray.addAll(newList);
        notifyDataSetChanged();
    }


    public void DeleteItem(int pos, MyDbManager dbManager){
        dbManager.DeleteFromDb(notesArray.get(pos).getId(), MyConstants.TABLE_NAME);
        notesArray.remove(pos);
        notifyItemChanged(0,notesArray.size());
        notifyItemRemoved(pos);
    }

    public void DeleteItems(SubjectItem subject, MyDbManager dbManager){
        dbManager.DeleteFromDb(subject.getId());
        int pos = 0;
        notesArray.clear();
       // notifyDataSetChanged();
    }


}
