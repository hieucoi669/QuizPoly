package com.example.quizpoly.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.example.quizpoly.R;
import com.example.quizpoly.Sound.App;
import com.example.quizpoly.Sound.MusicManager;

public class SettingActivity extends AppCompatActivity {
    SwitchCompat switchBG, switchSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        switchBG = findViewById(R.id.switchBackGroundMusic);
        switchSound = findViewById(R.id.switchSound);
        setBgTogMusic(App.getMusicPlayer().getStateMusic());
        setBgTogSound(App.getMusicPlayer().getStateSound());

        switchBG.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    App.getMusicPlayer().setStateMusic(true);
                    App.getMusicPlayer().playBgMusic(MusicManager.bgMusicList);
                    App.getMusicPlayer().setting(switchBG.isChecked(), switchSound.isChecked());
                }
                else
                {
                    App.getMusicPlayer().stopBgMusic();
                    App.getMusicPlayer().setting(switchBG.isChecked(), switchSound.isChecked());
                }
            }
        });
        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                App.getMusicPlayer().setting(switchBG.isChecked(), switchSound.isChecked());
            }
        });
    }

    public void setBgTogMusic(boolean state){
        if(state){
            switchBG.setChecked(true);
        }else{
            switchBG.setChecked(false);
        }
    }

    public void setBgTogSound(boolean state){
        if(state){
            switchSound.setChecked(true);
        }else{
            switchSound.setChecked(false);
        }
    }
    public void settingBack(View view) {
        App.getMusicPlayer().setting(switchBG.isChecked(), switchSound.isChecked());
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.getMusicPlayer().pauseBgMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.getMusicPlayer().resumeBgMusic();
    }

}