package com.android.example.todoer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.example.todoer.R;
import com.android.example.todoer.model.TaskRealm;
import com.android.example.todoer.realm.RealmController;

import io.realm.Realm;

public class EditorActivity extends AppCompatActivity {

    public static String EXTRA_TASK_ID = "taskId";

    private EditText titleEditText;

    private Realm realm;
    private TaskRealm task;
    private int taskId;
    private boolean isExistedTask = false;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        realm = Realm.getDefaultInstance();
        titleEditText = (EditText) findViewById(R.id.title_edit_text);

        if (getIntent().hasExtra(EXTRA_TASK_ID)) {
            taskId = getIntent().getExtras().getInt(EXTRA_TASK_ID);
            Toast.makeText(EditorActivity.this,
                    "id = " + taskId,
                    Toast.LENGTH_SHORT).show();
            task = realm.where(TaskRealm.class).equalTo(TaskRealm.ID, taskId).findFirst();
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

        Toast.makeText(EditorActivity.this,
                "Task saved",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EditorActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new task, hide the "Delete" menu item.
        if (!isExistedTask) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            if (isExistedTask) {
                realm.beginTransaction();
                task.deleteFromRealm();
                realm.commitTransaction();
                Toast.makeText(EditorActivity.this,
                        "Task deleted",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
