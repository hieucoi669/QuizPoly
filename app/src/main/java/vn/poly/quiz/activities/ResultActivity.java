package vn.poly.quiz.activities;

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

import vn.poly.quiz.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.emitters.StreamEmitter;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import vn.poly.quiz.sound.App;
import vn.poly.quiz.sound.MusicManager;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    KonfettiView konfettiView;
    String displayName, imageURL, result;
    CircleImageView ivAvatarResult;
    TextView tvUsernameResult, tvScoreResult, tvTimeResult;
    Button btnShare, btnPlayAgain;
    SimpleDateFormat sdf;
    int numCorrectAnswer, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        konfettiView = findViewById(R.id.viewConfetti);
        tvUsernameResult = findViewById(R.id.tvUsernameResult);
        tvScoreResult = findViewById(R.id.tvScoreResult);
        tvTimeResult = findViewById(R.id.tvTimeResult);
        ivAvatarResult = findViewById(R.id.ivAvatarResult);
        btnShare = findViewById(R.id.btnShare);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);

        Locale.setDefault(new Locale("vi", "VN"));
        sdf = new SimpleDateFormat("mm:ss:SS", Locale.getDefault());

        Bundle bundle = getIntent().getExtras();
        imageURL = bundle.getString("imageURL");
        displayName = bundle.getString("displayName");
        numCorrectAnswer = bundle.getInt("numCorrect");
        time = bundle.getInt("time");

        tvUsernameResult.setText(getString(R.string.result_congratulation_message, displayName));
        result = getString(R.string.result_total_correct_ans_message, numCorrectAnswer);
        tvScoreResult.setText(result);
        tvTimeResult.setText(getString(R.string.result_total_time_message, sdf.format(time)));

        if(imageURL != null){
            Glide.with(this).load(imageURL).into(ivAvatarResult);
        }

        confettiEffect();

        btnShare.setOnClickListener(this);
        btnPlayAgain.setOnClickListener(this);
    }

    private void confettiEffect(){
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        float x1 = (float) (display.widthPixels / 3.0);
        float y1 = (float) (display.heightPixels / 5.5);
        float x2 = (float) (display.widthPixels * 2 / 3.0);
        float y2 = (float) (display.heightPixels / 5.5);

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
                .setPosition(konfettiView.getX() + x1, konfettiView.getY() + y1)
                .burst(300);
        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(0f, 10f)
                .setFadeOutEnabled(true)
                .setTimeToLive(500L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(5, 5f))
                .setPosition(konfettiView.getX() + x2, konfettiView.getY() + y2)
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
            App.getMusicPlayer().play(this, MusicManager.buttonClick, mediaPlayer -> {

            });
            share();
        }else if(id == R.id.btnPlayAgain){
            App.getMusicPlayer().play(this, MusicManager.buttonClick, mediaPlayer -> {

            });
            finish();
        }
    }

    private void share() {
        Bitmap bitmap = takeScreenShot();
        File f = saveBitmap(bitmap);
        shareScreenshot(f);
    }

    private void shareScreenshot(File f) {
        Uri uri = FileProvider.getUriForFile(
                ResultActivity.this,
                "vn.poly.quiz.activities",
                f);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "QuizPoly Test");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, result);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        Intent chooser = Intent.createChooser(shareIntent, getString(R.string.result_share_title));
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
        } catch (IOException e){
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