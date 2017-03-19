package com.android.example.todoer.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.todoer.R;
import com.android.example.todoer.model.ProjectRealm;
import com.android.example.todoer.model.TaskRealm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class TaskEditorActivity extends AppCompatActivity {

    public static String EXTRA_TASK_ID = "taskId";

    private EditText titleEditText;
    private String title;

    public static final String DATE_PICKER_TAG = "datePicker";
    public static final DateFormat dateFormat =
            new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    private LinearLayout dateContainer;
    private TextView dateTextView;
    public static Calendar calendar = Calendar.getInstance();

    public static final String PRIORITY_PICKER_TAG = "priorityPicker";
    private LinearLayout priorityContainer;
    private TextView priorityTextView;
    private static int priority;

    public static final String PROJECT_PICKER_TAG = "projectPicker";
    private LinearLayout projectContainer;
    private TextView projectTextView;
    private static long projectId;

    private Realm realm;
    private TaskRealm task;
    private long taskId;
    private boolean isExistedTask = false;

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

        setupTask();

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

        priorityContainer = (LinearLayout) findViewById(R.id.priority_container);
        priorityContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment priorityPickerFragment = new PriorityPickerFragment();
                priorityPickerFragment.show(getSupportFragmentManager(), PRIORITY_PICKER_TAG);

            }
        });

        projectContainer = (LinearLayout) findViewById(R.id.project_container);
        projectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment projectPickerFragment = new ProjectPickerFragment();
                projectPickerFragment.show(getSupportFragmentManager(), PROJECT_PICKER_TAG);

            }
        });
    }

    private void setupTask() {
        // Set the task's parameters
        if (getIntent().hasExtra(EXTRA_TASK_ID)) {
            taskId = getIntent().getExtras().getLong(EXTRA_TASK_ID);
            task = realm.where(TaskRealm.class).equalTo(TaskRealm.ID, taskId).findFirst();

            titleEditText.setText(task.getTitle());

            calendar.setTimeInMillis(task.getDate());
            dateTextView.setText(dateFormat.format(task.getDate()));

            priority = task.getPriority();
            setupPriority();

            projectId = task.getProjectId();
            setupProject(projectId, projectTextView, getString(R.string.drawer_inbox));

            isExistedTask = true;
        } else {
            calendar = Calendar.getInstance();
            dateTextView.setText(dateFormat.format(calendar.getTimeInMillis()));
            priorityTextView.setText(getString(R.string.priority_none));
            priorityTextView.setTextColor(getColor(R.color.colorPriorityNone));
            priority = TaskRealm.PRIORITY_NONE;
            projectId = ProjectRealm.INBOX_ID;
            projectTextView.setText(getString(R.string.drawer_inbox));
        }
    }

    private static void setupProject(long projectId, TextView projectTextView, String inboxName) {
        if (projectId == ProjectRealm.INBOX_ID) {
            projectTextView.setText(inboxName);
        } else {
            String projectName = Realm.getDefaultInstance().where(ProjectRealm.class)
                    .equalTo(ProjectRealm.ID, projectId).findFirst().getName();
            projectTextView.setText(projectName);
        }
    }

    private void setupPriority() {
        int priorityColor;
        String priorityText;

        switch (priority) {
            case TaskRealm.PRIORITY_NONE:
                priorityColor = getColor(R.color.colorPriorityNone);
                priorityText = getString(R.string.priority_none);
                break;
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
    public boolean onOptionsItemSelected(MenuItem item) {
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private TextView dateTextView;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
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

    //TODO: use interface
    public static class PriorityPickerFragment extends DialogFragment {

        private TextView priorityTextView;
        private RadioGroup priorityRadioGroup;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            priorityTextView = (TextView) getActivity().findViewById(R.id.editor_priority);

            // Create a new instance of PriorityPickerDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // Get the layout inflater
            final LayoutInflater inflater = getActivity().getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.priority_dialog, null);

            priorityRadioGroup = (RadioGroup)
                    dialogView.findViewById(R.id.priority_radio_group);


            builder.setTitle(R.string.select_priority)
                    // Inflate and set the layout for the dialog
                    .setView(dialogView)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            priorityChanged(priorityRadioGroup.getCheckedRadioButtonId());
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PriorityPickerFragment.this.getDialog().cancel();
                        }
                    });

            return builder.create();
        }

        private void priorityChanged(int checkedId) {
            int priorityColor = 0;
            String priorityText = "";

            switch (checkedId) {
                case R.id.rb_priority_none:
                    priority = TaskRealm.PRIORITY_NONE;
                    priorityColor = getActivity().getResources()
                            .getColor(R.color.colorPriorityNone);
                    priorityText = getString(R.string.priority_none);
                    break;
                case R.id.rb_priority_low:
                    priority = TaskRealm.PRIORITY_LOW;
                    priorityColor = getActivity().getResources()
                            .getColor(R.color.colorPriorityLow);
                    priorityText = getString(R.string.priority_low);
                    break;
                case R.id.rb_priority_medium:
                    priority = TaskRealm.PRIORITY_MEDIUM;
                    priorityColor = getActivity().getResources()
                            .getColor(R.color.colorPriorityMedium);
                    priorityText = getString(R.string.priority_medium);
                    break;
                case R.id.rb_priority_high:
                    priority = TaskRealm.PRIORITY_HIGH;
                    priorityColor = getActivity().getResources()
                            .getColor(R.color.colorPriorityHigh);
                    priorityText = getString(R.string.priority_high);
                    break;
            }

            priorityTextView.setText(priorityText);
            priorityTextView.setTextColor(priorityColor);
        }
    }

    public static class ProjectPickerFragment extends DialogFragment {

        private TextView projectTextView;
        private Realm realm;
        private RealmResults<ProjectRealm> projects;
        private int checkedItemPosition = 0;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            projectTextView = (TextView) getActivity().findViewById(R.id.editor_project);

            realm = Realm.getDefaultInstance();

            projects = realm.where(ProjectRealm.class).findAllSorted(ProjectRealm.NAME);

            //TODO: fix this - user can't create two projects with the same names
            String[] projectNames = new String[projects.size() + 1];
            projectNames[0] = getActivity().getString(R.string.drawer_inbox);
            for (int i = 1; i < projectNames.length; i++) {
                projectNames[i] = projects.get(i - 1).getName();
            }

            // Create a new instance of PriorityPickerDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.select_priority)
                    // Inflate and set the layout for the dialog
                    .setSingleChoiceItems(projectNames, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkedItemPosition = which;
                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (checkedItemPosition == ProjectRealm.INBOX_ID) {
                                projectId = ProjectRealm.INBOX_ID;
                            } else {
                                projectId = projects.get(checkedItemPosition - 1).getId();
                            }
                            setupProject(projectId, projectTextView, getString(R.string.drawer_inbox));
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProjectPickerFragment.this.getDialog().cancel();
                        }
                    });

            return builder.create();
        }
    }
}
