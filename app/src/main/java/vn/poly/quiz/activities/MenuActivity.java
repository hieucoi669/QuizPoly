package vn.poly.quiz.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.bumptech.glide.Glide;

import vn.poly.quiz.R;
import vn.poly.quiz.sound.App;
import vn.poly.quiz.sound.MusicManager;
import vn.poly.quiz.models.User;
import vn.poly.quiz.dao.UserDAO;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnTouchListener, LifecycleObserver {

    UserDAO userDAO;
    User u;
    String username, displayName,stringUri;
    CircleImageView ivAvatar;
    TextView tvDisplayName, tvUsername;
    CardView cvPlay, cvLeaderBoard, cvSettings, cvStatistics;
    Button btnConfirm, btnCancel;
    TextInputLayout tilPassword;
    CardView cvLayoutTop;
    ImageView ivLogout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userDAO = new UserDAO(this);
        u = userDAO.checkUserExist(username,"username=?");
        displayName = u.getDisplayName();
        stringUri = u.getStringUri();
        ivAvatar = findViewById(R.id.ivAvatar);
        tvDisplayName = findViewById(R.id.tvDisplayNameMenu);
        tvUsername = findViewById(R.id.tvUsernameLogin);
        cvPlay = findViewById(R.id.cardViewPlay);
        cvLeaderBoard = findViewById(R.id.cardViewLeaderBoard);
        cvSettings = findViewById(R.id.cardViewSettings);
        cvStatistics = findViewById(R.id.cardViewStatistics);
        cvLayoutTop = findViewById(R.id.cvLayoutTop);
        ivLogout = findViewById(R.id.ivLogout);

        if(stringUri != null)
        {
            Glide.with(this)
                    .load(stringUri)
                    .placeholder(R.drawable.avatar_holder)
                    .into(ivAvatar);
        }

        tvDisplayName.setText(displayName);
        tvUsername.setText(getString(R.string.menu_username, username));

        App.getMusicPlayer().playBgMusic(this, MusicManager.bgMusicList);

        cvPlay.setOnClickListener(this);
        cvLeaderBoard.setOnClickListener(this);
        cvSettings.setOnClickListener(this);
        cvStatistics.setOnClickListener(this);

        cvPlay.setOnTouchListener(this);
        cvLeaderBoard.setOnTouchListener(this);
        cvSettings.setOnTouchListener(this);
        cvStatistics.setOnTouchListener(this);

        cvLayoutTop.setOnClickListener(view -> editUser());
        ivLogout.setOnClickListener(view -> logOut());
    }

    public void logOut() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void leaderBoard () {
        App.getMusicPlayer().play(this, MusicManager.buttonClick, mediaPlayer -> {

        });
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
    }

    public void statistics () {
        App.getMusicPlayer().play(this, MusicManager.buttonClick, mediaPlayer -> {

        });
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void setting () {
        App.getMusicPlayer().play(this, MusicManager.buttonClick, mediaPlayer -> {

        });
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void play() {
        App.getMusicPlayer().play(this, MusicManager.buttonClick, mediaPlayer -> {

        });
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("username",username);
        startActivity(intent);
    }

    public void editUser() {
        App.getMusicPlayer().play(this, MusicManager.buttonClick, mediaPlayer -> {

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

        btnConfirm.setOnClickListener(view2 -> {
            if(checkPassDialog()){
                dialog.dismiss();
                Intent intent = new Intent(MenuActivity.this, EditActivity.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
        btnCancel.setOnClickListener(view1 -> dialog.dismiss());
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

    private void shrink(View cardView, MotionEvent motionEvent){
        switch (motionEvent.getAction()) {
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
            default:
                break;
        }
    }

    private boolean checkPassDialog(){
        tilPassword.setError(null);
        String pass = u.getPassword();
        try{
            String pass_input = Objects.requireNonNull(tilPassword.getEditText()).getText().toString();
            if(pass_input.length() == 0){
                tilPassword.setError(getString(R.string.ed_password_error_null));
                return false;
            }
            if(pass.equals(pass_input)){
                return true;
            }else{
                tilPassword.setError(getString(R.string.ed_password_error_wrong));
                return false;
            }
        }catch(Exception e){
            tilPassword.setError(getString(R.string.ed_password_error));
            return false;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int id = view.getId();
        if(id == R.id.cardViewPlay){
            shrink(cvPlay, motionEvent);
        }else if(id == R.id.cardViewLeaderBoard){
            shrink(cvLeaderBoard, motionEvent);
        }else if(id == R.id.cardViewSettings){
            shrink(cvSettings, motionEvent);
        }else if(id == R.id.cardViewStatistics){
            shrink(cvStatistics, motionEvent);
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.cardViewPlay){
            play();
        }else if(id == R.id.cardViewLeaderBoard){
            leaderBoard();
        }else if(id == R.id.cardViewSettings){
            setting();
        }else if(id == R.id.cardViewStatistics){
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