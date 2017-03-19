package com.android.example.todoer.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ProjectRealm extends RealmObject {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String COLOR = "color";

    public static final long INBOX_ID = 0;

    public static final int COLOR_RED = 0;
    public static final int COLOR_GREEN = 1;
    public static final int COLOR_BLUE = 2;
    public static final int COLOR_PURPLE = 3;
    public static final int COLOR_ORANGE = 4;

    @PrimaryKey
    private long id;

    private String name;

    private int color;

    public ProjectRealm() {
        setId(new Date().getTime());
    }

    public long getId() {
        return id;
    }

    private void setId(long id) {
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
