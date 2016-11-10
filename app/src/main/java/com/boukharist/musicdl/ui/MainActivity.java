package com.boukharist.musicdl.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.boukharist.musicdl.R;
import com.boukharist.musicdl.utils.Utils;
import com.boukharist.musicdl.adapter.RecyclerViewAdapter;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String apiKey = "AIzaSyDlveOFgxThr2RKRXizUDJ2E1Ab5VgcEGY";
    RecyclerView mRecyclerView;
    RecyclerViewAdapter mRecyclerViewAdapter;
    private View loadingView;
    private View emptyView;
    private View errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_title);
        setSupportActionBar(toolbar);




        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));


        loadingView = getLayoutInflater().inflate(R.layout.view_loading, mRecyclerView, false);
        emptyView = getLayoutInflater().inflate(R.layout.view_empty, mRecyclerView, false);
        errorView = getLayoutInflater().inflate(R.layout.view_error, mRecyclerView, false);


        mRecyclerViewAdapter = new RecyclerViewAdapter(this, loadingView, emptyView, errorView);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.setState(RecyclerViewAdapter.STATE_EMPTY);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(listener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class AsyncCallWS extends AsyncTask<String, Void, List<SearchResult>> {
        String TAG = "natija";

        @Override
        protected List<SearchResult> doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            if (params != null && params.length > 0) {
                return loadVideos(params[0]);
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<SearchResult> result) {
            Log.i(TAG, "onPostExecute");
            //  recursiveRetroCall(result, 0);
            if (result == null) {
                result = new ArrayList<>();
            }
            if (result.size() > 0) {
                mRecyclerViewAdapter.setState(RecyclerViewAdapter.STATE_NORMAL);
                for (SearchResult item : result) {
                    mRecyclerViewAdapter.addItem(item);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            mRecyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, loadingView, emptyView, errorView);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
            mRecyclerViewAdapter.setState(RecyclerViewAdapter.STATE_LOADING);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }
    }


    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if (Utils.isNetworkAvailable(MainActivity.this)) {
                new AsyncCallWS().execute(query);
            } else {
                mRecyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, loadingView, emptyView, errorView);
                mRecyclerView.setAdapter(mRecyclerViewAdapter);
                mRecyclerViewAdapter.setState(RecyclerViewAdapter.STATE_ERROR);
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // newText is text entered by user to SearchView
            return false;
        }
    };

    private List<SearchResult> loadVideos(String queryTerm) {
        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.

            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName(getString(R.string.app_name)).build();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://console.developers.google.com/
            search.setKey(apiKey);
            search.setQ(queryTerm);

            // Restrict  the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            //  search.setFields("items(id/kind,id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url,snippet/thumbnails/medium/url)");
            search.setMaxResults(5L);//AppConstants.NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                return searchResultList;
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }
}
