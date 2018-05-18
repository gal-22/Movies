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

import com.bumptech.glide.Glide;
import com.example.galzaid.movies.Constants;
import com.example.galzaid.movies.Movie;
import com.example.galzaid.movies.activities.MovieInfoActivity;
import com.example.galzaid.movies.R;

import java.util.ArrayList;
import java.util.Objects;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>  {
    private ArrayList<Movie> movies;
    private Context context;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 1000;

    public MoviesAdapter(ArrayList<Movie> movies, Context context ) {
        this.movies = movies;
        this.context = context;

    }


    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new MoviesViewHolder(layoutInflater.inflate(R.layout.movie_square, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        holder.setData(position);
    }


    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ConstraintLayout movieConstraint;
        private TextView movieName;
        private TextView movieRate;
        private ImageView moviePoster;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            movieConstraint = itemView.findViewById(R.id.movie_holder_constraints);
            movieName = itemView.findViewById(R.id.movie_name_tv);
            movieRate = itemView.findViewById(R.id.movie_rating_tv);
            moviePoster = itemView.findViewById(R.id.movie_iv);
            itemView.setOnClickListener(this);
        }

        public void setData(int position) {
            movieConstraint.setTag(position);
            movieName.setText(movies.get(position).getMovieName());
            movieRate.setText(movies.get(position).getMovieRating() + "");
            if (!Objects.equals(movies.get(position).getMovieUrl(), "") && movies.get(position).getMovieUrl() != null) {
                Glide.with(moviePoster).load
                        (Constants.baseImageUrlSmall + movies.get(position).getMovieUrl())
                        .transition(withCrossFade())
                        .into(moviePoster);
            }
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
            Movie movie = movies.get(pos);
            Intent intent = new Intent(context, MovieInfoActivity.class);
            intent.putExtra("movie", movie);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context, moviePoster, "profile");
            context.startActivity(intent, options.toBundle());
        }
    }
}