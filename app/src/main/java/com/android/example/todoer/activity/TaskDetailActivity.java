package com.android.example.todoer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.android.example.todoer.R;
import com.android.example.todoer.model.TaskRealm;

import io.realm.Realm;

public class TaskDetailActivity extends AppCompatActivity {

    public static String EXTRA_TASK_NO = "taskNo";

    private EditText titleEditText;

    private Realm realm;
    private TaskRealm task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        realm = Realm.getDefaultInstance();

        int taskNo = getIntent().getExtras().getInt(EXTRA_TASK_NO);

        task = realm.where(TaskRealm.class).equalTo(TaskRealm.ID, taskNo).findFirst();

        titleEditText = (EditText) findViewById(R.id.title_edit_text);
        titleEditText.setText(task.getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String changedTitle = titleEditText.getText().toString();
                realm.beginTransaction();
                task.setTitle(changedTitle);
                realm.commitTransaction();

                Intent intent = new Intent(TaskDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
