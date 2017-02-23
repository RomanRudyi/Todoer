package com.android.example.todoer.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.example.todoer.R;
import com.android.example.todoer.adapter.TaskListAdapter;
import com.android.example.todoer.model.TaskDummy;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectFragment extends Fragment {

    public static final String PROJECT_ID = "project_id";

    private RecyclerView recyclerView;
    private long id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        recyclerView = (RecyclerView)
                inflater.inflate(R.layout.fragment_task_list, container, false);

        id = getArguments().getLong(PROJECT_ID);

        Toast.makeText(getActivity(), "id = " + id, Toast.LENGTH_SHORT).show();

        // TODO: replace dummy data

        TaskListAdapter taskListAdapter = new TaskListAdapter(TaskDummy.tasks);
        recyclerView.setAdapter(taskListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        taskListAdapter.setListener(new TaskListAdapter.Listener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.EXTRA_TASK_NO, position);
                startActivity(intent);
            }
        });

        return recyclerView;
    }

}
