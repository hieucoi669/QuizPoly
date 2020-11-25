package com.example.quizpoly.Sound;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import com.example.quizpoly.R;


public class MusicManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private static final String STATE_MUSIC = "MUSIC";
    private static final String STATE_SOUND = "SOUND";
    public static final int PLAYER_IDLE = -1;
    public static final int PLAYER_PLAY = 1;
    public static final int PLAYER_PAUSE = 2;
    private int stateS, stateBg;

    public static final int bgMusicList = R.raw.bgmusic1;
    public static final int buttonclick = R.raw.buttonsoundfinal;

    private MediaPlayer mediaPlayer;
    private MediaPlayer bgMusic;
    private SharedPreferences preferences;
    private Context c;
    private boolean stateMusic;
    private boolean stateSound;

    public MusicManager(Context context) {
        this.c = context;
        getPreferenceSetting();
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


    private void getPreferenceSetting() {
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        boolean stateMusic = preferences.getBoolean(STATE_MUSIC, true);
        boolean stateSound = preferences.getBoolean(STATE_SOUND, true);

        setStateMusic(stateMusic);
        setStateSound(stateSound);
    }

    public void setting(boolean stateMusic, boolean stateSound) {

        setStateMusic(stateMusic);
        setStateSound(stateSound);

        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(STATE_MUSIC, stateMusic);
        editor.putBoolean(STATE_SOUND, stateSound);
        editor.apply();
    }

    public void play(int idSound, MediaPlayer.OnCompletionListener completionListener) {
        if (!getStateSound()) {
            completionListener.onCompletion(null);
            return;
        }
        stopSound();
        stateS = PLAYER_IDLE;
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(c, idSound);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(completionListener);
        if (completionListener == null) {
            mediaPlayer.setOnCompletionListener(this);
        }
        mediaPlayer.setOnPreparedListener(this);
    }

    public void playBgMusic(int idSound) {
        if (!getStateMusic()) {
            return;
        }
        stopBgMusic();
        stateBg = PLAYER_IDLE;
        bgMusic = new MediaPlayer();
        bgMusic = MediaPlayer.create(c, idSound);
        bgMusic.setAudioStreamType(AudioManager.STREAM_MUSIC);
        bgMusic.setLooping(true);
        bgMusic.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                if (stateBg == PLAYER_IDLE) {
                    mp.start();
                    stateBg = PLAYER_PLAY;
                }
            }
        });

    }

    public void pauseBgMusic() {
        if (stateBg == PLAYER_PLAY) {
            bgMusic.pause();
            stateBg = PLAYER_PAUSE;
        }
    }

    public void pauseSound() {
        if (stateS == PLAYER_PLAY) {
            mediaPlayer.pause();
            stateS = PLAYER_PAUSE;
        }
    }

    public void resumeBgMusic() {
        if (stateBg == PLAYER_PAUSE && stateS != PLAYER_PLAY) {
            bgMusic.start();
            stateBg = PLAYER_PLAY;
        }
    }

    public void resumeSound() {
        if (stateS == PLAYER_PAUSE) {
            mediaPlayer.start();
            stateS = PLAYER_PLAY;
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
        if (stateS == PLAYER_PLAY || stateS == PLAYER_PAUSE) {
            mediaPlayer.release();
            mediaPlayer = null;
            stateS = PLAYER_IDLE;
        }
        pauseBgMusic();
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
