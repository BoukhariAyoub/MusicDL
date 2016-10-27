package com.boukharist.musicdl;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.hugomatilla.audioplayerview.AudioPlayerView;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrateur on 27-Oct-16.
 */

public class Utils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    public static long downloadData(Context context, String url, String title) {
        if (isStoragePermissionGranted(context)) {
            // Create request for android download manager
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.addRequestHeader("Content-Type", "audio/mpeg");
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Music");

            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }

            Log.d("natija", success + " for " + folder.getPath());
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle("Downloading "+ title)
                    .setDescription("Downloading via Music DL")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Music/" + title + ".mp3");
            return downloadManager.enqueue(request);
        }
        return 0;
    }

    private static boolean isStoragePermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("natija", "Permission is granted");
                return true;
            } else {

                Log.v("natija", "Permission is revoked");
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("natija", "Permission is granted");
            return true;
        }
    }

    public static void destroyPlayer(MyAudioPlayer audioPlayerView) {
       if (audioPlayerView != null) {
           audioPlayerView.stop();
           audioPlayerView.destroy();

      /*       try {
                audioPlayerView.toggleAudio();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                audioPlayerView.destroy();
                Log.i("TAG", "destroyed");
            }*/
        }
    }

    public static void destroyPlayer(AudioPlayerView[] list, int position) {
        AudioPlayerView audioPlayerView = null;
        if (position >= 0 && position < list.length) {
            audioPlayerView = list[position];
        }
        if (audioPlayerView != null) {
            try {
                audioPlayerView.toggleAudio();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                audioPlayerView.destroy();
                Log.i("TAG", "destroyed");
            }
        }
    }
}
