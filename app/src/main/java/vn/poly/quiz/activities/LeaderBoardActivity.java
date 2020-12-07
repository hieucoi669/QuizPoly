package vn.poly.quiz.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.poly.quiz.LoadingDialog;
import vn.poly.quiz.R;
import vn.poly.quiz.RecAdapter;
import vn.poly.quiz.models.Quiz;
import vn.poly.quiz.sound.App;
import vn.poly.quiz.sound.MusicManager;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LeaderBoardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Quiz> list, listSearch;
    TextInputLayout edSearch;
    ImageView ivBack;
    DatabaseReference rootRef;
    Quiz quiz;
    RecAdapter recAdapter;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        ivBack = findViewById(R.id.ivBack);
        edSearch = findViewById(R.id.edSearch);
        recyclerView = findViewById(R.id.recycleView);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.showLoadingDialog();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        rootRef = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();
        listSearch = new ArrayList<>();
        Query query = rootRef.child("Quiz");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        quiz = data.getValue(Quiz.class);
                        list.add(quiz);
                    }
                    sortDescending(list);
                }
                recAdapter = new RecAdapter(list, LeaderBoardActivity.this);
                recyclerView.setAdapter(recAdapter);
                loadingDialog.hideLoadingDialog();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });

        Objects.requireNonNull(edSearch.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String displayName = edSearch.getEditText().getText().toString();
                listSearch.clear();
                for(Quiz q : list){
                    if(q.getDisplayName().toLowerCase().contains(displayName.toLowerCase())){
                        listSearch.add(q);
                    }
                }
                recAdapter = new RecAdapter(list,
                        LeaderBoardActivity.this, listSearch);
                recyclerView.setAdapter(recAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ivBack.setOnClickListener(view -> {
            App.getMusicPlayer().play(this, MusicManager.buttonClick, mediaPlayer -> {

            });
            finish();
        });
    }

    private void sortDescending(List<Quiz> list){
        Collections.sort(list, (q1, q2) -> {
            if (q1.getNumCorrectAnswer() < q2.getNumCorrectAnswer()) {
                return 1;
            } else if(q1.getNumCorrectAnswer() > q2.getNumCorrectAnswer()) {
                return -1;
            } else{
                return Integer.compare(q1.getTime(), q2.getTime());
            }
        });
    }
}