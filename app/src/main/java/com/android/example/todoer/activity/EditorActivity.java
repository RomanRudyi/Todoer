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

    public static final String PRIORITY_PICKER_TAG = "priorityPicker";
    private LinearLayout priorityContainer;
    private TextView priorityTextView;
    private static int priority;

    public static final String DATE_PICKER_TAG = "datePicker";
    public static final DateFormat dateFormat =
            new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
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

        // Set the task's parameters
        if (getIntent().hasExtra(EXTRA_TASK_ID)) {
            taskId = getIntent().getExtras().getInt(EXTRA_TASK_ID);
            /*Toast.makeText(EditorActivity.this,
                    "id = " + taskId,
                    Toast.LENGTH_SHORT).show();*/
            task = realm.where(TaskRealm.class).equalTo(TaskRealm.ID, taskId).findFirst();
            // Set the title
            titleEditText.setText(task.getTitle());
            // Set the date to the Calendar
            calendar.setTimeInMillis(task.getDate());
            // Set the date to the TextView
            dateTextView.setText(dateFormat.format(task.getDate()));
            // Set the priority to the variable
            priority = task.getPriority();
            // Set the priority to the TextView
            setupPriority();

            isExistedTask = true;
        } else {
            dateTextView.setText(dateFormat.format(new Date().getTime()));
            priorityTextView.setText(getString(R.string.priority_none));
            priorityTextView.setTextColor(getColor(R.color.colorPriorityNone));
            priority = TaskRealm.PRIORITY_NONE;
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

        priorityContainer = (LinearLayout) findViewById(R.id.priority_container);
        priorityContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment priorityPickerFragment = new PriorityPickerFragment();
                priorityPickerFragment.show(getSupportFragmentManager(), PRIORITY_PICKER_TAG);

            }
        });
    }

    // TODO: optimize the code
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
            return;
        }

        if (isExistedTask) {
            realm.beginTransaction();
            task.setTitle(title);
            task.setDate(calendar.getTimeInMillis());
            task.setPriority(priority);
            realm.commitTransaction();
        } else {
            realm.beginTransaction();
            TaskRealm taskRealm = new TaskRealm();
            taskRealm.setId(RealmController.getNextTaskId(realm));
            taskRealm.setTitle(title);
            taskRealm.setDate(calendar.getTimeInMillis());
            taskRealm.setPriority(priority);
            taskRealm.setActive(true);
            realm.copyToRealm(taskRealm);
            realm.commitTransaction();
        }

        /*Toast.makeText(EditorActivity.this,
                "Task saved",
                Toast.LENGTH_SHORT).show();*/
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

    public static class PriorityPickerFragment extends DialogFragment {

        private TextView priorityTextView;
        private RadioGroup priorityRadioGroup;
        int localPriority = priority;

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
            priorityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int priorityColor;
                    String priorityText;

                    switch (checkedId) {
                        case R.id.rb_priority_none:
                            localPriority = TaskRealm.PRIORITY_NONE;
                            priorityColor = getActivity().getResources()
                                    .getColor(R.color.colorPriorityNone);
                            priorityText = getString(R.string.priority_none);
                            break;
                        case R.id.rb_priority_low:
                            localPriority = TaskRealm.PRIORITY_LOW;
                            priorityColor = getActivity().getResources()
                                    .getColor(R.color.colorPriorityLow);
                            priorityText = getString(R.string.priority_low);
                            break;
                        case R.id.rb_priority_medium:
                            localPriority = TaskRealm.PRIORITY_MEDIUM;
                            priorityColor = getActivity().getResources()
                                    .getColor(R.color.colorPriorityMedium);
                            priorityText = getString(R.string.priority_medium);
                            break;
                        case R.id.rb_priority_high:
                            localPriority = TaskRealm.PRIORITY_HIGH;
                            priorityColor = getActivity().getResources()
                                    .getColor(R.color.colorPriorityHigh);
                            priorityText = getString(R.string.priority_high);
                            break;
                        default:
                            localPriority = priority;
                            priorityColor = getActivity().getResources()
                                    .getColor(R.color.colorPriorityNone);
                            priorityText = getString(R.string.priority_none);
                    }

                    priorityTextView.setText(priorityText);
                    priorityTextView.setTextColor(priorityColor);
                }
            });

            builder.setTitle(R.string.select_priority)
                    // Inflate and set the layout for the dialog
                    .setView(dialogView)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            priority = localPriority;
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
    }
}
