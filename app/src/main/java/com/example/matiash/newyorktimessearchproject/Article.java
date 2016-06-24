package com.example.matiash.newyorktimessearchproject;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Article implements Parcelable {
    public String headline;
    public String link;
    public String thumbnail;

    private Article(Parcel in) {
        headline = in.readString();
        link = in.readString();
        thumbnail = in.readString();
    }

    public String getLink() {
        return link;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getHeadline() {

        return headline;
    }

    public Article(JSONObject jsonObject) {
        try {

            //Basically check to see if the headline exists, if so you're doing article search, if not it's top stories.
            //Setting headline and link to approriate formats
            try {
                this.headline = jsonObject.getJSONObject("headline").getString("main");
                this.link = jsonObject.getString("web_url");
                this.thumbnail = "";

                JSONArray multimedia = jsonObject.getJSONArray("multimedia");
                if(multimedia != null) {
                    this.thumbnail = "http://nytimes.com/" + multimedia.getJSONObject(0).getString("url");
                }

            } catch(JSONException e){
                this.headline = jsonObject.getString("title");
                this.link = jsonObject.getString("url");
                this.thumbnail = "";

                JSONArray multimedia = jsonObject.getJSONArray("multimedia");
                if(multimedia != null) {
                    this.thumbnail = multimedia.getJSONObject(2).getString("url"); //Getting index 2 return Normal Size Thumbnail
                }
            }



        }catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article>  fromJSONArray(JSONArray jsonArray) {
        ArrayList<Article> articles = new ArrayList<>();
        for(int x =0; x < jsonArray.length();x++) {
            try {
                articles.add(new Article(jsonArray.getJSONObject(x)));
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        return articles;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(headline);
        parcel.writeString(link);
        parcel.writeString(thumbnail);
    }

    public static final Parcelable.Creator<Article> CREATOR
            = new Parcelable.Creator<Article>() {
        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }
        // We just need to copy this and change the type to match our class.
        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
