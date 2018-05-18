package com.example.galzaid.movies.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import com.bumptech.glide.Glide;
import com.example.galzaid.movies.Actor;
import com.example.galzaid.movies.activities.ActorActivity;
import com.example.galzaid.movies.R;

import java.util.ArrayList;
import java.util.Objects;

public class ActorsAdapter extends RecyclerView.Adapter<ActorsAdapter.ActorsViewHolder> {
    private static final String baseMovieUrl = "http://image.tmdb.org/t/p/w300";
    private ArrayList<Actor> actors;
    private Context context;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 1000;

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
            itemView.setOnClickListener(this);
        }

        public void setData(int position) {
            actorConstraint.setTag(position);
            actorName.setText(actors.get(position).getName());
            actorRole.setText(actors.get(position).getRole());
            if (!Objects.equals(actors.get(position).getProfilePath(), "") && actors.get(position).getProfilePath() != null) {
                if(actors.get(position).getProfilePath() != null && !actors.get(position).getProfilePath().equals("") && !actors.get(position).getProfilePath().equals("null")) {
                    //  Drawable drawable = context.getResources().getDrawable(R.drawable.no_photo_male);
                    Glide.with(actorPicture).load
                            (baseMovieUrl + fixStr(actors.get(position).getProfilePath()))
                            .transition(withCrossFade())
                            .into(actorPicture);
                    //.onLoadFailed(drawable)
                    //TODO onLoadFailed
                }
                else {
                    Glide.with(actorPicture)
                            .load(R.drawable.no_photo_male)
                            .transition(withCrossFade())
                            .into(actorPicture);
                }
            }
            Log.i("sat" , actors.get(position).getProfilePath());
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            long now = System.currentTimeMillis();
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return;
            }
            mLastClickTime = now;
                changeActivity(pos);

        }

        private void changeActivity(int pos) {
            Intent intent = new Intent(context,ActorActivity.class);
            intent.putExtra("actor" , actors.get(pos));
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context, actorPicture, "actor_trans");
            context.startActivity(intent, options.toBundle());
        }
    }
    public static String fixStr(String str) {
        assert str != null;
        if (str.charAt(0) == '"') str = str.substring(1, str.length() - 1);
            return str;
    }


}