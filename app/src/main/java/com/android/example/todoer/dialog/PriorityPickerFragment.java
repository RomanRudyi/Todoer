package com.android.example.todoer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.android.example.todoer.R;
import com.android.example.todoer.activity.TaskEditorActivity;
import com.android.example.todoer.model.TaskRealm;

public class PriorityPickerFragment extends DialogFragment {

    public interface PriorityPickerListener {
        void onPrioritySet(DialogFragment dialog, int priority);
    }

    public static final String PRIORITY = "priority";

    private TaskEditorActivity taskEditorActivity;
    private RadioGroup priorityRadioGroup;
    private int priority;

    public static PriorityPickerFragment newInstance(int priority) {
        PriorityPickerFragment fragment = new PriorityPickerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PRIORITY, priority);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        priority = getArguments().getInt(PRIORITY);

        // Create a new instance of PriorityPickerDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.priority_dialog, null);

        priorityRadioGroup = (RadioGroup)
                dialogView.findViewById(R.id.priority_radio_group);
        priorityRadioGroup.check(getCheckedId());

        builder.setTitle(R.string.select_priority)
                // Inflate and set the layout for the dialog
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        priorityChanged(priorityRadioGroup.getCheckedRadioButtonId());
                        taskEditorActivity.onPrioritySet(PriorityPickerFragment.this, priority);
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

    private void priorityChanged(int checkedId) {
        switch (checkedId) {
            case R.id.rb_priority_none:
                priority = TaskRealm.PRIORITY_NONE;
                break;
            case R.id.rb_priority_low:
                priority = TaskRealm.PRIORITY_LOW;
                break;
            case R.id.rb_priority_medium:
                priority = TaskRealm.PRIORITY_MEDIUM;
                break;
            case R.id.rb_priority_high:
                priority = TaskRealm.PRIORITY_HIGH;
                break;
        }
    }

    private int getCheckedId() {
        int checkedId;
        switch (priority) {
            case TaskRealm.PRIORITY_LOW:
                checkedId = R.id.rb_priority_low;
                break;
            case TaskRealm.PRIORITY_MEDIUM:
                checkedId = R.id.rb_priority_medium;
                break;
            case TaskRealm.PRIORITY_HIGH:
                checkedId = R.id.rb_priority_high;
                break;
            default:
                checkedId = R.id.rb_priority_none;
        }
        return checkedId;
    }
}
