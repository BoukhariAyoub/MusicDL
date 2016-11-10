package com.boukharist.musicdl.networking;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

/**
 * Created by Administrateur on 14-Feb-16.
 */
public class RestApi {

    public static final String ENDPOINT = "http://www.yt-mp3.com/";
  //  public static final String ENDPOINT = "https://www.googleapis.com/";



    public static Retrofit mRetrofit;


    static {
        if (mRetrofit == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.d("RETROFIT", message);
                }
            });

            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(httpClient)
                    .build();
        }
    }


    public static <T> T get(Class<T> tClass) {
        return mRetrofit.create(tClass);
    }

}
