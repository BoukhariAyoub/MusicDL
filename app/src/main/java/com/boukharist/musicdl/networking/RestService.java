
package com.boukharist.musicdl.networking;

import com.boukharist.musicdl.model.Item;
import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrateur on 14-Feb-16.
 */
public interface RestService {

    String FETCH_URL = "fetch";

    // https://www.googleapis.com/youtube/v3/search?part=snippet&q=eminem&type=video&key=<key>


    String api = "advanced";
    String format = "JSON";

    String videoDemo = "https://www.youtube.com/watch?v=eVdjb3AtKpM";

    @GET(FETCH_URL)
    Call<Item> getItem(@Query("v") String video);

    @GET("youtube/v3/search")
    Call<JsonElement> search(@Query("part") String part, @Query("q") String query, @Query("type") String type, @Query("key") String key);

   /* @GET("download/get/")
    @Streaming
    Call<ProgressResponseBody> download(@Query("i") String i);
*/
}
