package com.boukharist.musicdl;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.piasy.rxandroidaudio.StreamAudioPlayer;
import com.hugomatilla.audioplayerview.AudioPlayerView;

import java.io.File;
import java.io.FileOutputStream;

public class ScrollingActivity extends AppCompatActivity {

    static final int BUFFER_SIZE = 2048;
    byte[] mBuffer;
    private StreamAudioPlayer mStreamAudioPlayer;
    private FileOutputStream mFileOutputStream;
    private File mOutputFile;
    String AudioURL;
    AudioPlayerView audioPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStreamAudioPlayer = StreamAudioPlayer.getInstance();
        mBuffer = new byte[BUFFER_SIZE];
      //  AudioURL = "http://www.stephaniequinn.com/Music/Allegro%20from%20Duet%20in%20C%20Major.mp3";
        AudioURL = "http://dlo8.yt-mp3.com/download/mv-zion-t-eat-%EA%BA%BC%EB%82%B4-%EB%A8%B9%EC%96%B4%EC%9A%94.mp3?id=Ibb5RhoKfzE&title=%5BMV%5D+Zion.T+_+Eat%28%EA%BA%BC%EB%82%B4+%EB%A8%B9%EC%96%B4%EC%9A%94%29&t=1476121105&extra=a&ip=37.164.176.142&h=44302853af33a1a780ffa4ab33431d6ce6684944";

        audioPlayerView = (AudioPlayerView) findViewById(R.id.player);
        Typeface iconFont = Typeface.createFromAsset(getAssets(), "audio-player-view-font-custom.ttf");
        audioPlayerView.setTypeface(iconFont);
        audioPlayerView.withUrl(AudioURL);

        audioPlayerView.setOnAudioPlayerViewListener(new AudioPlayerView.OnAudioPlayerViewListener() {
            @Override
            public void onAudioPreparing() {
                // spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAudioReady() {
                //  spinner.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAudioFinished() {

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
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

    public void sayHello(View v){
        TextView textView = (TextView) findViewById(R.id.textView);
        EditText editText = (EditText) findViewById(R.id.editText);
        textView.setText("Hello, " + editText.getText().toString() + "!");
    }


    @Override
    protected void onDestroy() {
        audioPlayerView.destroy();
        super.onDestroy();
    }
}
