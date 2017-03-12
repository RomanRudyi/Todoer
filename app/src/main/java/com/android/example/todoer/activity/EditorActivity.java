package com.android.example.todoer.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.todoer.R;
import com.android.example.todoer.model.TaskRealm;
import com.android.example.todoer.realm.RealmController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class EditorActivity extends AppCompatActivity {

    public static String EXTRA_TASK_ID = "taskId";

    private EditText titleEditText;

    private TextView priorityTextView;

    public static final String DATE_PICKER_TAG = "datePicker";
    public static final DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    private LinearLayout dateContainer;
    private TextView dateTextView;
    public static Calendar calendar = Calendar.getInstance();

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
        dateTextView = (TextView) findViewById(R.id.editor_date);
        priorityTextView = (TextView) findViewById(R.id.editor_priority);

        if (getIntent().hasExtra(EXTRA_TASK_ID)) {
            taskId = getIntent().getExtras().getInt(EXTRA_TASK_ID);
            Toast.makeText(EditorActivity.this,
                    "id = " + taskId,
                    Toast.LENGTH_SHORT).show();
            task = realm.where(TaskRealm.class).equalTo(TaskRealm.ID, taskId).findFirst();
            titleEditText.setText(task.getTitle());
            dateTextView.setText(dateFormat.format(task.getDate()));
            setupPriority();

            isExistedTask = true;
        } else {
            dateTextView.setText(dateFormat.format(new Date().getTime()));
            priorityTextView.setText(getString(R.string.priority_none));
            priorityTextView.setTextColor(getColor(R.color.colorPriorityNone));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });

        dateContainer = (LinearLayout) findViewById(R.id.date_container);
        dateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), DATE_PICKER_TAG);
            }
        });
    }

    private void setupPriority() {
        int priority = task.getPriority();
        int priorityColor;
        String priorityText;

        switch (priority) {
            case TaskRealm.PRIORITY_LOW:
                priorityColor = getColor(R.color.colorPriorityLow);
                priorityText = getString(R.string.priority_low);
                break;
            case TaskRealm.PRIORITY_MEDIUM:
                priorityColor = getColor(R.color.colorPriorityMedium);
                priorityText = getString(R.string.priority_medium);
                break;
            case TaskRealm.PRIORITY_HIGH:
                priorityColor = getColor(R.color.colorPriorityHigh);
                priorityText = getString(R.string.priority_high);
                break;
            default:
                priorityColor = getColor(R.color.colorPriorityNone);
                priorityText = getString(R.string.priority_none);
        }

        priorityTextView.setText(priorityText);
        priorityTextView.setTextColor(priorityColor);
    }

    private void saveTask() {
        title = titleEditText.getText().toString().trim();

        if (title.isEmpty()) {
            return;
        }

        if (isExistedTask) {
            realm.beginTransaction();
            task.setTitle(title);
            task.setDate(calendar.getTimeInMillis());
            realm.commitTransaction();
        } else {
            realm.beginTransaction();
            TaskRealm taskRealm = new TaskRealm();
            taskRealm.setId(RealmController.getNextTaskId(realm));
            taskRealm.setTitle(title);
            taskRealm.setDate(calendar.getTimeInMillis());
            // TODO: replace to real priority
            taskRealm.setPriority(TaskRealm.PRIORITY_NONE);
            taskRealm.setActive(true);
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private TextView dateTextView;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            dateTextView = (TextView) getActivity().findViewById(R.id.editor_date);
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            calendar.set(year, month, day);
            dateTextView.setText(dateFormat.format(calendar.getTimeInMillis()));
        }
    }
}
