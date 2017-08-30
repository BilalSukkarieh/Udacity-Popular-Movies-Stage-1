package com.bilalsukkarieh.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener{

    //declaring class variables
    final String TAG = "mainactLog";
    ArrayList<HashMap<String,String>> movieData;
    RecyclerView rv_movie_grid;
    final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    final String IMAGE_SIZE_PARAM = "w500";
    URL imageURL;
    String movieSort = "popular";
    ProgressBar pb;
    GetMovies getMovies;
    HashMap<String,String> hashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //referencing required ui elements
        rv_movie_grid = findViewById(R.id.rv_movie_grid);
        pb = findViewById(R.id.pb_load_images);

        //setting activity label to most popular as it is default sort
        getSupportActionBar().setTitle(getString(R.string.mostpopular));

        movieData = new ArrayList<>();

        //creating asynctask instance to retrieve moviews according to sort default is most popular
        getMovies = new GetMovies();
        getMovies.execute(movieSort);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the sort menu to allow user to change sort
        getMenuInflater().inflate(R.menu.sortmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_sort_popular){
            // change sort to most popular if user chose so
            movieSort = "popular";
            //cancel the running task and instantiate new instance with the new sort
            getMovies.cancel(true);
            getMovies = null;
            getMovies = new GetMovies();
            getMovies.execute(movieSort);
            //change the label of activity to mach the sort chosen by user
            getSupportActionBar().setTitle(getString(R.string.mostpopular));
        }
        if(item.getItemId() == R.id.action_sort_top){
            // change sort to most popular if user chose so
            movieSort = "top_rated";
            //cancel the running task and instantiate new instance with the new sort
            getMovies.cancel(true);
            getMovies = null;
            getMovies = new GetMovies();
            getMovies.execute(movieSort);
            //change the label of activity to mach the sort chosen by user
            getSupportActionBar().setTitle(getString(R.string.toprated));
        }
        return true;
    }

    //asynctask with a string argument to be able to change the sort
    private class GetMovies extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            //display a progress bar and hide the details view while task is processing
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            rv_movie_grid.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... s) {
            //use the http handler class to retriece data from api as json
            HttpHandler httpHandler = new HttpHandler();
            String url = "http://api.themoviedb.org/3/movie/"+s[0]+"?api_key=" + getString(R.string.moviedbapi);
            String jsonStr = httpHandler.makeServiceCall(url);
            //clear the movie data list to avoid duplicate data when changing sort or when new query is made
            movieData.clear();
            if (jsonStr != null) {
                //try retrieving json data and catch any error
                try {
                    //set the retreived data to a json object to handle it
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //results are stored in json array
                    JSONArray movies = jsonObj.getJSONArray("results");
                    //loop the json array to get data of each movie
                    for (int i = 0; i < movies.length(); i++) {
                        JSONObject movie = movies.getJSONObject(i);
                        //get the required data id is retrieved to be used for movie details
                        String posterPath = movie.getString("poster_path");
                        String movieId = String.valueOf(movie.getInt("id"));

                        //build uri of the thumbnail
                        Uri imageUri = Uri.parse(BASE_IMAGE_URL).buildUpon()
                                .appendPath(IMAGE_SIZE_PARAM)
                                .appendEncodedPath(posterPath)
                                .build();
                        //convert the uri to url
                        try{
                            imageURL = new URL(imageUri.toString());

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.d(TAG, "" + imageURL);

                        //hash map is used to pass the thumbnail url and movie id
                        hashMap = new HashMap<>();

                        hashMap.put("id", movieId);
                        hashMap.put("imageURL", imageURL.toString());

                        //retrieved data is stored in list
                        movieData.add(hashMap);
                    }
                    Log.d(TAG, "" + hashMap);
                } catch (final JSONException e) {
                    //catch the json parsing error and get the info in log
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }

            } else {
                //catch the error of no data is retrieved from api
                Log.e(TAG, "Couldn't get json from server.");

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //populate the data on completion of task
            MovieAdapter adapter = new MovieAdapter(MainActivity.this, movieData, MainActivity.this);
            RecyclerView.LayoutManager lm = new GridLayoutManager(MainActivity.this, 2, LinearLayoutManager.VERTICAL, false);
            rv_movie_grid.setLayoutManager(lm);
            rv_movie_grid.setHasFixedSize(true);
            rv_movie_grid.setAdapter(adapter);
            //hide the progress bar and show the thumbnails
            pb.setVisibility(View.GONE);
            rv_movie_grid.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(String movieId, URL itemImageURL) {
        //on thumbnail or item is clicked start movie detail intent with
        //the thumbnail url and movie id as extras for retrieval of selected movie details
        Intent movieDetailIntent = new Intent(MainActivity.this, MovieDetails.class);
        movieDetailIntent.putExtra("movieId", movieId);
        movieDetailIntent.putExtra("thumbnailURL", itemImageURL.toString());
        startActivity(movieDetailIntent);
    }


}

