package com.android.example.todoer.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.example.todoer.R;
import com.android.example.todoer.adapter.ProjectListAdapter;
import com.android.example.todoer.utility.OnRecyclerViewItemClickListener;
import com.android.example.todoer.model.ProjectRealm;

import io.realm.Realm;
import io.realm.RealmResults;


public class ProjectListFragment extends Fragment {

    private RealmResults<ProjectRealm> projects;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_project_list, container, false);

        RecyclerView projectsRecyclerView = (RecyclerView) layout.findViewById(R.id.projects_list_view);

        Realm realm = Realm.getDefaultInstance();
        projects = realm.where(ProjectRealm.class).findAllSorted(ProjectRealm.NAME);

        ProjectListAdapter projectListAdapter = new ProjectListAdapter(getActivity(), projects);
        projectsRecyclerView.setAdapter(projectListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        projectsRecyclerView.setLayoutManager(layoutManager);

        projectListAdapter.setListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(int position) {
                Fragment projectFragment = new ProjectFragment();
                /** Pass the project id to {@link ProjectFragment} */
                Bundle bundle = new Bundle();
                bundle.putLong(ProjectFragment.PROJECT_ID, projects.get(position).getId());
                projectFragment.setArguments(bundle);

                FragmentTransaction transaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame,
                        projectFragment, MainActivity.VISIBLE_FRAGMENT);
                transaction.addToBackStack(null);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
            }
        });

        return layout;
    }

}
