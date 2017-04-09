package com.android.example.todoer.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.android.example.todoer.dialog.PriorityPickerFragment;
import com.android.example.todoer.dialog.ProjectPickerFragment;
import com.android.example.todoer.model.ProjectRealm;
import com.android.example.todoer.model.TaskRealm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;

public class TaskEditorActivity extends AppCompatActivity implements ProjectPickerFragment.ProjectPickerListener,
        PriorityPickerFragment.PriorityPickerListener {

    public static final String EXTRA_TASK_ID = "taskId";

    public static final String DATE_PICKER_TAG = "datePicker";
    public static final String PRIORITY_PICKER_TAG = "priorityPicker";
    public static final String PROJECT_PICKER_TAG = "projectPicker";

    public static final DateFormat dateFormat =
            new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    public static Calendar calendar = Calendar.getInstance();

    private EditText titleEditText;

    private TextView dateTextView;
    private TextView priorityTextView;
    private TextView projectTextView;

    private Realm realm;
    private TaskRealm task;
    private boolean isExistedTask = false;
    private int priority;
    private long projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        realm = Realm.getDefaultInstance();
        titleEditText = (EditText) findViewById(R.id.title_edit_text);
        dateTextView = (TextView) findViewById(R.id.editor_date);
        priorityTextView = (TextView) findViewById(R.id.editor_priority);
        projectTextView = (TextView) findViewById(R.id.editor_project);

        // Set the task's parameters
        setupTask();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });

        LinearLayout dateContainer = (LinearLayout) findViewById(R.id.date_container);
        dateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), DATE_PICKER_TAG);
            }
        });

        LinearLayout priorityContainer = (LinearLayout) findViewById(R.id.priority_container);
        priorityContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment priorityPickerFragment = PriorityPickerFragment.newInstance(priority);
                priorityPickerFragment.show(getSupportFragmentManager(), PRIORITY_PICKER_TAG);

            }
        });

        LinearLayout projectContainer = (LinearLayout) findViewById(R.id.project_container);
        projectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment projectPickerFragment = ProjectPickerFragment.newInstance(projectId);
                projectPickerFragment.show(getSupportFragmentManager(), PROJECT_PICKER_TAG);

            }
        });
    }

    private void setupTask() {
        if (getIntent().hasExtra(EXTRA_TASK_ID)) {
            // get task id from Intent
            long taskId = getIntent().getExtras().getLong(EXTRA_TASK_ID);
            // get Task from Realm
            task = realm.where(TaskRealm.class).equalTo(TaskRealm.ID, taskId).findFirst();
            // setup Task title
            titleEditText.setText(task.getTitle());
            // setup Task date
            calendar.setTimeInMillis(task.getDate());
            dateTextView.setText(dateFormat.format(task.getDate()));
            // setup Task priority
            priority = task.getPriority();
            setupPriorityContainer();
            // setup Task Project
            projectId = task.getProjectId();
            setupProjectContainer();
            // set that the Task is existed
            isExistedTask = true;
        } else {
            // set the default values to containers
            calendar = Calendar.getInstance();
            dateTextView.setText(dateFormat.format(calendar.getTimeInMillis()));
            priorityTextView.setText(getString(R.string.priority_none));
            priorityTextView.setTextColor(getResources().getColor(R.color.colorPriorityNone));
            priority = TaskRealm.PRIORITY_NONE;
            projectId = ProjectRealm.INBOX_ID;
            setupProjectContainer();
        }
    }

    /**
     * This helper method sets up the Project container
     */
    private void setupProjectContainer() {
        String projectName = Realm.getDefaultInstance().where(ProjectRealm.class)
                .equalTo(ProjectRealm.ID, projectId).findFirst().getName();
        projectTextView.setText(projectName);
    }

    /**
     * This helper method sets up the Priority container
     */
    private void setupPriorityContainer() {
        int priorityColor;
        String priorityText;

        switch (priority) {
            case TaskRealm.PRIORITY_NONE:
                priorityColor = getResources().getColor(R.color.colorPriorityNone);
                priorityText = getString(R.string.priority_none);
                break;
            case TaskRealm.PRIORITY_LOW:
                priorityColor = getResources().getColor(R.color.colorPriorityLow);
                priorityText = getString(R.string.priority_low);
                break;
            case TaskRealm.PRIORITY_MEDIUM:
                priorityColor = getResources().getColor(R.color.colorPriorityMedium);
                priorityText = getString(R.string.priority_medium);
                break;
            case TaskRealm.PRIORITY_HIGH:
                priorityColor = getResources().getColor(R.color.colorPriorityHigh);
                priorityText = getString(R.string.priority_high);
                break;
            default:
                priorityColor = getResources().getColor(R.color.colorPriorityNone);
                priorityText = getString(R.string.priority_none);
        }

        priorityTextView.setText(priorityText);
        priorityTextView.setTextColor(priorityColor);
    }

    private void saveTask() {
        String title = titleEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_task_title), Toast.LENGTH_SHORT).show();
            return;
        }

        if (isExistedTask) {
            realm.beginTransaction();
            task.setTitle(title);
            task.setDate(calendar.getTimeInMillis());
            task.setPriority(priority);
            task.setProjectId(projectId);
            realm.commitTransaction();
        } else {
            realm.beginTransaction();
            TaskRealm taskRealm = new TaskRealm();
            taskRealm.setTitle(title);
            taskRealm.setDate(calendar.getTimeInMillis());
            taskRealm.setPriority(priority);
            taskRealm.setProjectId(projectId);
            taskRealm.setActive(true);
            realm.copyToRealm(taskRealm);
            realm.commitTransaction();
        }

        Intent intent = new Intent(TaskEditorActivity.this, MainActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            if (isExistedTask) {
                realm.beginTransaction();
                task.deleteFromRealm();
                realm.commitTransaction();
                Toast.makeText(TaskEditorActivity.this,
                        "Task deleted",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TaskEditorActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProjectSet(DialogFragment dialog, long projectId) {
        this.projectId = projectId;
        setupProjectContainer();
    }

    @Override
    public void onPrioritySet(DialogFragment dialog, int priority) {
        this.priority = priority;
        setupPriorityContainer();
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private TextView dateTextView;

        @Override
        public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
            dateTextView = (TextView) getActivity().findViewById(R.id.editor_date);

            // Use the current date as the default date in the picker
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

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
