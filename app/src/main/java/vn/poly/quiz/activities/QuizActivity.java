package vn.poly.quiz.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import vn.poly.quiz.models.Question;
import vn.poly.quiz.models.QuestionInfo;
import vn.poly.quiz.models.Quiz;
import vn.poly.quiz.sound.App;
import vn.poly.quiz.sound.MusicManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    List<Question> listQuestion;
    List<String> listAnswer;
    List<TextView> listTV;
    String username, displayName, uniqueKey, imageURL;
    int currentPos = 1, countTime = 0, numCorrectAnswer = 0, totalTime = 0;
    TextView tvQuestion, tvAnswerOne, tvAnswerTwo, tvAnswerThree,
            tvAnswerFour, tvProgress, tvClock, tvCount;
    ProgressBar pbQuestion, pbClock;
    DatabaseReference rootRef, questionRef;
    LoadingDialog loadingDialog;
    Quiz quiz;
    PlayerDetail pd;
    QuestionInfo qInfo;
    SimpleDateFormat sdf;
    ObjectAnimator animation;
    boolean answered, countDownSound, doubleBackToExitPressedOnce=false;

    FrameLayout frameLayoutClock;
    Animation animation1,animation2;
    AnimationSet animationSet;

    AlphaAnimation animationAlpha;
    protected App mApp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizcount_layout);
        tvCount = findViewById(R.id.tvCount);

        mApp = (App) this.getApplicationContext();
        rootRef = FirebaseDatabase.getInstance().getReference();
        loadingDialog = new LoadingDialog(this);
        loadingDialog.showLoadingDialog();
        getQuestionList();
        hideSystemUI();
    }

    private void declaration()
    {
        tvQuestion = findViewById(R.id.tvQuestion);
        tvAnswerOne = findViewById(R.id.tvAnswerOne);
        tvAnswerTwo = findViewById(R.id.tvAnswerTwo);
        tvAnswerThree = findViewById(R.id.tvAnswerThree);
        tvAnswerFour = findViewById(R.id.tvAnswerFour);
        tvProgress = findViewById(R.id.tvProgress);
        tvClock = findViewById(R.id.tvClock);
        pbQuestion = findViewById(R.id.pbQuestion);
        pbClock = findViewById(R.id.pbClock);
        frameLayoutClock = findViewById(R.id.frameClock);

        Locale.setDefault(new Locale("vi", "VN"));
        sdf = new SimpleDateFormat("ss:SS", Locale.getDefault());

        listAnswer = new ArrayList<>();

        Intent i = getIntent();
        username = i.getStringExtra("username");
        displayName = i.getStringExtra("displayName");
        imageURL = i.getStringExtra("imageURL");

        listTV = new ArrayList<>();
        Collections.addAll(listTV, tvAnswerOne, tvAnswerTwo, tvAnswerThree, tvAnswerFour);

        setQuestion();

        tvAnswerOne.setOnClickListener(this);
        tvAnswerTwo.setOnClickListener(this);
        tvAnswerThree.setOnClickListener(this);
        tvAnswerFour.setOnClickListener(this);

        uniqueKey = rootRef.child("Quiz").push().getKey();
        quiz = new Quiz(uniqueKey, imageURL, username, displayName, 0, 0);
        rootRef.child("Quiz").child(uniqueKey).setValue(quiz);
        setTimePlayed();

        animation1 = AnimationUtils.loadAnimation(this,R.anim.zoomin);
        animation2 = AnimationUtils.loadAnimation(this,R.anim.rotate);
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation1);
        animationSet.addAnimation(animation2);
    }

    private void setQuestion(){

        pbQuestion.setProgress(currentPos);
        tvProgress.setText(getString(R.string.quiz_progress_question, currentPos, pbQuestion.getMax()));
        setDefaultAnswer();
        setClock();
        countDownSound = false;

        Question q = listQuestion.get(currentPos - 1);

        listAnswer.clear();
        Collections.addAll(listAnswer, q.getCorrectAnswer(), q.getFalseAnswerOne(),
                q.getFalseAnswerTwo(), q.getFalseAnswerThree());
        Collections.shuffle(listAnswer);

        textFade(tvQuestion);
        tvQuestion.setText(q.getQuestion());
        textFade(tvAnswerOne);
        tvAnswerOne.setText(listAnswer.get(0));
        textFade(tvAnswerTwo);
        tvAnswerTwo.setText(listAnswer.get(1));
        textFade(tvAnswerThree);
        tvAnswerThree.setText(listAnswer.get(2));
        textFade(tvAnswerFour);
        tvAnswerFour.setText(listAnswer.get(3));
    }

    private void setDefaultAnswer(){
        answered = false;
        for(TextView tv : listTV){
            tv.setTypeface(Typeface.DEFAULT);
            tv.setBackground(ContextCompat.getDrawable(this, R.drawable.default_border_bg));
            tv.setClickable(true);
        }
    }

    private void setSelectedAnswer(TextView tv){
        tv.setTypeface(null,Typeface.BOLD);
        tv.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_border_bg));
        frameLayoutClock.clearAnimation();
        App.getMusicPlayer().stopSound();
        answered = true;
        animation.cancel();

        for(TextView textView : listTV){
            textView.setClickable(false);
        }

        new CountDownTimer(2000, 1000) {
            public void onFinish() {
                // When timer is finished
                // Execute your code here
                setResult(tv);
            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
            }
        }.start();

    }

    private void setResult(TextView tv){

        boolean answer;
        totalTime += countTime;
        Question q = listQuestion.get(currentPos - 1);
        String id = q.getId();
        String question = q.getQuestion();

        if(tv != null){

            if(tv.getText().equals(q.getCorrectAnswer())){
                numCorrectAnswer++;
                colorFadeTrue(tv);
                answer = true;
            }else{
                colorFadeWrong(tv);
                for(TextView textView : listTV){
                    if(textView.getText().equals(q.getCorrectAnswer())){
                        colorFadeTrue(textView);
                        break;
                    }
                }
                answer = false;
            }
        }else {
            for(TextView textView : listTV){
                if(textView.getText().equals(q.getCorrectAnswer())){
                    colorFadeTrue(textView);
                    break;
                }
            }
            answer = false;
        }

        quiz.setNumCorrectAnswer(numCorrectAnswer);
        quiz.setTime(totalTime);
        rootRef.child("Quiz").child(uniqueKey).setValue(quiz);
        setPlayerDetail(countTime, answer);

        setQuestionInfo(id, question, answer);

        new CountDownTimer(2000, 1000) {
            public void onFinish() {
                currentPos++;
                if(11 == currentPos){
                    Intent i = new Intent(QuizActivity.this, ResultActivity.class);
                    Bundle b = new Bundle();
                    b.putString("imageURL", imageURL);
                    b.putString("displayName", displayName);
                    b.putInt("numCorrect", numCorrectAnswer);
                    b.putInt("time", totalTime);
                    i.putExtras(b);
                    startActivity(i);
                    finish();
                }else {
                    setQuestion();
                }
            }

            public void onTick(long millisUntilFinished) {

            }
        }.start();
    }

    private void setClock(){

        animation = ObjectAnimator.ofInt(pbClock, "progress", 1500, 0);
        animation.setDuration(15000);
        animation.setInterpolator(null);
        animation.addUpdateListener(valueAnimator -> {
            int time = Integer.parseInt(valueAnimator.getAnimatedValue().toString())*10;
            countTime = 15000 - time;
            String formatted = sdf.format(time);
            tvClock.setText(formatted);
            if(countTime>=10000 && !countDownSound)
            {
                frameLayoutClock.startAnimation(animationSet);
                App.getMusicPlayer().play(this, MusicManager.clockTicking, mediaPlayer -> {});
                countDownSound = true;
            }
        });
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                frameLayoutClock.clearAnimation();
                if(!answered){
                    setResult(null);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animation.start();
    }

    private void setTimePlayed(){
        Query query = rootRef.child("PlayerDetail").orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        pd = data.getValue(PlayerDetail.class);
                    }
                }else {
                    pd = new PlayerDetail(username, 0, 0,
                            0, 0);
                }
                if(pd != null){
                    int newTimePlayed = pd.getTotalPlayed() + 1;
                    pd.setTotalPlayed(newTimePlayed);
                }

                rootRef.child("PlayerDetail")
                        .child(username)
                        .setValue(pd);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void setPlayerDetail(int countTime, boolean correct){
        int newTotalTime = pd.getTotalTimePlayed() + countTime;
        pd.setTotalTimePlayed(newTotalTime);

        int newTotalQuestionAns = pd.getTotalQuestionAnswered() + 1;
        pd.setTotalQuestionAnswered(newTotalQuestionAns);

        int newTotalCorrect = pd.getTotalCorrectAnswer();
        if(correct){
            newTotalCorrect ++;
        }
        pd.setTotalCorrectAnswer(newTotalCorrect);

        rootRef.child("PlayerDetail")
                .child(username)
                .setValue(pd);
    }

    private void getQuestionList(){
        listQuestion = new ArrayList<>();
        questionRef = rootRef.child("Question");
        questionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren() ){
                    Question q = data.getValue(Question.class);
                    if (q != null) {
                        q.setId(data.getKey());
                    }
                    listQuestion.add(q);
                    listQuestion.add(q);
                }
                Collections.shuffle(listQuestion);
                loadingDialog.hideLoadingDialog();
                countDown(tvCount,3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setQuestionInfo(String id, String question, boolean correct){
        Query query = rootRef.child("QuestionInfo").orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        qInfo = data.getValue(QuestionInfo.class);
                    }
                }else {
                    qInfo = new QuestionInfo(id, question, 0, 0);
                }

                if(qInfo != null){
                    int newNumberAnswered = qInfo.getNumberAnswered() + 1;
                    qInfo.setNumberAnswered(newNumberAnswered);
                    if(correct){
                        int newNumberCorrectAnswered = qInfo.getNumberCorrectAnswer() + 1;
                        qInfo.setNumberCorrectAnswer(newNumberCorrectAnswered);
                    }
                }

                rootRef.child("QuestionInfo")
                        .child(id)
                        .setValue(qInfo);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.tvAnswerOne){

            setSelectedAnswer(tvAnswerOne);

        }else if(id == R.id.tvAnswerTwo){

            setSelectedAnswer(tvAnswerTwo);

        }else if(id == R.id.tvAnswerThree){

            setSelectedAnswer(tvAnswerThree);

        }else if(id == R.id.tvAnswerFour){

            setSelectedAnswer(tvAnswerFour);

        }

    }

    private void countDown(final TextView tv, final int count) {
        if (count == 0) {
            tv.setText("");
            setContentView(R.layout.activity_quizs);
            declaration();
            return;
        }
        tv.setText(String.valueOf(count));
        animationAlpha = new AlphaAnimation(1.0f, 0.0f);
        animationAlpha.setDuration(1000);
        animationAlpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation anim) {
                countDown(tv, count - 1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tv.startAnimation(animationAlpha);
    }

    private void colorFadeTrue(TextView tv){
        ObjectAnimator colorFadeTrue = ObjectAnimator.ofObject(tv, "backgroundColor",
                new ArgbEvaluator(), Color.WHITE,Color.GREEN);
        colorFadeTrue.setDuration(600);
        colorFadeTrue.start();
    }

    private void colorFadeWrong(TextView tv){
        ObjectAnimator colorFadeTrue = ObjectAnimator.ofObject(tv, "backgroundColor",
                new ArgbEvaluator(), Color.WHITE,Color.RED);
        colorFadeTrue.setDuration(600);
        colorFadeTrue.start();
    }

    private void textFade(TextView tv){
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f);
        fadeIn.setDuration(600);
        tv.startAnimation(fadeIn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(animation != null){
            answered = true;
            animation.end();
        }
        App.getMusicPlayer().stopSound();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            animationAlpha.cancel();
            if(animation != null){
                answered = true;
                animation.end();
            }
            App.getMusicPlayer().stopSound();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this,
                getString(R.string.quiz_on_press_back_toast), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if(controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }
        else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApp.setCurrentActivity(this);
    }
}