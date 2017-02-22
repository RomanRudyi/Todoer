package com.android.example.todoer.app;

import android.app.Application;

import io.realm.Realm;

public class Todoer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
