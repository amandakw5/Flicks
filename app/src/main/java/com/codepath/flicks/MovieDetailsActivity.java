package com.codepath.flicks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.flicks.models.Movie;
import com.codepath.flicks.models.MovieTrailerActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.flicks.R.drawable.flicks_backdrop_placeholder;

public class MovieDetailsActivity extends AppCompatActivity {
    // the movie to display
    Movie movie;
    Context context;
    //the base url for the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //the parameter name for the API key
    public final static String API_KEY_PARAM = "api_key";
    // tag for log in from the activity
    public final static String TAG = "MovieListActivity";

    //instance fields
    AsyncHttpClient client;
    String imageUrl;
    String video;

    // the view objects
    @BindView(R.id.tvTitle)TextView tvTitle;
    @BindView(R.id.tvOverview) TextView tvOverview;
    @BindView(R.id.rbVoteAverage) RatingBar rbVoteAverage;
    @BindView(R.id.back) ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        context = this;

        ButterKnife.bind(this);

        int placeholderId = flicks_backdrop_placeholder;
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        // load the backdrop image
        imageUrl = movie.getImgUrl();
        // load image using glide
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(back);
        // unwrap the movie passed in via intent, using its simple name as a key

        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVote_average().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                getTrailer();
            }
        });
    }
    // get the list of currently playing movies from the API
    private void getTrailer() {
        client = new AsyncHttpClient();
        // create the url
        String videoId = API_BASE_URL + "/movie/" + movie.getId() + "/videos";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // API key, always required
        // execute a GET request expecting a JSON object response
        client.get(videoId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // load the results into movies list
                try {
                    JSONArray results = response.getJSONArray("results");
                    video = results.getJSONObject(0).getString("key");
                    Log.i(TAG, String.format("Loaded %s videos", results.length()));
                    Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                    intent.putExtra("video", video);
                    MovieDetailsActivity.this.startActivity(intent);
                } catch (JSONException e) {
                    logError("Failed to parse videos", e, true);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data", throwable, true);
            }
        });
    }
    // handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log the error
        Log.e(TAG, message, error);
        // alert the user to avoid silent errors
        if (alertUser) {
            // show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
