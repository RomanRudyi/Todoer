package com.android.example.todoer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.android.example.todoer.R;
import com.android.example.todoer.activity.TaskEditorActivity;
import com.android.example.todoer.model.ProjectRealm;

import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProjectPickerFragment extends DialogFragment {

    public interface ProjectPickerListener {
        void onProjectSet(DialogFragment dialog, long projectId);
    }

    public static final String PROJECT_ID = "projectId";

    private TaskEditorActivity taskEditorActivity;
    private Realm realm;
    private RealmResults<ProjectRealm> projects;
    private int checkedItemPosition = 0;
    private long projectId = ProjectRealm.INBOX_ID;

    public static ProjectPickerFragment newInstance(long projectId) {
        ProjectPickerFragment fragment = new ProjectPickerFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(PROJECT_ID, projectId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        projectId = getArguments().getLong(PROJECT_ID);

        realm = Realm.getDefaultInstance();

        projects = realm.where(ProjectRealm.class).findAllSorted(ProjectRealm.NAME);

        String[] projectNames = new String[projects.size()];
        for (int i = 0; i < projectNames.length; i++) {
            projectNames[i] = projects.get(i).getName();
        }

        // Create a new instance of PriorityPickerDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.select_priority)
                // Inflate and set the layout for the dialog
                .setSingleChoiceItems(projectNames, getProjectPosition(projectNames), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItemPosition = which;
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        projectId = projects.get(checkedItemPosition).getId();
                        taskEditorActivity.onProjectSet(ProjectPickerFragment.this, projectId);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ColorPickerDialogListener so we can send events to the host
            taskEditorActivity = (TaskEditorActivity) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement ColorPickerDialogListener");
        }
    }

    private int getProjectPosition(String[] projectNames) {
        int position = 0;
        String taskProjectName = realm.where(ProjectRealm.class).equalTo(ProjectRealm.ID, projectId).findFirst().getName();
        for (int i = 0; i < projectNames.length; i++) {
            if (Objects.equals(projectNames[i], taskProjectName)) {
                position = i;
            }
        }
        return position;
    }
}
