package vn.poly.quiz.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import vn.poly.quiz.DatabaseHelper;
import vn.poly.quiz.models.Quiz;

import java.util.ArrayList;
import java.util.List;

public class  QuizDAO {
    private final SQLiteDatabase db;
    public static final String TABLE_NAME = "Quiz";
    public static final String SQL_Quiz = "Create table " + TABLE_NAME + "(" +
            "quizID text PRIMARY KEY, " +
            "username text," +
            "numberCorrectAnswer int," +
            "time int);";

    public QuizDAO(Context context){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public List<Quiz> getQuizList()
    {
        List<Quiz> listQ = new ArrayList<>();
        String sql = "Select * " +
                "FROM Quiz " +
                "ORDER BY numberCorrectAnswer DESC, time";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Quiz q = new Quiz();

            q.setQuizID(cursor.getString(0));
            q.setUsername(cursor.getString(1));
            q.setNumCorrectAnswer(cursor.getInt(2));
            q.setTime(cursor.getInt(3));

            listQ.add(q);
            cursor.moveToNext();
        }
        cursor.close();
        return listQ;
    }

    public void insertQuiz(Quiz q){

        ContentValues values = new ContentValues();

        values.put("quizID", q.getQuizID());
        values.put("username", q.getUsername());
        values.put("numberCorrectAnswer", q.getNumCorrectAnswer());
        values.put("time", q.getTime());

        try{
            db.insert(TABLE_NAME, null, values);
        }catch (Exception e){
            Log.i("QuizDAO_Error", e.getMessage());
        }
    }



    public void updateQuiz(String quizID, int numCorrectAnswer, int time)
    {
        ContentValues values = new ContentValues();

        values.put("numberCorrectAnswer", numCorrectAnswer);
        values.put("time", time);

        db.update(TABLE_NAME,values,"quizID=?", new String[]{quizID});
    }

    public List<Quiz> filterQuiz(String username) {
        List<Quiz> listQ = new ArrayList<>();
        String getSQL = "Select * From Quiz Where username Like '%"+username+"%' " +
                "ORDER BY numberCorrectAnswer DESC, time";
        Cursor cursor = db.rawQuery(getSQL,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Quiz q = new Quiz();

            q.setQuizID(cursor.getString(0));
            q.setUsername(cursor.getString(1));
            q.setNumCorrectAnswer(cursor.getInt(2));
            q.setTime(cursor.getInt(3));

            listQ.add(q);

            cursor.moveToNext();
        }
        cursor.close();
        return listQ;
    }

}
