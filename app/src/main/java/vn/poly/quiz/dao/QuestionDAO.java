package vn.poly.quiz.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import vn.poly.quiz.DatabaseHelper;
import vn.poly.quiz.models.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {
    private final SQLiteDatabase db;
    public static final String TABLE_NAME = "Question";
    public static final String SQL_Question = "Create table " + TABLE_NAME + "(" +
            "id integer PRIMARY KEY AUTOINCREMENT, " +
            "question text," +
            "correctAnswer text," +
            "falseAnswerOne text," +
            "falseAnswerTwo text," +
            "falseAnswerThree text);";

    public QuestionDAO(Context context){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public List<Question> getQuestionList()
    {
        List<Question> listQ = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME,null,null,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Question q = new Question();

            q.setId(cursor.getInt(0));
            q.setQuestion(cursor.getString(1));
            q.setCorrectAnswer(cursor.getString(2));
            q.setFalseAnswerOne(cursor.getString(3));
            q.setFalseAnswerTwo(cursor.getString(4));
            q.setFalseAnswerThree(cursor.getString(5));

            listQ.add(q);
            cursor.moveToNext();
        }
        cursor.close();
        return listQ;
    }

    public void insertQuestion(Question q){

        ContentValues values = new ContentValues();

        values.put("question", q.getQuestion());
        values.put("correctAnswer", q.getCorrectAnswer());
        values.put("falseAnswerOne", q.getFalseAnswerOne());
        values.put("falseAnswerTwo", q.getFalseAnswerTwo());
        values.put("falseAnswerThree", q.getFalseAnswerThree());

        try{
            db.insert(TABLE_NAME, null, values);
        }catch (Exception e){
            Log.i("QuestionDAO_Error", e.getMessage());
        }
    }

// --Commented out by Inspection START (11/27/20, 13:39):
//    public void deleteAllQuestion(){
//        db.delete(TABLE_NAME, null, null);
//    }
// --Commented out by Inspection STOP (11/27/20, 13:39)

}
