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
        void onColorPickerDialogPositiveClick(DialogFragment dialog);
    }

    private RadioGroup colorRadioGroup;
    private ProjectEditorActivity projectEditorActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of ColorPickerDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.color_dialog, null);

        colorRadioGroup = (RadioGroup)
                dialogView.findViewById(R.id.color_radio_group);


        builder.setTitle(R.string.select_priority)
                // Inflate and set the layout for the dialog
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        colorChanged(colorRadioGroup.getCheckedRadioButtonId());
                        projectEditorActivity.onColorPickerDialogPositiveClick(ColorPickerFragment.this);
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
                projectEditorActivity.setProjectColor(ProjectRealm.COLOR_RED);
                break;
            case R.id.rb_color_green:
                projectEditorActivity.setProjectColor(ProjectRealm.COLOR_GREEN);
                break;
            case R.id.rb_color_blue:
                projectEditorActivity.setProjectColor(ProjectRealm.COLOR_BLUE);
                break;
            case R.id.rb_color_purple:
                projectEditorActivity.setProjectColor(ProjectRealm.COLOR_PURPLE);
                break;
            case R.id.rb_color_orange:
                projectEditorActivity.setProjectColor(ProjectRealm.COLOR_ORANGE);
                break;
        }
    }
}
