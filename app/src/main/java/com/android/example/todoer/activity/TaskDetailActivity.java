package com.android.example.todoer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.example.todoer.R;
import com.android.example.todoer.model.TaskRealm;
import com.android.example.todoer.realm.RealmController;

import io.realm.Realm;

public class TaskDetailActivity extends AppCompatActivity {

    public static String EXTRA_TASK_NO = "taskNo";

    private EditText titleEditText;

    private Realm realm;
    private TaskRealm task;
    private int taskNo;
    private boolean isExistedTask = false;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        realm = Realm.getDefaultInstance();
        titleEditText = (EditText) findViewById(R.id.title_edit_text);

        if (getIntent().hasExtra(EXTRA_TASK_NO)) {
            taskNo = getIntent().getExtras().getInt(EXTRA_TASK_NO);
            task = realm.where(TaskRealm.class).equalTo(TaskRealm.ID, taskNo).findFirst();
            titleEditText.setText(task.getTitle());
            isExistedTask = true;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });
    }

    private void saveTask() {
        title = titleEditText.getText().toString().trim();

        if (title.isEmpty()) {
            return;
        }

        if (isExistedTask) {
            realm.beginTransaction();
            task.setTitle(title);
            realm.commitTransaction();
        } else {
            realm.beginTransaction();
            TaskRealm taskRealm = new TaskRealm(RealmController.getNextTaskId(realm), title);
            realm.copyToRealm(taskRealm);
            realm.commitTransaction();
        }

        Toast.makeText(TaskDetailActivity.this,
                "Task saved",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(TaskDetailActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
