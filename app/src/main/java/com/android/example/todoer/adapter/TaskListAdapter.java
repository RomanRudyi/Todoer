package com.android.example.todoer.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    // Remember the last item shown on screen
    private int lastPosition = -1;

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
        final TaskRealm task = tasks.get(position);
        String titleText = task.getTitle();
        DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String dateText = dateFormat.format(task.getDate());
        final boolean isActive = task.isActive();

        holder.taskTitleTextView.setText(titleText);
        holder.taskDateTextView.setText(dateText);

        setupCheckBox(holder.taskCheckBox, position);
        holder.taskCheckBox.setChecked(!isActive);
        holder.taskCheckBox.setOnClickListener(new View.OnClickListener() {
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });

        // Applying the animation
        setAnimation(holder.itemView, position);
    }

    /**
     * The key method to apply the animation
     * @param viewToAnimate
     * @param position
     */
    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation =
                    AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);

            // TODO: figured out if I need it
            animation.setDuration(500);

            viewToAnimate.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView taskTitleTextView;
        TextView taskDateTextView;
        ImageView taskProjectImageView;
        CheckBox taskCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            taskTitleTextView = (TextView) itemView.findViewById(R.id.item_title);
            taskDateTextView = (TextView) itemView.findViewById(R.id.item_date);
            taskProjectImageView = (ImageView) itemView.findViewById(R.id.item_project_color);
            taskCheckBox = (CheckBox) itemView.findViewById(R.id.item_check_box);
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
