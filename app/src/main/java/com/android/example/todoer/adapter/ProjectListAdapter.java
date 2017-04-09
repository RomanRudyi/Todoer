package com.android.example.todoer.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.example.todoer.R;
import com.android.example.todoer.activity.ProjectEditorActivity;
import com.android.example.todoer.model.ProjectRealm;
import com.android.example.todoer.utility.OnRecyclerViewItemClickListener;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {

    private Context context;
    private RealmResults<ProjectRealm> projects;
    private OnRecyclerViewItemClickListener listener;
    private Realm realm;

    public ProjectListAdapter(Context context, RealmResults<ProjectRealm> projects) {
        this.context = context;
        this.projects = projects;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectListAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ProjectRealm poject = projects.get(position);

        // if project is Inbox - don't show it int this list
        if (poject.getId() == ProjectRealm.INBOX_ID) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)holder.cardView.getLayoutParams();
            holder.cardView.setVisibility(View.GONE);
            params.height = 0;
            params.width = 0;
        }

        String name = poject.getName();
        holder.projectTitleTextView.setText(name);
        setupProjectColor(poject.getColor(), holder.projectColorImageView);

        holder.projectEditImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProjectEditorActivity.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(ProjectEditorActivity.EXTRA_PROJECT_ID, poject.getId());
                context.startActivity(intent);
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
    }

    public void setListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView projectTitleTextView;
        ImageView projectColorImageView;
        ImageView projectEditImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.project_card_view);
            projectTitleTextView = (TextView) itemView.findViewById(R.id.project_name_text_view);
            projectColorImageView = (ImageView) itemView.findViewById(R.id.project_color_image_view);
            projectEditImageView = (ImageView) itemView.findViewById(R.id.project_edit_image_view);
        }
    }

    /**
     * This helper method sets project color
     * @param color
     * @param imageView
     */
    private void setupProjectColor(int color, ImageView imageView) {
        switch (color) {
            case ProjectRealm.COLOR_RED:
                imageView.setBackgroundColor(context.getResources()
                        .getColor(R.color.colorProjectRed));
                break;
            case ProjectRealm.COLOR_GREEN:
                imageView.setBackgroundColor(context.getResources()
                        .getColor(R.color.colorProjectGreen));
                break;
            case ProjectRealm.COLOR_BLUE:
                imageView.setBackgroundColor(context.getResources()
                        .getColor(R.color.colorProjectBlue));
                break;
            case ProjectRealm.COLOR_PURPLE:
                imageView.setBackgroundColor(context.getResources()
                        .getColor(R.color.colorProjectPurple));
                break;
            case ProjectRealm.COLOR_ORANGE:
                imageView.setBackgroundColor(context.getResources()
                        .getColor(R.color.colorProjectOrange));
                break;
        }
    }
}
