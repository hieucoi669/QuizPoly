package vn.poly.quiz.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.poly.quiz.R;
import vn.poly.quiz.dao.QuizResultDAO;
import vn.poly.quiz.models.QuestionRate;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    QuizResultDAO quizResultDAO;
    PieChart pieChartCorrect, pieChartWrong;
    final int[] colorClassArray = new int[]{
            Color.parseColor("#41fc03"), Color.parseColor("#ff4a4a")};
    ArrayList<PieEntry> dataEasy, dataHard;
    TextView tvQuestionHR, tvQuestionLR;
    Button btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        btnReturn = findViewById(R.id.btnReturnStatistics);
        tvQuestionHR = findViewById(R.id.tvQuestionHR);
        tvQuestionLR = findViewById(R.id.tvQuestionLR);
        pieChartCorrect = findViewById(R.id.pieChartCorrect);
        pieChartWrong = findViewById(R.id.pieChartWrong);

        quizResultDAO = new QuizResultDAO(this);
        QuestionRate easyQ = quizResultDAO.getQuestionRate("DESC");

        if(easyQ != null){
            dataEasy = new ArrayList<>();
            dataEasy.add(new PieEntry(easyQ.getRate(),""));
            dataEasy.add(new PieEntry(100 - easyQ.getRate(), ""));

            settingsPieChart(dataEasy, pieChartCorrect);

            tvQuestionHR.setText(easyQ.getQuestion());
        }else{
            tvQuestionHR.setText(R.string.statistic_not_enough_data);
            pieChartCorrect.setVisibility(View.INVISIBLE);
        }

        QuestionRate hardQ = quizResultDAO.getQuestionRate("");

        if(hardQ != null){
            dataHard = new ArrayList<>();
            dataHard.add(new PieEntry(hardQ.getRate(),""));
            dataHard.add(new PieEntry(100 - hardQ.getRate(), ""));

            settingsPieChart(dataHard, pieChartWrong);

            tvQuestionLR.setText(hardQ.getQuestion());
        }else{
            tvQuestionLR.setText(R.string.statistic_not_enough_data);
            pieChartWrong.setVisibility(View.INVISIBLE);
        }

        btnReturn.setOnClickListener(view -> finish());
    }

    private void settingsPieChart(ArrayList<PieEntry> data, PieChart chart){
        PieDataSet pieDataSet = new PieDataSet(data,"");
        pieDataSet.setColors(colorClassArray);
        pieDataSet.setSliceSpace(3);
        pieDataSet.setValueTextSize(6f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        pieDataSet.setValueFormatter(new PercentFormatter(pieChartCorrect));
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
}