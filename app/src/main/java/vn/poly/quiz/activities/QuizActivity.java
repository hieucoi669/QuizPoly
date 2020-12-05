package vn.poly.quiz.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import vn.poly.quiz.R;
import vn.poly.quiz.dao.QuizDAO;
import vn.poly.quiz.dao.QuizResultDAO;
import vn.poly.quiz.models.Question;
import vn.poly.quiz.dao.QuestionDAO;
import vn.poly.quiz.models.Quiz;
import vn.poly.quiz.models.QuizResult;
import vn.poly.quiz.sound.App;
import vn.poly.quiz.sound.MusicManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    QuestionDAO questionDAO;
    QuizDAO quizDAO;
    QuizResultDAO quizResultDAO;
    List<Question> listQuestion;
    List<String> listAnswer;
    List<TextView> listTV;
    String username;
    final String quizID = UUID.randomUUID().toString();
    int currentPos = 1, countTime = 0, numCorrectAnswer = 0, totalTime = 0;
    TextView tvQuestion, tvAnswerOne, tvAnswerTwo, tvAnswerThree, tvAnswerFour, tvProgress, tvClock;
    ProgressBar pbQuestion, pbClock;
    @SuppressLint("SimpleDateFormat")
    final
    SimpleDateFormat sdf = new SimpleDateFormat("ss:SS");
    ObjectAnimator animation;
    boolean answered, countDownSound, doubleBackToExitPressedOnce=false;

    FrameLayout frameLayoutClock;
    Animation animation1,animation2;
    AnimationSet animationSet;

    TextView tvCount;

    AlphaAnimation animationAlpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizcount_layout);
        tvCount = findViewById(R.id.tvCount);
        countDown(tvCount,3);
        hideSystemUI();
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void khaibao()
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

        questionDAO = new QuestionDAO(this);
        quizDAO = new QuizDAO(this);
        quizResultDAO = new QuizResultDAO(this);

        listAnswer = new ArrayList<>();

        Intent i = getIntent();
        username = i.getStringExtra("username");

        listTV = new ArrayList<>();
        Collections.addAll(listTV, tvAnswerOne, tvAnswerTwo, tvAnswerThree, tvAnswerFour);

        generateQuestion();
        listQuestion = questionDAO.getQuestionList();
        Collections.shuffle(listQuestion);

        setQuestion();

        tvAnswerOne.setOnClickListener(this);
        tvAnswerTwo.setOnClickListener(this);
        tvAnswerThree.setOnClickListener(this);
        tvAnswerFour.setOnClickListener(this);

        Quiz q = new Quiz(quizID, username, 0, 0);
        quizDAO.insertQuiz(q);

        frameLayoutClock = findViewById(R.id.frameClock);

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

        totalTime += countTime;
        Question q = listQuestion.get(currentPos - 1);
        QuizResult qr = new QuizResult();
        qr.setQuizID(quizID);
        qr.setQuestionID(q.getId());

        if(tv != null){

            if(tv.getText().equals(q.getCorrectAnswer())){
                numCorrectAnswer++;
                colorFadeTrue(tv);
                qr.setResult(1);
            }else{
                colorFadeWrong(tv);
                for(TextView textView : listTV){
                    if(textView.getText().equals(q.getCorrectAnswer())){
                        colorFadeTrue(textView);
                        break;
                    }
                }
                qr.setResult(0);
            }
        }else {
            for(TextView textView : listTV){
                if(textView.getText().equals(q.getCorrectAnswer())){
                    colorFadeTrue(textView);
                    break;
                }
            }
            qr.setResult(0);
        }

        quizResultDAO.insertQuizResult(qr);
        quizDAO.updateQuiz(quizID, numCorrectAnswer, totalTime);

        new CountDownTimer(2000, 1000) {
            public void onFinish() {
                currentPos++;
                if(11 == currentPos){
                    Intent i = new Intent(QuizActivity.this, ResultActivity.class);
                    Bundle b = new Bundle();
                    b.putString("username", username);
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

    private void generateQuestion(){
        if(questionDAO.getQuestionList().size() == 0){
            questionDAO.insertQuestion(
                    new Question("Hiện tại, FPT Polytechnic có bao nhiêu cơ sở trên toàn quốc?",
                            "7",
                            "8","5","6"));

            questionDAO.insertQuestion(new Question("Trường Cao đẳng FPT Polytechnic được thành lập vào khoảng thời gian nào?",
                    "Tháng 7 năm 2010",
                    "Tháng 7 năm 2009",
                    "Tháng 8 năm 2010",
                    "Tháng 8 năm 2009"));

            questionDAO.insertQuestion(new Question("Nhóm ngành nào sau đây 'không' nằm trong danh sách đào tạo của FPoly?",
                    "Khoa học máy tính",
                    "Thiết kế đồ họa",
                    "Hướng dẫn du lịch",
                    "Phun xăm thẩm mỹ"));

            questionDAO.insertQuestion(new Question("Nhóm ngành nào sau đây 'không' nằm trong danh sách đào tạo của FPoly?",
                    "Khoa học máy tính",
                    "Thiết kế đồ họa",
                    "Hướng dẫn du lịch",
                    "Phun xăm thẩm mỹ"));

            questionDAO.insertQuestion(new Question("Cơ sở FPT Polytechnic đầu tiên được thành lập ở đâu?",
                    "TP Hà Nội",
                    "TP Hồ Chí Minh",
                    "TP Đà Nẵng",
                    "TP Cần Thơ"));

            //Clone
            questionDAO.insertQuestion(
                    new Question("Hiện tại, FPT Polytechnic có bao nhiêu cơ sở trên toàn quốc?",
                            "7",
                            "8","5","6"));

            questionDAO.insertQuestion(new Question("Trường Cao đẳng FPT Polytechnic được thành lập vào khoảng thời gian nào?",
                    "Tháng 7 năm 2010",
                    "Tháng 7 năm 2009",
                    "Tháng 8 năm 2010",
                    "Tháng 8 năm 2009"));

            questionDAO.insertQuestion(new Question("Nhóm ngành nào sau đây 'không' nằm trong danh sách đào tạo của FPoly?",
                    "Khoa học máy tính",
                    "Thiết kế đồ họa",
                    "Hướng dẫn du lịch",
                    "Phun xăm thẩm mỹ"));

            questionDAO.insertQuestion(new Question("Nhóm ngành nào sau đây 'không' nằm trong danh sách đào tạo của FPoly?",
                    "Khoa học máy tính",
                    "Thiết kế đồ họa",
                    "Hướng dẫn du lịch",
                    "Phun xăm thẩm mỹ"));

            questionDAO.insertQuestion(new Question("Cơ sở FPT Polytechnic đầu tiên được thành lập ở đâu?",
                    "TP Hà Nội",
                    "TP Hồ Chí Minh",
                    "TP Đà Nẵng",
                    "TP Cần Thơ"));
        }
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
            khaibao();
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
        fadeIn.setDuration(1000);
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
        Toast.makeText(this, getString(R.string.quiz_on_press_back_toast), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE

                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}