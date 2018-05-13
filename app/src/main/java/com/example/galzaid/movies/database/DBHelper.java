package com.example.galzaid.movies.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.galzaid.movies.Movie;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "moviesDB";
    private static final int VERSION = 1;
    private static final String MOVIES_TABLE_NAME = "moviesTable";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MOVIES_TABLE_NAME + "( " +
                Movie.Properties.id + " INTEGER PRIMARY KEY," +
                Movie.Properties.name + " TEXT," +
                Movie.Properties.favorite + " BOOLEAN," +
                Movie.Properties.description + " TEXT," +
                Movie.Properties.movieRating + " DOUBLE," +
                Movie.Properties.actorJsonArrStr + " Text," +
                Movie.Properties.movieUrl + " TEXT," +
                Movie.Properties.movieSecondUrl + " TEXT," +
                Movie.Properties.budget + " DOUBLE," +
                Movie.Properties.revenue + " DOUBLE," +
                Movie.Properties.runtime + " TEXT," +
                Movie.Properties.releaseDate + " TEXT" +
                " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE " + MOVIES_TABLE_NAME);
        onCreate(db);
    }

    public long addNewMovie(Movie m) {
        ContentValues newMovie = m.getContentValues();
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(MOVIES_TABLE_NAME, null, newMovie);
    }

    public boolean deleteMovie(Movie m) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(MOVIES_TABLE_NAME, "id = " + m.getMovieId(), null) > 0;
    }

    public ArrayList<Movie> getAllFavorites() {
        ArrayList<Movie> favorites = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MOVIES_TABLE_NAME, null);
        if (c != null) {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                Movie m = Movie.createFromCursor(c);
                favorites.add(m);
                c.moveToNext();
            }

            c.close();
        }
        for (int i = 0; i < favorites.size(); i++) {
            Log.i("Favorite at " + i, favorites.get(i).getMovieName() + " " + favorites.get(i).getMovieId());
        }
        return favorites;
    }

    public boolean isFavorite(Movie movie) {
        ArrayList<Movie> favorites;
        favorites = getAllFavorites();
        boolean favorite = false;
        for (int i = 0; i < favorites.size(); i++) {
            if (movie.getMovieId() == favorites.get(i).getMovieId())
                favorite = true;
        }
        return favorite;
    }
}