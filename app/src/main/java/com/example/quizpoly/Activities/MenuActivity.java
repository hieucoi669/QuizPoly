package com.example.quizpoly.Activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.bumptech.glide.Glide;
import com.example.quizpoly.R;
import com.example.quizpoly.Sound.App;
import com.example.quizpoly.Sound.MusicManager;
import com.example.quizpoly.Models.User;
import com.example.quizpoly.DAO.UserDAO;
import com.google.android.material.textfield.TextInputLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener, LifecycleObserver {

    UserDAO userDAO;
    User u;
    String username,displayname,stringUri;
    CircleImageView ivAvatar;
    TextView tenhienthi,tendangnhap;
    CardView cvPlay, cvLeaderBoard, cvSettings, cvStatistics;
    Button btnConfirm, btnCancel;
    TextInputLayout tilPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userDAO = new UserDAO(this);
        u = userDAO.checkUserExist(username,"username=?");
        displayname = u.getDisplayname();
        stringUri = u.getStringUri();
        ivAvatar = findViewById(R.id.ivAvatar);
        tenhienthi = findViewById(R.id.tvTenHienThi);
        tendangnhap = findViewById(R.id.tvTenDangNhap);
        cvPlay = findViewById(R.id.cardViewPlay);
        cvLeaderBoard = findViewById(R.id.cardViewLeaderBoard);
        cvSettings = findViewById(R.id.cardViewSettings);
        cvStatistics = findViewById(R.id.cardViewStatistics);

        if(stringUri != null)
        {
            Glide.with(this)
                    .load(stringUri)
                    .placeholder(R.drawable.avatar_holder)
                    .into(ivAvatar);
        }

        tenhienthi.setText(displayname);
        tendangnhap.setText("@"+username);

        App.getMusicPlayer().playBgMusic(MusicManager.bgMusicList);

        cvPlay.setOnClickListener(this);
        cvLeaderBoard.setOnClickListener(this);
        cvSettings.setOnClickListener(this);
        cvStatistics.setOnClickListener(this);
    }

    public void logout(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void leaderBoard () {
        App.getMusicPlayer().play(MusicManager.buttonclick, mediaPlayer -> {

        });
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
    }

    public void statistics () {
        App.getMusicPlayer().play(MusicManager.buttonclick, mediaPlayer -> {

        });
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void setting () {
        App.getMusicPlayer().play(MusicManager.buttonclick, mediaPlayer -> {

        });
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void play() {
        App.getMusicPlayer().play(MusicManager.buttonclick, mediaPlayer -> {

        });
        Intent intent = new Intent(this, QuizsActivity.class);
        intent.putExtra("username",username);
        startActivity(intent);
    }

    public void edit(View view) {
        App.getMusicPlayer().play(MusicManager.buttonclick, mediaPlayer -> {

        });

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MenuActivity.this, android.R.style.Theme_Material_Dialog_Alert);

        LayoutInflater layoutInflater = getLayoutInflater();
        View viewD = layoutInflater.inflate(R.layout.dialog_password, null);

        btnConfirm = viewD.findViewById(R.id.btnConfirm);
        btnCancel = viewD.findViewById(R.id.btnCancel);
        tilPassword = viewD.findViewById(R.id.edPasswordDialog);

        alertDialog.setView(viewD);

        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPassDialog()){
                    dialog.dismiss();
                    Intent intent = new Intent(MenuActivity.this, EditActivity.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void toResult(View v){
        Intent i = new Intent(this, ResultActivity.class);
        Bundle b = new Bundle();
        b.putString("username", "backdoor");
        b.putInt("numCorrect", 2);
        b.putInt("time", 23000);
        i.putExtras(b);
        startActivity(i);
    }

    private void shrink(View cardView){
        cardView.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(cardView,
                            "scaleX", 0.9f);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(cardView,
                            "scaleY", 0.9f);
                    scaleDownX.setDuration(100);
                    scaleDownY.setDuration(100);

                    AnimatorSet scaleDown = new AnimatorSet();
                    scaleDown.play(scaleDownX).with(scaleDownY);

                    scaleDown.start();

                    break;

                case MotionEvent.ACTION_UP:
                    ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(
                            cardView, "scaleX", 1f);
                    ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(
                            cardView, "scaleY", 1f);
                    scaleDownX2.setDuration(100);
                    scaleDownY2.setDuration(100);

                    AnimatorSet scaleUp = new AnimatorSet();
                    scaleUp.play(scaleDownX2).with(scaleDownY2);

                    scaleUp.start();

                    break;
            }
            return false;
        });
    }

    private boolean checkPassDialog(){
        tilPassword.setError(null);
        String pass = u.getPassword();
        try{
            String pass_input = tilPassword.getEditText().getText().toString();
            if(pass_input.length() == 0){
                tilPassword.setError("Password không được để trống!");
                return false;
            }
            if(pass.equals(pass_input)){
                return true;
            }else{
                tilPassword.setError("Password không chính xác!");
                return false;
            }
        }catch(Exception e){
            tilPassword.setError("Password không hợp lệ!");
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.cardViewPlay){
            shrink(cvPlay);
            play();
        }else if(id == R.id.cardViewLeaderBoard){
            shrink(cvLeaderBoard);
            leaderBoard();
        }else if(id == R.id.cardViewSettings){
            shrink(cvSettings);
            setting();
        }else if(id == R.id.cardViewStatistics){
            shrink(cvStatistics);
            statistics();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        App.getMusicPlayer().pauseBgMusic();
        //App in background
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        App.getMusicPlayer().resumeBgMusic();
        // App in foreground
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        App.getMusicPlayer().pauseBgMusic();
    }
}