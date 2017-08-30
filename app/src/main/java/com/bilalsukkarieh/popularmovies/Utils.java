package com.bilalsukkarieh.popularmovies;

import android.net.Uri;

import java.net.URL;

class Utils {

    public static URL getURL(String s){
        //convert a string to url
        Uri itemUri = Uri.parse(s);
        URL itemURL = null;
        try{
            itemURL = new URL(itemUri.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
        return itemURL;
    }
}
