package com.android.example.todoer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.example.todoer.R;
import com.android.example.todoer.model.TaskDummy;

public class TaskDetailActivity extends AppCompatActivity {

    public static String EXTRA_TASK_NO = "taskNo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int taskNo = (Integer) getIntent().getExtras().getInt(EXTRA_TASK_NO);

        TextView taskDetailTextView = (TextView) findViewById(R.id.task_detail_text_view);
        taskDetailTextView.setText(TaskDummy.tasks[taskNo]);
    }
}
