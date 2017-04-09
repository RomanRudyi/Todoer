package com.android.example.todoer.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.example.todoer.R;
import com.android.example.todoer.adapter.TaskListAdapter;
import com.android.example.todoer.utility.OnRecyclerViewItemClickListener;
import com.android.example.todoer.model.TaskRealm;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectFragment extends Fragment {

    public static final String PROJECT_ID = "project_id";

    private RelativeLayout taskListLayout;
    private RecyclerView taskRecyclerView;
    private RelativeLayout emptyView;

    private RealmResults<TaskRealm> tasks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        taskListLayout = (RelativeLayout)
                inflater.inflate(R.layout.fragment_task_list, container, false);
        taskRecyclerView = (RecyclerView) taskListLayout.findViewById(R.id.task_recycler_view);
        emptyView = (RelativeLayout) taskListLayout.findViewById(R.id.empty_view);

        Realm realm = Realm.getDefaultInstance();

        long projectId = getArguments().getLong(PROJECT_ID);

        tasks = realm.where(TaskRealm.class)
                .equalTo(TaskRealm.PROJECT_ID, projectId)
                .findAllSorted(
                        new String[] {TaskRealm.IS_ACTIVE, TaskRealm.PRIORITY, TaskRealm.DATE, TaskRealm.TITLE},
                        new Sort[] {Sort.DESCENDING, Sort.DESCENDING, Sort.ASCENDING, Sort.ASCENDING});

        refreshTaskRecyclerView();

        TaskListAdapter taskListAdapter = new TaskListAdapter(getActivity(), tasks);
        taskRecyclerView.setAdapter(taskListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        taskRecyclerView.setLayoutManager(layoutManager);

        taskListAdapter.setListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), TaskEditorActivity.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                long taskId = tasks.get(position).getId();
                intent.putExtra(TaskEditorActivity.EXTRA_TASK_ID, taskId);
                startActivity(intent);
            }
        });

        return taskListLayout;
    }

    private void refreshTaskRecyclerView() {
        if (tasks == null || tasks.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            taskRecyclerView.setVisibility(View.GONE);
            taskListLayout.setBackgroundColor(getResources().getColor(R.color.colorEmptyView));
        } else {
            emptyView.setVisibility(View.GONE);
            taskRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
