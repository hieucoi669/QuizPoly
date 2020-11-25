package com.example.quizpoly.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.quizpoly.DatabaseHelper;
import com.example.quizpoly.Models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    public static final String TABLE_NAME = "User";
    public static final String SQL_User = "Create table " + TABLE_NAME + "(" +
            "id text PRIMARY KEY, " +
            "username text," +
            "password text," +
            "stringUri text," +
            "displayname text);";

    public UserDAO(Context context){
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public List<User> getUserList()
    {
        List<User> listUser = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME,null,null,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            User u = new User();

            u.setId(cursor.getString(0));
            u.setUsername(cursor.getString(1));
            u.setPassword(cursor.getString(2));
            u.setStringUri(cursor.getString(3));
            u.setDisplayname(cursor.getString(4));

            listUser.add(u);
            cursor.moveToNext();
        }
        cursor.close();
        return listUser;
    }

    public int insertUser(User u){

        ContentValues values = new ContentValues();

        values.put("id", u.getId());
        values.put("username", u.getUsername());
        values.put("password",u.getPassword());
        values.put("stringUri", u.getStringUri());
        values.put("displayname",u.getDisplayname());

        try{
            if(db.insert(TABLE_NAME, null, values) < 0){
                return -1;
            }
        }catch (Exception e){
            Log.i("UserDAO_Error", e.getMessage());
        }
        return 1;
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

    public int updateUser(User u)
    {
        ContentValues values = new ContentValues();

        values.put("id", u.getId());
        values.put("username", u.getUsername());
        values.put("password",u.getPassword());
        values.put("stringUri", u.getStringUri());
        values.put("displayname",u.getDisplayname());

        int kq = db.update(TABLE_NAME,values,"id=?", new String[]{u.getId()});
        if(kq==0)
        {
            return -1;
        }
        return 1;
    }

//    public User checkUserExist(String username) {
//        Cursor result = db.query(TABLE_NAME,null,"username=?",new String[]{username},null,null,null);
//        result.moveToFirst();
//        if(result.getCount() != 0)
//        {
//            User u = new User();
//            u.setId(result.getString(0));
//            u.setUsername(result.getString(1));
//            u.setPassword(result.getString(2));
//            u.setStringUri(result.getString(3));
//            u.setDisplayname(result.getString(4));
//
//            result.close();
//            return u;
//        }
//        result.close();
//        return null;
//    }

    public User checkUserExist(String HOC,String selection) {
        Cursor result = db.query(TABLE_NAME,null,selection,new String[]{HOC},null,null,null);
        result.moveToFirst();
        if(result.getCount() != 0)
        {
            User u = new User();
            u.setId(result.getString(0));
            u.setUsername(result.getString(1));
            u.setPassword(result.getString(2));
            u.setStringUri(result.getString(3));
            u.setDisplayname(result.getString(4));

            result.close();
            return u;
        }
        result.close();
        return null;
    }

    public float correctRate(String username){
        String sql = "SELECT AVG(result) AS TyLe " +
                "FROM Quiz INNER JOIN QuizResult on Quiz.quizID = QuizResult.quizID " +
                "WHERE username = '" + username + "'";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        if(!c.isAfterLast()){
            float i = c.getFloat(0);
            c.close();
            return i;
        }else {
            c.close();
            return 0;
        }
    }

    public long numberOfTimePlayed(String username){
        return DatabaseUtils.longForQuery(db,
                "SELECT COUNT(*) FROM Quiz WHERE username = '" + username + "'",
                null);
    }

    public int totalTimePlayed(String username){
        String sql = "SELECT SUM(time) AS TongThoiGian " +
                "FROM Quiz " +
                "WHERE username = '" + username + "'";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        if(!c.isAfterLast()){
            int i = c.getInt(0);
            c.close();
            return i;
        }else {
            c.close();
            return 0;
        }
    }
}
