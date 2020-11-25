package com.example.quizpoly.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.quizpoly.DatabaseHelper;
import com.example.quizpoly.Models.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    public static final String TABLE_NAME = "Question";
    public static final String SQL_Question = "Create table " + TABLE_NAME + "(" +
            "id integer PRIMARY KEY AUTOINCREMENT, " +
            "question text," +
            "correctAnswer text," +
            "falseAnswerOne text," +
            "falseAnswerTwo text," +
            "falseAnswerThree text);";

    public QuestionDAO(Context context){
        dbHelper = new DatabaseHelper(context);
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

    public int insertQuestion(Question q){

        ContentValues values = new ContentValues();

        values.put("question", q.getQuestion());
        values.put("correctAnswer", q.getCorrectAnswer());
        values.put("falseAnswerOne", q.getFalseAnswerOne());
        values.put("falseAnswerTwo", q.getFalseAnswerTwo());
        values.put("falseAnswerThree", q.getFalseAnswerThree());

        try{
            if(db.insert(TABLE_NAME, null, values) < 0){
                return -1;
            }
        }catch (Exception e){
            Log.i("QuestionDAO_Error", e.getMessage());
        }
        return 1;
    }

    public void deleteAllQuestion(){
        db.delete(TABLE_NAME, null, null);
    }

//    public int deleteTinTuc(int id)
//    {
//        String idxoa = String.valueOf(id);
//        int kq = db.delete(TABLE_NAME,"id=?",new String[]{idxoa});
//        if(kq==0)
//        {
//            return -1;//xoa khong thanh cong
//        }
//        return 1;//xoa thanh cong
//    }

//    public int updateDiaDiem(DiaDiem dd)
//    {
//        ContentValues values = new ContentValues();
//        values.put("id", dd.getId());
//        values.put("tieude", dd.getTieude());
//        values.put("kinhdo", dd.getKinhdo());
//        values.put("vido" , dd.getVido());
//        int kq = db.update(TABLE_NAME,values,"id=?", new String[]{dd.getId()});
//        if(kq==0)
//        {
//            return -1;
//        }
//        return 1;
//    }
//
//    public DiaDiem checkIDExist(String id) {
//        Cursor result = db.query(TABLE_NAME,null,"id=?",new String[]{id},null,null,null);
//        result.moveToFirst();
//        if(result.getCount() != 0)
//        {
//            DiaDiem dd = new DiaDiem();
//            dd.setId(result.getString(0));
//            dd.setTieude(result.getString(1));
//            dd.setKinhdo(result.getString(2));
//            dd.setVido(result.getString(3));
//            result.close();
//            return dd;
//        }
//        result.close();
//        return null;
//    }

}
