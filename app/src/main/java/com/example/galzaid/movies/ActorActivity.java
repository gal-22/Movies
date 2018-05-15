package com.example.galzaid.movies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class ActorActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView actorImageView;
    private Actor selectedActor;
    private TextView actorBiographyTv;
    private final String baseActorUrl = "http://image.tmdb.org/t/p/w500";
    private SmoothProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor);
        toolbar = findViewById(R.id.actor_toolbar);
        actorImageView = findViewById(R.id.actor_profile_iv);
        progressBar = findViewById(R.id.actors_progress_bar);
        actorBiographyTv = findViewById(R.id.actor_biography_tv);
         Intent intent = getIntent();
        selectedActor = (Actor) intent.getSerializableExtra("actor");
        getActorRequest(selectedActor.getActorId());
        Glide.with(actorImageView)
                .load(baseActorUrl + fixStr(selectedActor.getProfilePath()))
                .into(actorImageView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
                onBackPressed();
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setInterpolator(new AccelerateDecelerateInterpolator());
        progressBar.setSmoothProgressDrawableProgressiveStopSpeed(3.4f);
        progressBar.setSmoothProgressDrawableStrokeWidth(20f);
        progressBar.setSmoothProgressDrawableSeparatorLength(0);
        progressBar.setSmoothProgressDrawableUseGradients(true);
        progressBar.setFadingEdgeLength(3);
        progressBar.progressiveStart();
        int[] colors;
        colors = initColors();
        progressBar.setSmoothProgressDrawableColors(colors);
        progressBar.setSmoothProgressDrawableCallbacks(new SmoothProgressDrawable.Callbacks() {
            @Override
            public void onStop() {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStart() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }




    public static String fixStr(String str) {
        assert str != null;
        if (str.charAt(0) == '"') str = str.substring(1, str.length() - 1);
        return str;
    }
    public void getActorRequest(int actorId) {
        Ion.with(this)
                .load("https://api.themoviedb.org/3/person/"  + actorId + "?api_key=ba50009df309cfd8d537ba914557af7f&language=en-US")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBar.progressiveStop();
                        renderPage(result);
                    }
                });
    }

    public int[] initColors() {
        int[] colors; colors = new int[3];
        colors[0] = Color.parseColor("#0000FF");
        colors[1] = Color.parseColor("#007300");
        colors[2] = Color.parseColor("#e50000");
        return colors;
    }

    public void renderPage(JsonObject result) {
        String biography = result.get("biography").getAsString();
        actorBiographyTv.setText(biography);
    }
}
