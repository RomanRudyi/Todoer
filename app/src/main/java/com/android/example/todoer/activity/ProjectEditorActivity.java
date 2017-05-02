package com.android.example.todoer.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.example.todoer.R;
import com.android.example.todoer.dialog.ColorPickerFragment;
import com.android.example.todoer.model.ProjectRealm;
import com.android.example.todoer.model.TaskRealm;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProjectEditorActivity extends AppCompatActivity implements ColorPickerFragment.ColorPickerDialogListener {

    public static final String EXTRA_PROJECT_ID = "projectId";

    public static final String COLOR_PICKER_TAG = "colorPicker";

    private EditText projectNameEditText;

    private LinearLayout colorContainer;

    private ImageView colorImageView;

    private Realm realm;
    private ProjectRealm project;

    private String projectName;
    private int projectColor;
    private long projectId;
    private boolean isExistedProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        projectNameEditText = (EditText) findViewById(R.id.project_name_edit_text);
        colorContainer = (LinearLayout) findViewById(R.id.color_container);
        colorImageView = (ImageView) findViewById(R.id.editor_project_color_image_view);

        realm = Realm.getDefaultInstance();

        // Set the project's parameters
        setupProject();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProject();
            }
        });

        colorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment colorPickerFragment = ColorPickerFragment.newInstance(projectColor);
                colorPickerFragment.show(getSupportFragmentManager(), COLOR_PICKER_TAG);

            }
        });
    }

    private void setupProject() {
        if (getIntent().hasExtra(EXTRA_PROJECT_ID)) {
            projectId = getIntent().getExtras().getLong(EXTRA_PROJECT_ID);
            project = realm.where(ProjectRealm.class).equalTo(ProjectRealm.ID, projectId).findFirst();

            projectNameEditText.setText(project.getName());

            projectColor = project.getColor();
            Drawable drawable = getChangedDrawable(this, projectColor);
            if (drawable != null) {
                colorImageView.setBackground(drawable);
            }

            isExistedProject = true;
        } else {
            projectColor = ProjectRealm.COLOR_ORANGE;
            Drawable drawable = getDrawable(R.drawable.circle_color_orange);
            if (drawable != null) {
                colorImageView.setBackground(drawable);
            }
            isExistedProject = false;
        }
    }

    private void saveProject() {
        projectName = projectNameEditText.getText().toString().trim();

        if (projectName.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_project_name), Toast.LENGTH_SHORT).show();
            return;
        }

        RealmResults<ProjectRealm> duplicates =
                realm.where(ProjectRealm.class).equalTo(ProjectRealm.NAME, projectName).findAll();
        if (!isExistedProject && duplicates.size() != 0) {
            Toast.makeText(this, getString(R.string.error_duplicate_project_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (isExistedProject) {
            realm.beginTransaction();
            project.setName(projectName);
            project.setColor(projectColor);
            realm.commitTransaction();
        } else {
            realm.beginTransaction();
            ProjectRealm projectRealm = new ProjectRealm();
            projectRealm.setName(projectName);
            projectRealm.setColor(projectColor);
            realm.copyToRealm(projectRealm);
            realm.commitTransaction();
        }

        Intent intent = new Intent(ProjectEditorActivity.this, MainActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private static Drawable getChangedDrawable(Context context, int projectColor) {
        Drawable drawable = context.getDrawable(R.drawable.circle_color_purple);
        switch (projectColor) {
            case ProjectRealm.COLOR_RED:
                drawable = context.getDrawable(R.drawable.circle_color_red);
                break;
            case ProjectRealm.COLOR_GREEN:
                drawable = context.getDrawable(R.drawable.circle_color_green);
                break;
            case ProjectRealm.COLOR_BLUE:
                drawable = context.getDrawable(R.drawable.circle_color_blue);
                break;
            case ProjectRealm.COLOR_PURPLE:
                drawable = context.getDrawable(R.drawable.circle_color_purple);
                break;
            case ProjectRealm.COLOR_ORANGE:
                drawable = context.getDrawable(R.drawable.circle_color_orange);
                break;
        }
        return drawable;
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
        if (!isExistedProject) {
            MenuItem delete = menu.findItem(R.id.action_delete);
            delete.setVisible(false);
        }
        MenuItem timer = menu.findItem(R.id.action_timer);
        timer.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            if (isExistedProject) {
                realm.beginTransaction();
                project.deleteFromRealm();
                RealmResults<TaskRealm> tasksToDelete = realm.where(TaskRealm.class)
                        .equalTo(TaskRealm.PROJECT_ID, projectId).findAll();
                tasksToDelete.deleteAllFromRealm();
                realm.commitTransaction();
                Toast.makeText(ProjectEditorActivity.this,
                        "Project deleted",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProjectEditorActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorSet(DialogFragment dialog, int projectColor) {
        this.projectColor = projectColor;
        // User touched the dialog's positive button
        Drawable drawable = getChangedDrawable(this, projectColor);
        if (drawable != null) {
            colorImageView.setBackground(drawable);
        } else {
            Toast.makeText(this, "color = " + projectColor + ". background isn't set", Toast.LENGTH_SHORT).show();
        }
    }
}
