package com.example.quizpoly.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.quizpoly.DAO.UserDAO;
import com.example.quizpoly.Models.User;
import com.example.quizpoly.R;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerDetailActivity extends AppCompatActivity {

    CircleImageView ivAvatar;
    TextView tvDisplayName, tvNumberOfTimePlayed, tvCorrectRate, tvAverageTime;
    String username;
    UserDAO userDAO;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SS");
    Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        ivAvatar = findViewById(R.id.ivAvatarPD);
        tvDisplayName = findViewById(R.id.tvDisplayNamePD);
        tvAverageTime = findViewById(R.id.tvAverageTime);
        tvNumberOfTimePlayed = findViewById(R.id.tvNumberOfTimePlayed);
        tvCorrectRate = findViewById(R.id.tvCorrectRate);
        btnClose = findViewById(R.id.btnClose);

        userDAO = new UserDAO(this);

        Intent i = getIntent();
        username = i.getStringExtra("username");

        User u = userDAO.checkUserExist(username, "username=?");
        String stringUri = u.getStringUri();

        if(stringUri != null){
            Glide.with(this).load(stringUri).into(ivAvatar);
        }

        tvDisplayName.setText(u.getDisplayname());
        int numberOfTimePlayed = (int) userDAO.numberOfTimePlayed(username);
        tvNumberOfTimePlayed.setText("Tổng số lần chơi: " + numberOfTimePlayed);

        int totalPlayTime = userDAO.totalTimePlayed(username);
        int averageTime = totalPlayTime / numberOfTimePlayed;
        String formatted = sdf.format(averageTime);
        tvAverageTime.setText("Thời gian trung bình mỗi lần chơi: " + formatted);
        String rate = String.format("%.2f", userDAO.correctRate(username)*100);
        tvCorrectRate.setText("Tỷ lệ trả lời đúng: " + rate + "%");

        btnClose.setOnClickListener(view -> finish());
    }
}