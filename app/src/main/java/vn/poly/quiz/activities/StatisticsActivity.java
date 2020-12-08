package vn.poly.quiz.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.poly.quiz.LoadingDialog;
import vn.poly.quiz.R;
import vn.poly.quiz.models.QuestionInfo;
import vn.poly.quiz.sound.App;
import vn.poly.quiz.sound.MusicManager;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
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

public class StatisticsActivity extends AppCompatActivity {

    PieChart pieChartCorrect, pieChartWrong;
    final int[] colorClassArray = new int[]{
            Color.parseColor("#41fc03"), Color.parseColor("#ff4a4a")};
    ArrayList<PieEntry> data;
    TextView tvQuestionHR, tvQuestionLR;
    Button btnReturn;
    DatabaseReference rootRef;
    QuestionInfo qInfo, easyQ, hardQ;
    List<QuestionInfo> listQInfo;
    LoadingDialog loadingDialog;
    protected App mApp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        btnReturn = findViewById(R.id.btnReturnStatistics);
        tvQuestionHR = findViewById(R.id.tvQuestionHR);
        tvQuestionLR = findViewById(R.id.tvQuestionLR);
        pieChartCorrect = findViewById(R.id.pieChartCorrect);
        pieChartWrong = findViewById(R.id.pieChartWrong);

        mApp = (App) this.getApplicationContext();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.showLoadingDialog();
        getQuestionInfoList();

        btnReturn.setOnClickListener(view -> {
            App.getMusicPlayer().play(StatisticsActivity.this,
                    MusicManager.buttonClick, mediaPlayer -> {

            });
            finish();
        });
    }

    private void showInfo(QuestionInfo qInfo, PieChart pieChart, TextView tv){
        if(qInfo != null){
            data = new ArrayList<>();
            data.add(new PieEntry(qInfo.getNumberCorrectAnswer(),""));
            data.add(new PieEntry(
                    qInfo.getNumberAnswered() - qInfo.getNumberCorrectAnswer(), ""));

            settingsPieChart(data, pieChart);

            tv.setText(qInfo.getQuestion());
        }else{
            tv.setText(R.string.statistic_not_enough_data);
            pieChart.setVisibility(View.INVISIBLE);
        }
    }

    private void getQuestionInfoList() {
        listQInfo = new ArrayList<>();
        rootRef = FirebaseDatabase.getInstance().getReference();
        Query query = rootRef.child("QuestionInfo")
                .orderByChild("numberAnswered")
                .startAt(3);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    qInfo = data.getValue(QuestionInfo.class);
                    listQInfo.add(qInfo);
                }
                sortDescending(listQInfo);

                if(listQInfo.size() > 0){
                    easyQ = listQInfo.get(0);
                    hardQ = listQInfo.get(listQInfo.size() - 1);
                }

                showInfo(easyQ, pieChartCorrect, tvQuestionHR);
                showInfo(hardQ, pieChartWrong, tvQuestionLR);
                loadingDialog.hideLoadingDialog();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void settingsPieChart(ArrayList<PieEntry> data, PieChart chart){
        PieDataSet pieDataSet = new PieDataSet(data,"");
        pieDataSet.setColors(colorClassArray);
        pieDataSet.setSliceSpace(3);
        pieDataSet.setValueTextSize(6f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        pieDataSet.setValueFormatter(new PercentFormatter(chart));
        PieData pieData = new PieData(pieDataSet);
        chart.setData(pieData);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setUsePercentValues(true);
        chart.setHoleRadius(35);
        chart.setTransparentCircleRadius(45);
        chart.animateXY(1000,1000);
//        chart.setNoDataText("Not enough data!");
//        chart.setNoDataTextColor(Color.BLACK);
        chart.invalidate();
    }

    private void sortDescending(List<QuestionInfo> list){
        Collections.sort(list, (qi1, qi2) -> {
            if (qi1.getRate() < qi2.getRate()) {
                return 1;
            } else if(qi1.getRate() > qi2.getRate()) {
                return -1;
            } else{
                return Integer.compare(qi2.getNumberAnswered(), qi1.getNumberAnswered());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApp.setCurrentActivity(this);
    }
}