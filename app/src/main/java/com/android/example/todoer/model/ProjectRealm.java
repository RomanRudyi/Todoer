package com.android.example.todoer.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ProjectRealm extends RealmObject {

    @PrimaryKey
    private long id;

    private String name;

    private int color;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
