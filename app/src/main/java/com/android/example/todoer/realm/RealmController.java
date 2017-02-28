package com.android.example.todoer.realm;

import com.android.example.todoer.model.ProjectRealm;
import com.android.example.todoer.model.TaskRealm;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmController {

    /**
     * This helper method returns the new id for {@link TaskRealm} object
     * @param realm - Realm instance
     * @return nextTaskId
     */
    public static int getNextTaskId(Realm realm) {
        RealmResults<TaskRealm> tasks = realm.where(TaskRealm.class).findAllSorted(TaskRealm.ID);
        int nextTaskId;
        if (tasks.size() == 0) {
            nextTaskId = 0;
        } else {
            nextTaskId = tasks.get(tasks.size() - 1).getId() + 1;
        }
        return nextTaskId;
    }

    /**
     * This helper method returns the new id for {@link ProjectRealm} object
     * The id = 0 is required for {@link TaskRealm} objects, which isn't related to any project (inbox only)
     * @param realm - Realm instance
     * @return nextProjectId
     */
    public static int getNextProjectId(Realm realm) {
        RealmResults<ProjectRealm> projects = realm.where(ProjectRealm.class).findAllSorted(ProjectRealm.ID);
        int nextProjectId;
        if (projects.size() == 0) {
            nextProjectId = 1;
        } else {
            nextProjectId = projects.get(projects.size() - 1).getId() + 1;
        }
        return nextProjectId;
    }
}
