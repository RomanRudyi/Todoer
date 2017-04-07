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

import io.realm.Realm;
import io.realm.RealmResults;

public class ProjectPickerFragment extends DialogFragment {
    public interface ProjectPickerDialogListener {
        void onProjectPickerDialogPositiveClick(DialogFragment dialog, long projectId);
    }

    public static final String PROJECT_ID = "projectId";

    private TaskEditorActivity taskEditorActivity;
    private int checkedItemPosition = 0;
    private long projectId;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        projectId = savedInstanceState.getLong(PROJECT_ID);

        Realm realm = Realm.getDefaultInstance();

        final RealmResults<ProjectRealm> projects =
                realm.where(ProjectRealm.class).findAllSorted(ProjectRealm.NAME);

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
                        taskEditorActivity.onProjectPickerDialogPositiveClick(ProjectPickerFragment.this, projectId);
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
}
