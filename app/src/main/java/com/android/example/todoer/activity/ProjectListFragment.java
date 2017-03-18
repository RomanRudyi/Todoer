package com.android.example.todoer.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.example.todoer.R;
import com.android.example.todoer.adapter.ProjectListAdapter;
import com.android.example.todoer.model.ProjectRealm;

import io.realm.Realm;
import io.realm.RealmResults;


public class ProjectListFragment extends Fragment {

    private RecyclerView projectsListView;
    private RealmResults<ProjectRealm> projects;
    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_project_list, container, false);

        projectsListView = (RecyclerView) layout.findViewById(R.id.projects_list_view);

        realm = Realm.getDefaultInstance();
        projects = realm.where(ProjectRealm.class).findAllSorted(ProjectRealm.NAME);

        ProjectListAdapter adapter = new ProjectListAdapter(getActivity(), projects);
        projectsListView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        projectsListView.setLayoutManager(layoutManager);

        return layout;
    }

}
