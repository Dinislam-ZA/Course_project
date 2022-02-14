package com.example.note_application_neko_ru.db;

import com.example.note_application_neko_ru.adapter.RcvItemList;
import com.example.note_application_neko_ru.adapter.RcvTasksItem;

import java.util.List;

public interface OnDataRecived {
    void OnReceivedInterface(List<RcvItemList> list);
    void OnTaskReceivedInterface(List<RcvTasksItem> list);
}
