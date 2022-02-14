package com.example.note_application_neko_ru.adapter;

import java.io.Serializable;

//Класс заметки
public class RcvItemList implements Serializable {

    private int Id = 0;
    private String title;
    private String description;
    private int subject;
    private String uri = "empty";

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
