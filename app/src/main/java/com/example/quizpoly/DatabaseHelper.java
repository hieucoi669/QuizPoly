package com.example.quizpoly;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.quizpoly.DAO.QuestionDAO;
import com.example.quizpoly.DAO.QuizDAO;
import com.example.quizpoly.DAO.QuizResultDAO;
import com.example.quizpoly.DAO.UserDAO;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "datatintuc";
    public static final int VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(QuestionDAO.SQL_Question);
        sqLiteDatabase.execSQL(UserDAO.SQL_User);
        sqLiteDatabase.execSQL(QuizDAO.SQL_Quiz);
        sqLiteDatabase.execSQL(QuizResultDAO.SQL_QuizResult);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + QuestionDAO.TABLE_NAME);
        sqLiteDatabase.execSQL("drop table if exists " + UserDAO.TABLE_NAME);
        sqLiteDatabase.execSQL("drop table if exists " + QuizDAO.TABLE_NAME);
        sqLiteDatabase.execSQL("drop table if exists " + QuizResultDAO.TABLE_NAME);
    }
}
