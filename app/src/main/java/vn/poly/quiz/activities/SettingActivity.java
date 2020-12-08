package vn.poly.quiz.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.widget.Button;

import vn.poly.quiz.R;
import vn.poly.quiz.sound.App;
import vn.poly.quiz.sound.MusicManager;

public class SettingActivity extends AppCompatActivity {

    SwitchCompat switchBG, switchSound;
    Button btnBack;
    protected App mApp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        switchBG = findViewById(R.id.switchBackGroundMusic);
        switchSound = findViewById(R.id.switchSound);
        btnBack = findViewById(R.id.btnSettingBack);

        mApp = (App) this.getApplicationContext();

        setBgTogMusic(App.getMusicPlayer().getStateMusic());
        setBgTogSound(App.getMusicPlayer().getStateSound());

        switchBG.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
            {
                App.getMusicPlayer().setStateMusic(true);
                App.getMusicPlayer().playBgMusic(this, MusicManager.bgMusicList);
            }
            else
            {
                App.getMusicPlayer().stopBgMusic();
            }
            App.getMusicPlayer().setting(this, switchBG.isChecked(), switchSound.isChecked());
        });

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) ->
                App.getMusicPlayer().setting(this,switchBG.isChecked(), switchSound.isChecked()));

        btnBack.setOnClickListener(view -> settingBack());
    }

    public void setBgTogMusic(boolean state){
        switchBG.setChecked(state);
    }

    public void setBgTogSound(boolean state){
        switchSound.setChecked(state);
    }
    public void settingBack() {
        App.getMusicPlayer().play(this, MusicManager.buttonClick, mediaPlayer -> {

        });
        App.getMusicPlayer().setting(this, switchBG.isChecked(), switchSound.isChecked());
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApp.setCurrentActivity(this);
    }
}