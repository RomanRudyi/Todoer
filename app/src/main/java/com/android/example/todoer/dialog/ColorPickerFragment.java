package com.android.example.todoer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.android.example.todoer.R;
import com.android.example.todoer.activity.ProjectEditorActivity;
import com.android.example.todoer.model.ProjectRealm;

public class ColorPickerFragment extends DialogFragment {

    public interface ColorPickerDialogListener {
        void onColorSet(DialogFragment dialog, int projectColor);
    }

    public static final String PROJECT_COLOR = "project_color";

    private RadioGroup colorRadioGroup;
    private ProjectEditorActivity projectEditorActivity;
    private int projectColor;

    public static ColorPickerFragment newInstance(int projectColor) {
        ColorPickerFragment fragment = new ColorPickerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PROJECT_COLOR, projectColor);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        projectColor = getArguments().getInt(PROJECT_COLOR);

        // Create a new instance of ColorPickerDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.color_dialog, null);

        colorRadioGroup = (RadioGroup)
                dialogView.findViewById(R.id.color_radio_group);
        colorRadioGroup.check(getCheckedId());

        builder.setTitle(R.string.select_priority)
                // Inflate and set the layout for the dialog
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        colorChanged(colorRadioGroup.getCheckedRadioButtonId());
                        projectEditorActivity.onColorSet(ColorPickerFragment.this, projectColor);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ColorPickerFragment.this.getDialog().cancel();
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
            projectEditorActivity = (ProjectEditorActivity) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ColorPickerDialogListener");
        }
    }

    private void colorChanged(int checkedId) {
        switch (checkedId) {
            case R.id.rb_color_red:
                projectColor = ProjectRealm.COLOR_RED;
                break;
            case R.id.rb_color_green:
                projectColor = ProjectRealm.COLOR_GREEN;
                break;
            case R.id.rb_color_blue:
                projectColor = ProjectRealm.COLOR_BLUE;
                break;
            case R.id.rb_color_purple:
                projectColor = ProjectRealm.COLOR_PURPLE;
                break;
            case R.id.rb_color_orange:
                projectColor = ProjectRealm.COLOR_ORANGE;
                break;
        }
    }

    private int getCheckedId() {
        int checkedId = R.id.rb_color_red;
        switch (projectColor) {
            case ProjectRealm.COLOR_RED:
                checkedId = R.id.rb_color_red;
                break;
            case ProjectRealm.COLOR_GREEN:
                checkedId = R.id.rb_color_green;
                break;
            case ProjectRealm.COLOR_BLUE:
                checkedId = R.id.rb_color_blue;
                break;
            case ProjectRealm.COLOR_ORANGE:
                checkedId = R.id.rb_color_orange;
                break;
            case ProjectRealm.COLOR_PURPLE:
                checkedId = R.id.rb_color_purple;
                break;
        }
        return checkedId;
    }
}
