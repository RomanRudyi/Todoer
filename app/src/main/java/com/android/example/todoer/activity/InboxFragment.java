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
import android.widget.Toast;

import com.android.example.todoer.R;
import com.android.example.todoer.adapter.TaskListAdapter;
import com.android.example.todoer.model.TaskRealm;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class InboxFragment extends Fragment {

    private RelativeLayout taskListLayout;
    private RecyclerView taskRecyclerView;
    private RelativeLayout emptyView;
    private TaskListAdapter taskListAdapter;

    private Realm realm;
    private RealmResults<TaskRealm> tasks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        // Inflate the layout for this fragment
        taskListLayout = (RelativeLayout)
                inflater.inflate(R.layout.fragment_task_list, container, false);
        taskRecyclerView = (RecyclerView) taskListLayout.findViewById(R.id.task_recycler_view);
        emptyView = (RelativeLayout) taskListLayout.findViewById(R.id.empty_view);

        tasks = realm.where(TaskRealm.class).findAllSorted(
                new String[] {TaskRealm.IS_ACTIVE, TaskRealm.TITLE},
                new Sort[] {Sort.DESCENDING, Sort.ASCENDING});

        refreshTaskRecyclerView();

        taskListAdapter = new TaskListAdapter(getActivity(), tasks);
        taskRecyclerView.setAdapter(taskListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        taskRecyclerView.setLayoutManager(layoutManager);

        taskListAdapter.setListener(new TaskListAdapter.Listener() {
            @Override
            public void onClick(int position) {
                int taskId = tasks.get(position).getId();
                Toast.makeText(getActivity(),
                        "id = " + taskId,
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), EditorActivity.class);
                intent.putExtra(EditorActivity.EXTRA_TASK_ID, taskId);
                startActivity(intent);
            }
        });

        return taskListLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshTaskRecyclerView();
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
