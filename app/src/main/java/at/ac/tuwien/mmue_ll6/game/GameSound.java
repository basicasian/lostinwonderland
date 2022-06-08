package at.ac.tuwien.mmue_ll6.game;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import at.ac.tuwien.mmue_ll6.R;

/**
 * Help class for GameSurfaceView to handle sound assets
 * @author Michelle Lau
 */
public class GameSound implements SoundPool.OnLoadCompleteListener, MediaPlayer.OnCompletionListener {

    protected MediaPlayer mediaPlayer;
    protected SoundPool soundPool;
    protected int jumpSoundID;

    /**
     * load the media player and initializing them with sources
     * @param context to get the sound
     */
    GameSound(Context context) {
        //Init media player with a song. Create audio pool
        mediaPlayer = MediaPlayer.create(context, R.raw.bgmusic);
        mediaPlayer.setLooping(true); // loops when music is played through
        mediaPlayer.start();

        createSoundPool();
        // load sound file from resource and returns it as id that can be played by the sound pool
        jumpSoundID = soundPool.load(context, R.raw.jumpsound, 1);
    }

    /**
     * creates a sound pool based on android version
     */
    private void createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = createNewSoundPool();
        } else {
            soundPool = createLegacySoundPool();
        }
    }

    /**
     * sound pool for android version before lollipop
     */
    private SoundPool createLegacySoundPool() {
        return new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    }

    /**
     * sound pool for android version after lollipop
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private SoundPool createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        return new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(5)
                .build();
    }

    /**
     * play the jump sound
     */
    public void playJumpSound() {
        soundPool.play(jumpSoundID, 0.4f, 0.4f, 1, 0, 1.0f);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //callback when media player has finished playing
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int i, int i1) {
        //callback when sound pool has finished loading
    }
}
