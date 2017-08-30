package com.bilalsukkarieh.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


public class MovieDetails extends AppCompatActivity {

    //declare class variables
     String movieId;
     URL thumbnailURL;
     String movieTitle;
     String movieRating;
     String movieProductionDate;
     String movieDuration;
     String movieOverview;
     ImageView img_thumbnail;
     TextView tv_title;
     TextView tv_production_date;
     TextView tv_rating;
     TextView tv_duration;
     TextView tv_overview;
     ProgressBar pb_movie;
     ScrollView ln_details;
     final String TAG = "deatailLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //reference required ui elements
        img_thumbnail = findViewById(R.id.img_thumbnail);
        tv_title = findViewById(R.id.tv_movie_title);
        tv_duration = findViewById(R.id.tv_duration);
        tv_rating = findViewById(R.id.tv_rating);
        tv_production_date = findViewById(R.id.tv_release_date);
        tv_overview = findViewById(R.id.tv_overview);
        pb_movie = findViewById(R.id.pbmovie);
        ln_details = findViewById(R.id.moviedetail);

        //check for intent and get the attached data
        Intent detailIntent = getIntent();
        if(detailIntent != null){
            movieId = detailIntent.getStringExtra("movieId");
            thumbnailURL = Utils.getURL(detailIntent.getStringExtra("thumbnailURL"));
            Picasso.with(this).load(thumbnailURL.toString()).into(img_thumbnail);
        }
        //instantiate the asynctask to retrieve movie details
        GetMovieDetails gmd = new GetMovieDetails();
        gmd.execute();
    }

    private class GetMovieDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            //show a progress bar while task is processing
            super.onPreExecute();
            pb_movie.setVisibility(View.VISIBLE);
            ln_details.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... s) {
            //use http handler class to get data from api
            HttpHandler httpHandler = new HttpHandler();
            String url = "http://api.themoviedb.org/3/movie/"+movieId+"?api_key=" + getString(R.string.moviedbapi);
            String jsonStr = httpHandler.makeServiceCall(url);
            //process retrieved data
            if (jsonStr != null) {
                //try getting required data and catch any error
                try {
                    //set the json retrieved to jsonobject and extracrt required data
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    movieTitle = jsonObj.getString("original_title");
                    movieRating = jsonObj.getString("vote_average");
                    movieOverview = jsonObj.getString("overview");
                    movieDuration = jsonObj.getString("runtime");
                    movieProductionDate = jsonObj.getString("release_date");

                } catch (final JSONException e) {
                    //catch any json error and log info
                    Log.d(TAG, e.toString());
                }

            } else {
                //catch retreival related error and log info
                Log.d(TAG, "error retrieving json");
            }
            //return null as it is void
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //populate the ui with data
            tv_title.setText(movieTitle);
            tv_duration.setText(String.format(getString(R.string.duration), movieDuration));
            tv_overview.setText(String.format(getString(R.string.overview), movieOverview));
            tv_production_date.setText(String.format(getString(R.string.releasedate), movieDuration));
            tv_rating.setText(String.format(getString(R.string.averagerating), movieRating));
            //hide progress bar and show details
            pb_movie.setVisibility(View.GONE);
            ln_details.setVisibility(View.VISIBLE);
        }
    }
}
