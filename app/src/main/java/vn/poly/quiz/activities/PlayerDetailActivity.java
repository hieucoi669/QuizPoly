package vn.poly.quiz.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import vn.poly.quiz.LoadingDialog;
import vn.poly.quiz.R;
import vn.poly.quiz.models.PlayerDetail;

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerDetailActivity extends AppCompatActivity {

    CircleImageView ivAvatar;
    TextView tvDisplayName, tvNumberOfTimePlayed, tvCorrectRate, tvAverageTime;
    String username, imageURL, displayName;
    @SuppressLint("SimpleDateFormat")
    final
    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SS");
    Button btnClose;
    DatabaseReference rootRef;
    LoadingDialog loadingDialog;
    PlayerDetail pd;
    int numberOfTimePlayed, averageTime;
    double correctRate;

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

        loadingDialog = new LoadingDialog(this);
        loadingDialog.showLoadingDialog();

        Intent i = getIntent();
        username = i.getStringExtra("username");
        imageURL = i.getStringExtra("imageURL");
        displayName = i.getStringExtra("displayName");

        rootRef = FirebaseDatabase.getInstance().getReference();
        Query query = rootRef.child("PlayerDetail").orderByChild("username").equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    pd = data.getValue(PlayerDetail.class);
                }
                assert pd != null;
                numberOfTimePlayed = pd.getTotalPlayed();
                averageTime = pd.getTotalTimePlayed()/numberOfTimePlayed;
                correctRate = pd.getTotalCorrectAnswer()*100.0/pd.getTotalQuestionAnswered();

                tvNumberOfTimePlayed.setText(getString(R.string.player_detail_time_played,
                        numberOfTimePlayed));
                String formatted = sdf.format(averageTime);
                tvAverageTime.setText(getString(R.string.player_detail_average_time, formatted));
                Locale.setDefault(new Locale("vi", "VN"));
                String rate = String.format(Locale.getDefault(),"%.2f", correctRate);
                tvCorrectRate.setText(getString(R.string.player_detail_correct_rate, rate));

                loadingDialog.hideLoadingDialog();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });

        if(imageURL != null){
            Glide.with(this).load(imageURL).into(ivAvatar);
        }

        tvDisplayName.setText(displayName);

        btnClose.setOnClickListener(view -> finish());
    }
}