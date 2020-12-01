package vn.poly.quiz.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import vn.poly.quiz.R;
import vn.poly.quiz.dao.UserDAO;
import vn.poly.quiz.models.User;

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerDetailActivity extends AppCompatActivity {

    CircleImageView ivAvatar;
    TextView tvDisplayName, tvNumberOfTimePlayed, tvCorrectRate, tvAverageTime;
    String username;
    UserDAO userDAO;
    @SuppressLint("SimpleDateFormat")
    final
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

        tvDisplayName.setText(u.getDisplayName());
        int numberOfTimePlayed = (int) userDAO.numberOfTimePlayed(username);
        tvNumberOfTimePlayed.setText(getString(R.string.player_detail_time_played,
                numberOfTimePlayed));

        int totalPlayTime = userDAO.totalTimePlayed(username);
        int averageTime = totalPlayTime / numberOfTimePlayed;
        String formatted = sdf.format(averageTime);
        tvAverageTime.setText(getString(R.string.player_detail_average_time, formatted));
        Locale.setDefault(new Locale("vi", "VN"));
        String rate = String.format(Locale.getDefault(),"%.2f", userDAO.correctRate(username)*100);
        tvCorrectRate.setText(getString(R.string.player_detail_correct_rate, rate));

        btnClose.setOnClickListener(view -> finish());
    }
}