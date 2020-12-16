package vn.poly.quiz.sound;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import vn.poly.quiz.R;

public class MusicManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private static final String STATE_MUSIC = "MUSIC";
    private static final String STATE_SOUND = "SOUND";
    public static final int PLAYER_IDLE = -1;
    public static final int PLAYER_PLAY = 1;
    public static final int PLAYER_PAUSE = 2;
    private int stateS, stateBg;

    public static final int bgMusicList = R.raw.bgmusic;
    public static final int buttonClick = R.raw.buttonsoundfinal;
    public static final int clockTicking = R.raw.ticking_ver2;
    public static final int correctSound = R.raw.correct_answer_sound;
    public static final int wrongSound = R.raw.wrong_answer_sound;

    private MediaPlayer mediaPlayer;
    private MediaPlayer bgMusic;
    private SharedPreferences preferences;
    private boolean stateMusic;
    private boolean stateSound;

    public MusicManager(Context context) {
        getPreferenceSetting(context);
    }

    public void setStateMusic(boolean stateMusic) {
        this.stateMusic = stateMusic;
    }

    public void setStateSound(boolean stateSound) {
        this.stateSound = stateSound;
    }

    public boolean getStateMusic() {
        return stateMusic;
    }

    public boolean getStateSound() {
        return stateSound;
    }

    private void getPreferenceSetting(Context c) {
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        boolean stateMusic = preferences.getBoolean(STATE_MUSIC, true);
        boolean stateSound = preferences.getBoolean(STATE_SOUND, true);

        setStateMusic(stateMusic);
        setStateSound(stateSound);
    }

    public void setting(Context c, boolean stateMusic, boolean stateSound) {

        setStateMusic(stateMusic);
        setStateSound(stateSound);

        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(STATE_MUSIC, stateMusic);
        editor.putBoolean(STATE_SOUND, stateSound);
        editor.apply();
    }

    public void play(Context c, int idSound, MediaPlayer.OnCompletionListener completionListener) {
        if (!getStateSound()) {
            if (completionListener == null) {
                return;
            }
            completionListener.onCompletion(null);
            return;
        }

//        stopSound();
        stateS = PLAYER_IDLE;
//        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(c, idSound);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(completionListener);
        if (completionListener == null) {
            mediaPlayer.setOnCompletionListener(this);
        }
        mediaPlayer.setOnPreparedListener(this);
    }

    public void playBgMusic(Context c, int idSound) {
        if (!getStateMusic()) {
            return;
        }
//        stopBgMusic();
        stateBg = PLAYER_IDLE;
        bgMusic = new MediaPlayer();
        bgMusic = MediaPlayer.create(c, idSound);
        bgMusic.setAudioStreamType(AudioManager.STREAM_MUSIC);
        bgMusic.setLooping(true);
        bgMusic.setOnPreparedListener(mp -> {

            if (stateBg == PLAYER_IDLE) {
                mp.start();
                stateBg = PLAYER_PLAY;
            }
        });

    }

    public void pauseBgMusic() {
        if(bgMusic != null){
            bgMusic.pause();
        }
    }

    public void resumeBgMusic() {
        if(bgMusic != null){
            bgMusic.start();
        }
    }

    public void stopBgMusic() {
        if (stateBg == PLAYER_PLAY || stateBg == PLAYER_PAUSE) {
            bgMusic.release();
            bgMusic = null;
            stateBg = PLAYER_IDLE;
        }
    }

    public void stopSound() {
        mediaPlayer.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.release();
        mediaPlayer = null;
        stateS = PLAYER_IDLE;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (stateS == PLAYER_IDLE) {
            mp.start();
            stateS = PLAYER_PLAY;
        }
    }
}
