package com.android.example.todoer.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.android.example.todoer.R;
import com.android.example.todoer.model.TaskDummy;

public class TaskDetailActivity extends AppCompatActivity {

    public static String EXTRA_TASK_NO = "taskNo";

    private EditText titleEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        int taskNo = getIntent().getExtras().getInt(EXTRA_TASK_NO);

        String title = TaskDummy.tasks[taskNo];
        titleEditText = (EditText) findViewById(R.id.title_edit_text);
        titleEditText.setText(title);
    }
}
