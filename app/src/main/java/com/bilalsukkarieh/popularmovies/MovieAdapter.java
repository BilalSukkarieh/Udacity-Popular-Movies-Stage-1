package com.bilalsukkarieh.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder>{

    private Context context;
    private ArrayList<HashMap<String, String>> movieData;
    private ItemClickListener mItemClickListener;

    //click interface
    interface ItemClickListener{
        //pass the movie id and the thumnail url to the click to be used in intent
        void onItemClick(String itemId, URL itemImageURL);
    }
    //constructor of class
    public MovieAdapter(Context c, ArrayList<HashMap<String,String>> mData, ItemClickListener listItemClickListener){
        this.context = c;
        this.movieData = mData;
        mItemClickListener = listItemClickListener;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout with the specified corresponding xml
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View v = li.inflate(R.layout.grid_item_layout, parent, false);

        return new MovieHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        //populate the view with the data
        URL itemURL = Utils.getURL(movieData.get(position).get("imageURL"));
        Picasso.with(context).load(itemURL.toString()).into(holder.getMovieThumbnail());
    }

    @Override
    public int getItemCount() {
        //return the items count
        return movieData.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //declare required layout variables
        ImageView movieThumbnail;

        MovieHolder(View itemView) {
            //class constructor and refrence variables
            super(itemView);
            movieThumbnail = itemView.findViewById(R.id.moviethumbnail);
            itemView.setOnClickListener(this);
        }

        public ImageView getMovieThumbnail(){
            //return object of the view elemnts
            return movieThumbnail;
        }


        @Override
        public void onClick(View view) {
            //pass the data onto the click to be used in intent
            String itemMovieId = movieData.get(getAdapterPosition()).get("id");
            URL itemImageURL = Utils.getURL(movieData.get(getAdapterPosition()).get("imageURL"));
            mItemClickListener.onItemClick(itemMovieId, itemImageURL);
        }
    }



}
