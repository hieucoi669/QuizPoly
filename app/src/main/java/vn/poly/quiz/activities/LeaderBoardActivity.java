package vn.poly.quiz.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.poly.quiz.R;
import vn.poly.quiz.dao.QuizDAO;
import vn.poly.quiz.models.Quiz;
import vn.poly.quiz.RecAdapter;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

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

        Objects.requireNonNull(edSearch.getEditText()).addTextChangedListener(new TextWatcher() {
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
        ivBack.setOnClickListener(view -> finish());
    }
}