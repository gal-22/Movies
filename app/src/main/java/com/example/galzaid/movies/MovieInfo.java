package com.example.galzaid.movies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.galzaid.movies.database.DBHelper;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class MovieInfo extends AppCompatActivity {
    private static final String baseMovieUrl = "http://image.tmdb.org/t/p/w500";
    private static final String baseTrailerUrl = "http://api.themoviedb.org/3/movie/";
    private static final String trailerUrlEnd = "/videos?api_key=";
    private final String API_KEY = "ba50009df309cfd8d537ba914557af7f";
    private ArrayList<String> movieTrailersUrlKeys;
    private ImageView movieIv;
    private TextView movieOverviewTv;
    private TextView movieTitleTv;
    private Toolbar toolbar;
    private ImageView movieBottomIv;
    private TextView releaseDate;
    private TextView rating;
    private TextView budgetTv;
    private TextView revenueTv;
    public TextView runtimeTv;
    private RecyclerView actorsRv;
    private FloatingActionButton likeFab;
    private AppBarLayout appBarLayout;
    private MenuItem likeMenu;
    private Movie selectedMovie;
    private DBHelper database;
    private TextView trailerTv;
    private Context context;
    private YouTubePlayerSupportFragment frag;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private LinearLayoutManager linearLayoutManager;
    private ActorsAdapter actorsAdapter;
    private ArrayList<Actor> actorArrayList;
    private TextView actorNameTv;
    private TextView actorRoleTv;
    private ImageView actorImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        frag = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    //I assume the below String value is your video id
                    youTubePlayer.cueVideo(fixStr(movieTrailersUrlKeys.get(0)));
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        context = this;
        movieTrailersUrlKeys = new ArrayList<>();
        movieIv = findViewById(R.id.movie_iv);
        actorsRv = findViewById(R.id.actors_rv);
        trailerTv = findViewById(R.id.trailers_title_Tv);
        movieOverviewTv = findViewById(R.id.movie_overview_tv);
        movieTitleTv = findViewById(R.id.movie_title_tv);
        toolbar = findViewById(R.id.main_toolbar);
        movieBottomIv = findViewById(R.id.movie_bottom_iv);
        rating = findViewById(R.id.movie_rating_info_tv);
        releaseDate = findViewById(R.id.movie_release_date_tv);
        revenueTv = findViewById(R.id.revenues_tv);
        budgetTv = findViewById(R.id.budget_tv);
        likeFab = findViewById(R.id.like_fab);
        appBarLayout = findViewById(R.id.main_appbar);
        runtimeTv = findViewById(R.id.runtime_tv);
        database = new DBHelper(this);
        actorArrayList = new ArrayList<>();  // TODO add actors
        // FIXME: 13/05/2018
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        actorsRv.setNestedScrollingEnabled(false);
        actorsRv.setLayoutManager(linearLayoutManager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        selectedMovie = (Movie) intent.getSerializableExtra("movie");
        selectedMovie.setActorArrayList(strToArrayList(selectedMovie.getActorJsonArrStr()));
        actorNameTv = findViewById(R.id.actor_name_tv);
        actorRoleTv = findViewById(R.id.actor_role_tv);
        actorImage = findViewById(R.id.actor_iv);
        actorArrayList = new ArrayList<>();
        actorArrayList = selectedMovie.getActorArrayList();
        actorsAdapter = new ActorsAdapter(actorArrayList, this , true);
        actorsRv.setAdapter(actorsAdapter);
        getAllTrailers();
        selectedMovie.setFavorite(database.isFavorite(selectedMovie));
        if (selectedMovie.isFavorite()) {
            likeFab.setImageResource(R.drawable.ic_favorite);
            if (likeMenu != null)
                likeMenu.setIcon(R.drawable.ic_favorite);
        } else {
            likeFab.setImageResource(R.drawable.ic_favorite_border);
            if (likeMenu != null)
                likeMenu.setIcon(R.drawable.ic_favorite_border);
        }
        releaseDate.setText(selectedMovie.getReleaseDate());
        runtimeTv.setText("Runtime: " + selectedMovie.getRuntime() + " minutes");
        budgetTv.setText("Budget: " + changeMoney(selectedMovie.getBudget()));
        revenueTv.setText("Revenues: " + changeMoney(selectedMovie.getRevenue()));
        rating.setText(selectedMovie.getMovieRating() + "");
        Glide.with(movieIv)
                .load(baseMovieUrl + fixStr(selectedMovie.getMovieSecondUrl()))
                .into(movieIv);
        Glide.with(movieBottomIv)
                .load(baseMovieUrl + selectedMovie.getMovieUrl())
                .into(movieBottomIv);

        getSupportActionBar().setTitle(""); // sets the title of the movie on toolbar
        movieOverviewTv.setText(selectedMovie.getMovieDescription());
        movieTitleTv.setText(selectedMovie.getMovieName());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
                onBackPressed();
            }
        });


        likeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == likeFab.getId()) {
                    boolean isFavorite = selectedMovie.isFavorite(); // TODO change to a movie that is recived from some favorite movie db
                    if (isFavorite) {
                        likeFab.setImageResource(R.drawable.ic_favorite_border);
                        likeMenu.setIcon(R.drawable.ic_favorite_border);
                        selectedMovie.setFavorite(false);
                        database.deleteMovie(selectedMovie);
                    } else {
                        likeFab.setImageResource(R.drawable.ic_favorite);
                        likeMenu.setIcon(R.drawable.ic_favorite);
                        selectedMovie.setFavorite(true);
                        database.addNewMovie(selectedMovie);
                    }
                }
            }
        });

        //  Tells If the collpasing layout is collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Collapsed
                    Log.i("Collapsed", "Collapsed");
                    if (likeMenu != null) likeMenu.setVisible(true);
                } else if (verticalOffset == 0) {
                    // Expanded
//                      if(likeMenu != null) likeMenu.setVisible(false);
                    if (selectedMovie.isFavorite()) {
                        likeFab.setImageResource(R.drawable.ic_favorite);
                    } else {
                        likeFab.setImageResource(R.drawable.ic_favorite_border);
                    }

                    Log.i("Expanded", " Da!");
                } else {
                    // Somewhere in between
                    Log.i("Between", "between");
                    Log.i("Height", "" + verticalOffset + " total + " + appBarLayout.getTotalScrollRange() + " aha " + likeFab.getVisibility());
                    if(likeFab != null) {
                        if (likeFab.getVisibility() == View.VISIBLE)
                            if(likeMenu != null) likeMenu.setVisible(false);
                    }
                    if (selectedMovie.isFavorite())
                        likeFab.setImageResource(R.drawable.ic_favorite);
                    else likeFab.setImageResource(R.drawable.ic_favorite_border);

                    if (likeFab.getVisibility() == View.INVISIBLE || likeFab.getVisibility() == View.GONE)
                        likeMenu.setVisible(true);
                    if (selectedMovie.isFavorite())
                        likeFab.setImageResource(R.drawable.ic_favorite);
                    else likeFab.setImageResource(R.drawable.ic_favorite_border);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    // this function inflates the menu with the heart icon...
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_info_menu, menu);
        likeMenu = menu.findItem(R.id.like);
        return true;
    }

    // tells if the menu item (in this case the heart) was pressed...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (selectedMovie.isFavorite()) {
            item.setIcon(R.drawable.ic_favorite_border);
            selectedMovie.setFavorite(false);
            database.deleteMovie(selectedMovie);
        } else {
            item.setIcon(R.drawable.ic_favorite);  // TODO change from a db of movies
            selectedMovie.setFavorite(true);
            database.addNewMovie(selectedMovie);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // Does the menu load before onCreate executes
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem likeMenu = menu.findItem(R.id.like);
        if (database.isFavorite(selectedMovie)) {
            likeMenu.setIcon(R.drawable.ic_favorite); // TODO change from a db of movies
            //  selectedMovie.setFavorite(false);
            //   database.deleteMovie(selectedMovie);
        } else {
            likeMenu.setIcon(R.drawable.ic_favorite_border);  // TODO change from a db of movies
            //   selectedMovie.setFavorite(true);
            //    database.addNewMovie(selectedMovie);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private static String fixStr(String str) {
        if (str == null)
            str = "";
       else if(str.length() > 0) {
            if (str.charAt(0) == '"')
                str = str.substring(1, str.length() - 1);
        }
        else str = "";
        return str;
    }

    private void getAllTrailers() {
        Log.i("farfar", baseTrailerUrl + selectedMovie.getMovieId() + trailerUrlEnd + API_KEY);
        Ion.with(this)
                .load(baseTrailerUrl + selectedMovie.getMovieId() + trailerUrlEnd + API_KEY)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            e.printStackTrace(); // if there is an error e. TODO Handle exception
                            Toast.makeText(MovieInfo.this, "Error with getting data", Toast.LENGTH_SHORT).show();
                            Log.i("results", e.getMessage());
                        } else {
                            if (result.getAsJsonObject().get("results") != null) { // if movies video exists
                                if (result.get("results").getAsJsonArray() != null) {
                                    if (result.get("results").getAsJsonArray().size() > 0) {
                                        if (Objects.equals(fixStr(result.get("results").getAsJsonArray().get(0).getAsJsonObject().get("site").toString().toLowerCase()), "youtube")) { // if the site is youtube
                                            String trailerKey = result.get("results").getAsJsonArray().get(0).getAsJsonObject().get("key").toString();
                                            movieTrailersUrlKeys.add(trailerKey);
                                            frag.initialize(PlayerConfig.API_KEY, onInitializedListener);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    }

    public String changeMoney(int num) { // receives an int returns a fixed string of the int
        double fixedNum;
        DecimalFormat df = new DecimalFormat("###.#");
        fixedNum = (double) num;
        String moneyStr;
        if (num >= 1000000000) {
            fixedNum = round(fixedNum / 1000000000, 2);
            moneyStr = df.format(fixedNum) + " Billion $";
        } else if (num >= 1000000) {
            fixedNum = round(fixedNum / 1000000, 2);
            moneyStr = df.format(fixedNum) + " Million $";
        } else moneyStr = num + "$";
        return moneyStr;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public ArrayList<Actor> strToArrayList(String str){
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();
        ArrayList<Actor> actorArrayList = new ArrayList<>();
        jsonArray = gson.fromJson(str, JsonArray.class);
        for (int i = 0; i < jsonArray.size(); i ++) {
            actorArrayList.add(createActorFromJson(jsonArray.get(i).getAsJsonObject()));
        }
        return actorArrayList;
    }

    public static Actor createActorFromJson(JsonObject actorInfo) {
        Actor actor = new Actor(0, "", "", "");
        if (actorInfo.getAsJsonObject() != null) {
            actor.setActorId( actorInfo.getAsJsonObject().get("id").getAsInt());
            actor.setName(fixStr(actorInfo.getAsJsonObject().get("name").getAsString()));
            actor.setRole(fixStr(actorInfo.getAsJsonObject().get("character").getAsString()));
            if (actorInfo.getAsJsonObject().get("profile_path") != null) {
               actor.setProfilePath(fixStr((actorInfo.getAsJsonObject().get("profile_path").toString())));
            }
        }
        return actor;
    }
}
//TODO add to selected movie it's actors!!!!!!