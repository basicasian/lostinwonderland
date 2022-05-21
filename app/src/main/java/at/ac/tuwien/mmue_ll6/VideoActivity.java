package at.ac.tuwien.mmue_ll6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

/**
 * Activity for playing the intro video with skip button
 * @author Renate Zhang
 */
public class VideoActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        //find the video view
        videoView = findViewById(R.id.videoView);
        //add listener to video completion
        //since we implemented the override method of MediaPlayer.OnCompletionListener we can pass
        //this instance as parameter. The VideoView object will call our implementation of
        //onCompletion() when the video has finished playing
        videoView.setOnCompletionListener(this);

        //Locate video source
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
        videoView.setVideoURI(videoUri);

        //start video view when video activity starts
        videoView.start();
    }

    public void skipButtonPressed(View v) {
        skipVideo();
    }

    public void skipVideo() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        skipVideo();
    }
}