package com.android.example.todoer.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TaskRealm extends RealmObject {

    public static final String ID = "id";
    public static final String PROJECT_ID = "projectId";
    public static final String IS_ACTIVE = "isActive";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String PRIORITY = "priority";
    public static final String REPEATABLE = "repeatable";
    public static final String DATE= "date";

    public static final int PRIORITY_NONE = 0;
    public static final int PRIORITY_LOW = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_HIGH = 3;

    @PrimaryKey
    private long id;

    private int projectId;

    private boolean isActive;

    private String title;

    private String description;

    private int priority;

    private boolean repeatable;

    private long date;

    public TaskRealm() {
        setId(new Date().getTime());
    }

    /*public TaskRealm(int id, String title, long date, int priority) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.priority = priority;
        isActive = true;
    }*/

    // TODO: replace after testing
    /*public TaskRealm(int id, String title) {
        this.id = id;
        this.title = title;
        this.priority = PRIORITY_NONE;
        isActive = true;
    }*/

    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
