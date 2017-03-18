package com.android.example.todoer.adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.example.todoer.R;
import com.android.example.todoer.model.OnRecyclerViewItemClickListener;
import com.android.example.todoer.model.ProjectRealm;

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
        String name = poject.getName();
        holder.projectTitleTextView.setText(name);
        setupProjectColor(poject.getColor(), holder.projectColorImageView);
    }

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
        }
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView projectTitleTextView;
        ImageView projectColorImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.project_card_view);
            projectTitleTextView = (TextView) itemView.findViewById(R.id.project_name_text_view);
            projectColorImageView = (ImageView) itemView.findViewById(R.id.project_color_image_view);
        }
    }
}
