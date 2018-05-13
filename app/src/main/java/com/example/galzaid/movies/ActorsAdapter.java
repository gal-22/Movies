package com.example.galzaid.movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

public class ActorsAdapter extends RecyclerView.Adapter<ActorsAdapter.ActorsViewHolder> {
    private static final String baseMovieUrl = "http://image.tmdb.org/t/p/w500";
    private ArrayList<Actor> actors;
    private Context context;

    public ActorsAdapter(ArrayList<Actor> actors, Context context) {
        this.actors = actors;
        this.context = context;
    }

    @NonNull
    @Override
    public ActorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ActorsViewHolder(layoutInflater.inflate(R.layout.actor_square, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ActorsViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return actors.size();
    }

    public class ActorsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        private ConstraintLayout actorConstraint;
        private TextView actorName;
        private TextView actorRole;
        private ImageView actorPicture;

        public ActorsViewHolder(View itemView)  {
            super(itemView);
            actorConstraint = itemView.findViewById(R.id.actor_holder_constraints);
            actorName = itemView.findViewById(R.id.actor_name_tv);
            actorRole = itemView.findViewById(R.id.actor_role_tv);
            actorPicture = itemView.findViewById(R.id.actor_iv);
            actorConstraint.setOnClickListener(this);
        }

        public void setData(int position) {
            actorConstraint.setTag(position);
            actorName.setText(actors.get(position).getName());
            actorRole.setText(actors.get(position).getRole());
            if (!Objects.equals(actors.get(position).getProfilePath(), "") && actors.get(position).getProfilePath() != null) {
                Glide.with(actorPicture).load
                        (baseMovieUrl + fixStr(actors.get(position).getProfilePath()))
                        .into(actorPicture);
            }
            Log.i("sat" , actors.get(position).getProfilePath());
        }

        @Override
        public void onClick(View view) {

        }
    }
    public static String fixStr(String str) {
        assert str != null;
        if (str.charAt(0) == '"') str = str.substring(1, str.length() - 1);
            return str;
    }
}