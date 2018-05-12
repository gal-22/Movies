package com.example.galzaid.movies;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class Movie implements Serializable {
    private static String baseMovieUrl = "http://image.tmdb.org/t/p/w500";
    private String movieUrl;
    private String movieSecondUrl;
    private String movieName;
    private int movieId;
    private String movieDescription;
    private double movieRating;
    private String releaseDate;
    private boolean favorite;
    private String runtime;
    private int budget;
    private int revenue;


    public Movie(String movieUrl, String movieName, String secondImageUrl, int movieId, String movieDescription, double movieRating, String releaseDate, boolean favorite, String runtime , int budget , int revenue) {
        this.movieUrl = movieUrl;
        this.movieName = movieName;
        this.movieId = movieId;
        this.movieDescription = movieDescription;
        this.movieRating = movieRating;
        this.movieSecondUrl = secondImageUrl;
        this.releaseDate = releaseDate;
        this.favorite = favorite;
        this.runtime = runtime;
        this.budget = budget;
        this.revenue = revenue;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMovieSecondUrl() {
        return movieSecondUrl;
    }

    public void setMovieSecondUrl(String movieSecondUrl) {
        this.movieSecondUrl = movieSecondUrl;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public Movie() {

    }


    public static String getBaseMovieUrl() {
        return baseMovieUrl;
    }

    public static void setBaseMovieUrl(String baseMovieUrl) {
        Movie.baseMovieUrl = baseMovieUrl;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public void setMovieDescription(String movieDescription) {
        this.movieDescription = movieDescription;
    }

    public double getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(double movieRating) {
        this.movieRating = movieRating;
    }


    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public static Movie fromJson(JsonObject movieInfo, String extraUrl) {
        Movie movie = new Movie();
        movie.movieName = fixStr(movieInfo.getAsJsonObject().get("title").toString());
        movie.movieRating = movieInfo.getAsJsonObject().get("vote_average").getAsDouble();
        movie.movieUrl = fixStr(movieInfo.getAsJsonObject().get("poster_path").toString());
        movie.movieDescription = fixStr(movieInfo.getAsJsonObject().get("overview").toString());
        movie.movieId = movieInfo.getAsJsonObject().get("id").getAsInt();
        movie.releaseDate = fixStr(movieInfo.getAsJsonObject().get("release_date").toString());
        movie.runtime = fixStr(movieInfo.getAsJsonObject().get("runtime").toString());
        movie.budget = movieInfo.getAsJsonObject().get("budget").getAsInt();
        movie.revenue = movieInfo.getAsJsonObject().get("revenue").getAsInt();
        movie.movieSecondUrl = extraUrl;
        return movie;
    }

    public static Movie fromJsonSearched(JsonObject movieInfo, String extraUrl) {
        Movie movie = new Movie();
        Log.i("fafa" , "kaka" +movieInfo.toString());
        movie.movieName = fixStr(movieInfo.getAsJsonObject().get("title").toString());
        movie.movieRating = movieInfo.getAsJsonObject().get("vote_average").getAsDouble();
        movie.movieUrl = fixStr(movieInfo.getAsJsonObject().get("poster_path").toString());
        movie.movieDescription = fixStr(movieInfo.getAsJsonObject().get("overview").toString());
        movie.movieId = movieInfo.getAsJsonObject().get("id").getAsInt();
        movie.releaseDate = fixStr(movieInfo.getAsJsonObject().get("release_date").toString());
        movie.runtime = fixStr(movieInfo.getAsJsonObject().get("runtime").toString());
        movie.budget = movieInfo.getAsJsonObject().get("budget").getAsInt();
        movie.revenue = movieInfo.getAsJsonObject().get("revenue").getAsInt();
        movie.movieSecondUrl = extraUrl;
        return movie;
    }


    private static String fixStr(String str) {
        if (str.charAt(0) == '"')
            str = str.substring(1, str.length() - 1);
        return str;
    }


    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    // database functions
    public static class Properties {
        public static String id = "id";
        public static String name = "name";
        public static String favorite = "favorite";
        public static String description = "description";
        public static String movieUrl = "movieUrl";
        public static String movieSecondUrl = "movieSecondUrl";
        public static String releaseDate = "releaseDate";
        public static String movieRating = "movieRating";
        public static String runtime = "runtime";
        public static String revenue = "revenue";
        public static String budget = "budget";
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Properties.id, movieId);
        contentValues.put(Properties.favorite, favorite);
        contentValues.put(Properties.name, movieName);
        contentValues.put(Properties.movieUrl, movieUrl);
        contentValues.put(Properties.movieSecondUrl, movieSecondUrl);
        contentValues.put(Properties.releaseDate, releaseDate);
        contentValues.put(Properties.movieRating, movieRating);
        contentValues.put(Properties.description, movieDescription);
        contentValues.put(Properties.runtime, runtime);
        contentValues.put(Properties.budget, budget);
        contentValues.put(Properties.revenue, revenue);
        return contentValues;
    }

    public static Movie createFromCursor(Cursor c) {
        Movie m = new Movie();
        m.movieId = c.getInt(c.getColumnIndex(Properties.id));
        m.favorite = c.getInt(c.getColumnIndex(Properties.favorite)) > 0;
        m.movieName = c.getString(c.getColumnIndex(Properties.name));
        m.movieUrl = c.getString(c.getColumnIndex(Properties.movieUrl));
        m.movieSecondUrl = c.getString(c.getColumnIndex(Properties.movieSecondUrl));
        m.releaseDate = c.getString(c.getColumnIndex(Properties.releaseDate));
        m.movieRating = c.getDouble(c.getColumnIndex(Properties.movieRating));
        m.movieDescription = c.getString(c.getColumnIndex(Properties.description));
        m.runtime = c.getString(c.getColumnIndex(Properties.runtime));
        m.revenue = c.getInt(c.getColumnIndex(Properties.revenue));
        m.budget = c.getInt(c.getColumnIndex(Properties.budget));
        return m;
    }



}
