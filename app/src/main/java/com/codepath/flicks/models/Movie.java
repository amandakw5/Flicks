package com.codepath.flicks.models;
/**
 * Created by awestort on 6/21/17.
 */

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;


@Parcel // annotation indicates class is Parcelable
public class Movie {
    public Movie() {}
    // values from API
    String title;
    String overview;
    String posterPath; // only the path
    String backdropPath;
    Double vote_average;
    Integer id;
    String imgUrl;
    String release;

    // initialize from JSON data
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        vote_average = object.getDouble("vote_average");
        id = object.getInt("id");
        release = object.getString("release_date");
    }

    public Double getVote_average() {
        return vote_average;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Integer getId() {
        return id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getRelease() {
        return release;
    }
}
