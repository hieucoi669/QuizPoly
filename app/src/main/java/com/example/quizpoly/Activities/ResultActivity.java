package com.example.quizpoly.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quizpoly.R;
import com.example.quizpoly.Models.User;
import com.example.quizpoly.DAO.UserDAO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.emitters.StreamEmitter;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    UserDAO userDAO;
    KonfettiView konfettiView;
    String username, imageStringUri, result;
    CircleImageView ivAvatarResult;
    TextView tvUsernameResult, tvScoreResult;
    Button btnShare, btnPlayAgain;

    int numCorrectAnswer, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        konfettiView = findViewById(R.id.viewKonfetti);
        tvUsernameResult = findViewById(R.id.tvUsernameResult);
        tvScoreResult = findViewById(R.id.tvScoreResult);
        ivAvatarResult = findViewById(R.id.ivAvatarResult);
        btnShare = findViewById(R.id.btnShare);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);

        userDAO = new UserDAO(this);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        numCorrectAnswer = bundle.getInt("numCorrect");
        time = bundle.getInt("time");

        User u = userDAO.checkUserExist(username,"username=?");
        imageStringUri = u.getStringUri();

        tvUsernameResult.setText("Chúc mừng " + username + "!");
        result = "Bạn đã trả lời đúng " + numCorrectAnswer + " trên 10 câu";
        tvScoreResult.setText(result);

        if(imageStringUri != null){
            Glide.with(this).load(new File(imageStringUri)).into(ivAvatarResult);
        }

        confettiEffect();

        btnShare.setOnClickListener(this);
        btnPlayAgain.setOnClickListener(this);
    }

    private void confettiEffect(){
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);

        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.CYAN)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 2f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Circle.INSTANCE, Shape.Square.INSTANCE)
                .addSizes(new Size(8, 5f))
                .setPosition(-50f, display.widthPixels + 50f, -50f, -50f)
                .streamFor(50, StreamEmitter.INDEFINITE);

        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(0f, 10f)
                .setFadeOutEnabled(true)
                .setTimeToLive(500L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(5, 5f))
                .setPosition(konfettiView.getX() + display.widthPixels / 3, konfettiView.getY() + display.heightPixels /8)
                .burst(300);
        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(0f, 10f)
                .setFadeOutEnabled(true)
                .setTimeToLive(500L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(5, 5f))
                .setPosition(konfettiView.getX() + display.widthPixels * 2 / 3, konfettiView.getY() + display.heightPixels /8)
                .burst(300);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        konfettiView.stopGracefully();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btnShare){
            Bitmap bitmap = takeScreenShot();
            File f = saveBitmap(bitmap);
            shareScreenshot(f);
        }else if(id == R.id.btnPlayAgain){
//            Intent intent = new Intent(ResultActivity.this, MenuActivity.class);
//            intent.putExtra("username",username);
//            startActivity(intent);
            finish();
        }
    }

    private void shareScreenshot(File f) {
        Uri uri = FileProvider.getUriForFile(
                ResultActivity.this,
                "com.example.quizpoly.Activities",
                f);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "QuizPoly Test");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, result);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        Intent chooser = Intent.createChooser(shareIntent, "Chia sẻ ảnh: ");
        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        startActivity(chooser);
    }

    private File saveBitmap(Bitmap bitmap) {
        File imagePath = new File(getExternalFilesDir("Pictures"),
                "SharedScreenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        }catch (FileNotFoundException e){
            Log.i("Save Screenshot Error", e.toString());
        }catch (IOException e){
            Log.i("Save Screenshot Error", e.toString());
        }
        return imagePath;
    }

    private Bitmap takeScreenShot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

}