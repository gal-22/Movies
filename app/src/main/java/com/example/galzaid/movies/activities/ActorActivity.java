package com.example.galzaid.movies.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.galzaid.movies.Actor;
import com.example.galzaid.movies.Constants;
import com.example.galzaid.movies.Movie;
import com.example.galzaid.movies.R;
import com.example.galzaid.movies.adapters.MoviesAdapter;
import com.example.galzaid.movies.database.DBHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Objects;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ActorActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView actorImageView;
    private Actor selectedActor;
    private TextView actorBiographyTv, actorBiographyTitleTv, bornTv, bornTitle;
    private SmoothProgressBar progressBar;
    private ArrayList<Movie> favorites;
    private ArrayList<Movie> actorMoviesKnowFor;
    private DBHelper database;
    private RecyclerView moviesRv;
    private LinearLayoutManager linearLayoutManager;
    MoviesAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor);
        initViews();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        moviesRv.setNestedScrollingEnabled(false);
        moviesRv.setLayoutManager(linearLayoutManager);
        favorites = new ArrayList<>();
        database = new DBHelper(this);
        favorites = database.getAllFavorites();
        Intent intent = getIntent();
        selectedActor = (Actor) intent.getSerializableExtra("actor");
        if (selectedActor.getProfilePath() != null && !selectedActor.getProfilePath().equals("") && !selectedActor.getProfilePath().equals("null")) {
            //  Drawable drawable = getResources().getDrawable(R.drawable.no_photo_male);
            Glide.with(actorImageView)
                    .load(Constants.baseImageUrlSmall + fixStr(selectedActor.getProfilePath()))
                    .transition(withCrossFade())
                    .into(actorImageView);
        } else {
            Glide.with(actorImageView)
                    .load(R.drawable.no_photo_male)
                    .transition(withCrossFade())
                    .into(actorImageView);
        }
        // .loadOnFailed(drawble)
        //TODO add loadOnFailed
        getActorRequest(selectedActor.getActorId());
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(selectedActor.getName());
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
        progressBar.setFadingEdgeLength(2);
        progressBar.progressiveStart();
        int[] colors;
        colors = initColors();
        progressBar.setSmoothProgressDrawableColors(colors);
        progressBar.setSmoothProgressDrawableCallbacks(new SmoothProgressDrawable.Callbacks() {
            @Override
            public void onStop() {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onStart() {
                if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    public static String fixStr(String str) {
        if (str == null)
            str = "";
        else if (str.length() > 0) {
            if (str.charAt(0) == '"') str = str.substring(1, str.length() - 1);
            if (str.equals("null")) str = "";
        }
        return str;
    }

    public void getActorRequest(final int actorId) {  // basic information about the actor, description etc
        Ion.with(this)
                .load("https://api.themoviedb.org/3/person/" + actorId + "?api_key=" + Constants.API_KEY + "&language=en-US")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            e.printStackTrace(); //TODO handle exception
                            Toast.makeText(ActorActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
                            progressBar.progressiveStop();
                        } else getActorRequestFull(actorId, result);
                    }
                });
    }

    public void getActorRequestFull(int actorId, final JsonObject actorInformation) { // adds the known for information
        Log.i("karkar", actorId + " actor id " + actorInformation.toString());
        Ion.with(this)
                .load("https://api.themoviedb.org/3/discover/movie?api_key=" + Constants.API_KEY + "&with_cast=" + actorId)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject actorKnowFor) {
                        initialRenderPage(actorInformation, actorKnowFor);
                    }
                });
    }

    public void getMovieFullData(final Movie movie) {
        Ion.with(this)
                .load("https://api.themoviedb.org/3/movie/" + movie.getMovieId() + "?api_key=" + Constants.API_KEY + "&append_to_response=credits")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject credits) {
                        addFullDataToMovie(credits, movie);
                    }

                });
    }

    public int[] initColors() {
        int[] colors;
        colors = new int[3];
        colors[0] = Color.parseColor("#0000FF");
        colors[1] = Color.parseColor("#007300");
        colors[2] = Color.parseColor("#e50000");
        return colors;
    }

    public void initialRenderPage(JsonObject actorInformation, JsonObject actorKnowForJson) {
        JsonArray actorMoviesJsonArr = new JsonArray();
        if (actorKnowForJson.getAsJsonObject().get("results") != null) {
            actorMoviesJsonArr = actorKnowForJson.getAsJsonObject().get("results").getAsJsonArray();
        }
        actorMoviesKnowFor = createMoviesArray(actorMoviesJsonArr); // contains only the base information!
        for (int i = 0; i < actorMoviesKnowFor.size(); i++) {
            getMovieFullData(actorMoviesKnowFor.get(i));
        }
        String birthday = "";
        String placeOfBirth = "";
        String biography = "";
        if(actorInformation.getAsJsonObject().get("biography") != null) {
            biography = fixStr(actorInformation.getAsJsonObject().get("biography").toString());
        }
        if (biography.equals("")) {
            actorBiographyTitleTv.setVisibility(View.GONE);
            actorBiographyTv.setVisibility(View.GONE);
        }
        if (actorInformation.getAsJsonObject().get("birthday") != null) {
            birthday = actorInformation.getAsJsonObject().get("birthday").toString();
            if (birthday.equals("null")) birthday = "";
            else if (actorInformation.getAsJsonObject().get("place_of_birth") != null) {
                placeOfBirth = actorInformation.getAsJsonObject().get("place_of_birth").toString();
            }
        }
        actorBiographyTv.setText(biography);
        if (birthday.equals("") || birthday.equals("null")) {
            bornTitle.setVisibility(View.GONE);
            bornTv.setVisibility(View.GONE);
        } else {
            bornTv.setText(fixStr(birthday) + "\n" + fixStr(placeOfBirth));
        }
        progressBar.progressiveStop();
    }

    public void addFullDataToMovie(JsonObject credits, Movie movie) {
        for (int i = 0; i < actorMoviesKnowFor.size(); i++) {
            if (actorMoviesKnowFor.get(i).getMovieId() == movie.getMovieId()) {
                if (credits.getAsJsonObject().get("budget") != null
                        && !credits.getAsJsonObject().get("budget").toString().equals("null")
                        && !credits.getAsJsonObject().get("budget").toString().equals("")) {
                    actorMoviesKnowFor.get(i).setBudget(credits.getAsJsonObject().get("budget").getAsInt());
                } else actorMoviesKnowFor.get(i).setBudget(0);
                if (credits.getAsJsonObject().get("runtime") != null
                        && !credits.getAsJsonObject().get("runtime").toString().equals("null")
                        && !credits.getAsJsonObject().get("runtime").toString().equals("")) {
                    actorMoviesKnowFor.get(i).setRuntime(fixStr(credits.getAsJsonObject().get("runtime").toString()));
                }
                else actorMoviesKnowFor.get(i).setRuntime("");
                if (credits.getAsJsonObject().get("revenue") != null) {
                    actorMoviesKnowFor.get(i).setRevenue(credits.getAsJsonObject().get("revenue").getAsInt());
                } else actorMoviesKnowFor.get(i).setRevenue(0);
                if(credits.getAsJsonObject().get("credits") != null) {
                    if (credits.getAsJsonObject().get("credits").getAsJsonObject().get("cast") != null
                            && !credits.getAsJsonObject().get("credits").toString().equals("null")
                            && !credits.getAsJsonObject().get("credits").toString().equals("")) {
                        actorMoviesKnowFor.get(i).setActorJsonArrStr(credits.getAsJsonObject().get("credits").getAsJsonObject()
                                .get("cast").getAsJsonArray().toString());
                    }
                    else actorMoviesKnowFor.get(i).setActorJsonArrStr("");
                }
                else actorMoviesKnowFor.get(i).setActorJsonArrStr("");
                if(credits.getAsJsonObject().get("credits") != null) {
                    if(credits.getAsJsonObject().get("credits").getAsJsonObject().get("cast") != null) {
                        actorMoviesKnowFor.get(i).setActorArrayList(createActorArr(credits.getAsJsonObject().get("credits").getAsJsonObject()
                                .get("cast").getAsJsonArray()));
                    }
                    else actorMoviesKnowFor.get(i).setActorArrayList(new ArrayList<Actor>());
                }
                else actorMoviesKnowFor.get(i).setActorArrayList(new ArrayList<Actor>());
            }
        }
        renderAll();
    }

    private void renderAll() {
        moviesAdapter = new MoviesAdapter(actorMoviesKnowFor, this);
        moviesRv.setAdapter(moviesAdapter);
        progressBar.progressiveStop();
    }

    public ArrayList<Movie> createMoviesArray(JsonArray movieInfo) {
        ArrayList<Movie> moviesBaseInfo = new ArrayList<>();
        for (int i = 0; i < movieInfo.size(); i++) {
            moviesBaseInfo.add(createMovieBaseFromJson(movieInfo.get(i).getAsJsonObject()));
        }
        return moviesBaseInfo;
    }

    public Movie createMovieBaseFromJson(JsonObject movieJson) {
        Movie movie = new Movie();
        movie.setMovieName(movieJson.get("title").getAsString());
        if (movieJson.getAsJsonObject().get("poster_path") != null)
            movie.setMovieUrl(fixStr(movieJson.get("poster_path").toString()));
        else movie.setMovieUrl("");
        if (movieJson.getAsJsonObject().get("backdrop_path") != null)
            movie.setMovieSecondUrl(fixStr(movieJson.get("backdrop_path").toString()));
        else movie.setMovieSecondUrl("");
        movie.setMovieRating(movieJson.get("vote_average").getAsDouble());
        movie.setMovieId(movieJson.get("id").getAsInt());
        movie.setFavorite(isFavorite(movie.getMovieId()));
        movie.setMovieDescription(movieJson.get("overview").getAsString());
        movie.setReleaseDate(movieJson.get("release_date").getAsString());
        return movie;
    }


    public boolean isFavorite(int movieId) {
        boolean isFavorite = false;
        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.get(i).getMovieId() == movieId) {
                isFavorite = true;
            }
        }
        return isFavorite;
    }

    public ArrayList<Actor> createActorArr(JsonArray actorJsonArr) {
        ArrayList<Actor> actorArrayList = new ArrayList<>();
        for (int i = 0; i < actorJsonArr.size(); i++) {
            actorArrayList.add(Actor.createActorFromJson(actorJsonArr.get(i).getAsJsonObject()));
        }
        return actorArrayList;
    }

    public void initViews() {
        toolbar = findViewById(R.id.actor_toolbar);
        actorImageView = findViewById(R.id.actor_profile_iv);
        progressBar = findViewById(R.id.actors_progress_bar);
        actorBiographyTv = findViewById(R.id.actor_biography_tv);
        actorBiographyTitleTv = findViewById(R.id.actor_biography_title);
        bornTv = findViewById(R.id.born_tv);
        bornTitle = findViewById(R.id.born_title_tv);
        moviesRv = findViewById(R.id.movie_actor_rv);
    }
}
