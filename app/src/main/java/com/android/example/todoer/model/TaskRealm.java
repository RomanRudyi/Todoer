package com.android.example.todoer.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TaskRealm extends RealmObject {

    public static final String ID = "id";
    public static final String PROJECT_ID = "projectId";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "description";
    public static final String PRIORITY = "priority";
    public static final String REPEATABLE = "repeatable";
    public static final String DATE= "date";

    @PrimaryKey
    private long id;

    private long projectId;

    private String title;

    private String description;

    private int priority;

    private boolean repeatable;

    private Date date;

    public TaskRealm() {}

    public TaskRealm(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
