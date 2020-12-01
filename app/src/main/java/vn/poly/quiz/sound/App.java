package vn.poly.quiz.sound;

import android.app.Application;

public class App extends Application {
    private static MusicManager musicPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayer = new MusicManager(this);
    }

    public static MusicManager getMusicPlayer(){
        return musicPlayer;
    }
}
