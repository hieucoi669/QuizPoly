package vn.poly.quiz.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import vn.poly.quiz.DatabaseHelper;
import vn.poly.quiz.models.QuestionRate;
import vn.poly.quiz.models.QuizResult;

public class QuizResultDAO {
    private final SQLiteDatabase db;
    public static final String TABLE_NAME = "QuizResult";
    public static final String SQL_QuizResult = "Create table " + TABLE_NAME + "(" +
            "quizResultID integer PRIMARY KEY AUTOINCREMENT, " +
            "quizID text," +
            "questionID integer," +
            "result integer);";

    public QuizResultDAO(Context context){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

// --Commented out by Inspection START (11/27/20, 20:10):
//    public List<QuizResult> getQuizResultList()
//    {
//        List<QuizResult> listQR = new ArrayList<>();
//        Cursor cursor = db.query(TABLE_NAME,null,null,null,null,null,null);
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast())
//        {
//            QuizResult qr = new QuizResult();
//
//            qr.setId(cursor.getInt(0));
//            qr.setQuizID(cursor.getString(1));
//            qr.setQuestionID(cursor.getInt(2));
//            qr.setResult(cursor.getInt(3));
//
//            listQR.add(qr);
//            cursor.moveToNext();
//        }
//        cursor.close();
//        return listQR;
//    }
// --Commented out by Inspection STOP (11/27/20, 20:10)

    public void insertQuizResult(QuizResult qr){

        ContentValues values = new ContentValues();

        values.put("quizID", qr.getQuizID());
        values.put("questionID", qr.getQuestionID());
        values.put("result", qr.getResult());

        try{
            db.insert(TABLE_NAME, null, values);
        }catch (Exception e){
            Log.i("QuizResultDAO_Error", e.getMessage());
        }
    }

    public QuestionRate getQuestionRate(String orderBy){
        String sql = "SELECT questionID, question, (SUM(result)*100/COUNT(result)) " +
                "FROM Question INNER JOIN QuizResult ON Question.id = QuizResult.questionID " +
                "GROUP BY questionID " +
                "ORDER BY (SUM(result)*100/COUNT(result)) " + orderBy + " " +
                "limit 1";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        if(c.getCount() == 1){
            QuestionRate qr = new QuestionRate(c.getString(1), c.getInt(2));
            c.close();
            return qr;
        }
        c.close();
        return null;
    }
}
