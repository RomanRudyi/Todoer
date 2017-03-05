package com.android.example.todoer.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.example.todoer.R;
import com.android.example.todoer.model.TaskRealm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private Context context;
    private RealmResults<TaskRealm> tasks;
    private Listener listener;
    private Realm realm;

    public TaskListAdapter(Context context, RealmResults<TaskRealm> tasks) {
        this.context = context;
        this.tasks = tasks;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        TextView taskTitleTextView = (TextView) cardView.findViewById(R.id.item_title);
        TextView taskDateTextView = (TextView) cardView.findViewById(R.id.item_date);
        ImageView taskProjectImageView = (ImageView) cardView.findViewById(R.id.item_project_color);
        CheckBox taskCheckBox = (CheckBox) cardView.findViewById(R.id.item_check_box);

        final TaskRealm task = tasks.get(position);
        String titleText = task.getTitle();
        DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String dateText = dateFormat.format(task.getDate());
        final boolean isActive = task.isActive();

        taskTitleTextView.setText(titleText);
        taskDateTextView.setText(dateText);

        setupCheckBox(taskCheckBox, position);
        taskCheckBox.setChecked(!isActive);
        taskCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActive) {
                    realm.beginTransaction();
                    task.setActive(false);
                    realm.commitTransaction();
                    notifyDataSetChanged();
                } else {
                    realm.beginTransaction();
                    task.setActive(true);
                    realm.commitTransaction();
                    notifyDataSetChanged();
                }
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }

    public interface Listener {
        void onClick(int position);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void setupCheckBox(CheckBox checkBox, int position) {
        int priority = tasks.get(position).getPriority();
        int colorCheckBox;

        switch (priority) {
            case TaskRealm.PRIORITY_LOW:
                colorCheckBox = context.getResources().getColor(R.color.colorPriorityLow);
                break;
            case TaskRealm.PRIORITY_MEDIUM:
                colorCheckBox = context.getResources().getColor(R.color.colorPriorityMedium);
                break;
            case TaskRealm.PRIORITY_HIGH:
                colorCheckBox = context.getResources().getColor(R.color.colorPriorityHigh);
                break;
            default:
                colorCheckBox = context.getResources().getColor(R.color.colorPriorityNone);
        }

        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {context.getResources().getColor(R.color.colorEditor), colorCheckBox};
        CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
    }
}
