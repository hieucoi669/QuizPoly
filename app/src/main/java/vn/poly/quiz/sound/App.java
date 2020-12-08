package vn.poly.quiz.sound;

import android.app.Activity;
import android.app.Application;


public class App extends Application {
    private static MusicManager musicPlayer;
    private Activity mCurrentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayer = new MusicManager(this);
    }

    public static MusicManager getMusicPlayer(){
        return musicPlayer;
    }

    public Activity getCurrentActivity () {
        return mCurrentActivity ;
    }
    public void setCurrentActivity (Activity mCurrentActivity) {
        this . mCurrentActivity = mCurrentActivity ;
    }
}
