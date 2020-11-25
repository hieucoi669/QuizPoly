package com.example.quizpoly.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizpoly.DAO.QuizDAO;
import com.example.quizpoly.Models.Quiz;
import com.example.quizpoly.R;
import com.example.quizpoly.RecAdapter;
import com.example.quizpoly.Sound.App;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Quiz> list;
    QuizDAO quizDAO;
    TextInputLayout edSearch;
    RecAdapter recAdapter;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        ivBack = findViewById(R.id.ivBack);
        edSearch = findViewById(R.id.edSearch);
        recyclerView = findViewById(R.id.recycleView);
        quizDAO = new QuizDAO(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        list = quizDAO.getQuizList();
        recAdapter = new RecAdapter(list, this);
        recyclerView.setAdapter(recAdapter);

        edSearch.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String displayName = edSearch.getEditText().getText().toString();

                List<Quiz> searchList = quizDAO.filterQuiz(displayName);

                recAdapter = new RecAdapter(list, LeaderBoardActivity.this, searchList);
                recyclerView.setAdapter(recAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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