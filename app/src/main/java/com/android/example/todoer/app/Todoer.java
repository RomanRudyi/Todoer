package com.android.example.todoer.app;

import android.app.Application;

import com.android.example.todoer.R;
import com.android.example.todoer.model.ProjectRealm;

import io.realm.Realm;

public class Todoer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        Realm realm = Realm.getDefaultInstance();
        ProjectRealm inbox = realm.where(ProjectRealm.class).equalTo(ProjectRealm.ID, ProjectRealm.INBOX_ID).findFirst();
        if (inbox == null) {
            realm.beginTransaction();
            ProjectRealm inboxProject = new ProjectRealm();
            inboxProject.setId(ProjectRealm.INBOX_ID);
            inboxProject.setName(getString(R.string.drawer_inbox));
            realm.copyToRealm(inboxProject);
            realm.commitTransaction();
        }
    }
}
