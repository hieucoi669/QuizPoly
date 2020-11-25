package com.example.quizpoly.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quizpoly.R;
import com.example.quizpoly.Sound.App;
import com.example.quizpoly.Sound.MusicManager;
import com.example.quizpoly.Models.User;
import com.example.quizpoly.DAO.UserDAO;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    UserDAO userDAO;
    TextInputLayout edUsername,edPassword,edRePassword;
    Button btnDangKy,btnBack;
    List<String> nameList;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
        edRePassword = findViewById(R.id.edRePassword);
        btnDangKy = findViewById(R.id.btnDangKy);
        btnBack = findViewById(R.id.btnBack);
        userDAO = new UserDAO(this);
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getMusicPlayer().play(MusicManager.buttonclick, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                    }
                });
                String id = null;
                String username = checkUsername();
                String password = checkPassword();
                id = UUID.randomUUID().toString();
                boolean repass = checkRePassword();
                if(username != null && password != null && repass)
                {
                    User u = new User();
                    u.setId(id);
                    u.setUsername(username);
                    u.setPassword(password);

                    try {
                        if(userDAO.insertUser(u)>0)
                        {
                            Toast.makeText(SignUpActivity.this, "Đăng kí thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, OneTimeActivity.class);
                            intent.putExtra("id",id);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(SignUpActivity.this, "Đăng kí không thành công!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Log.i("AddUser", e.toString());
                    }
                    finish();
                }
                else{
                    Toast.makeText(SignUpActivity.this,"Đăng kí không thành công!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getMusicPlayer().play(MusicManager.buttonclick, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                    }
                });
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public String checkUsername(){
        String name = null;
        nameList = new ArrayList<>();
        userList = userDAO.getUserList();
        for(User u : userList)
        {
            nameList.add(u.getUsername());
        }
        try {
            name = edUsername.getEditText().getText().toString();
            if(name.length()==0)
            {
                edUsername.setError("Tên đăng nhập không được để trống!");
                return null;
            }
            if(name.length()<5 || name.length()>20)
            {
                edUsername.setError("Tên đăng nhập có độ dài từ 5-20 kí tự!");
                return null;
            }
            for(int i=0; i<nameList.size(); i++){
                if(name.equalsIgnoreCase(nameList.get(i))){
                    edUsername.setError("Tên đăng nhập đã tồn tại!");
                    return null;
                }
            }

        }catch (Exception e)
        {
            edUsername.setError("Tên đăng nhập không hợp lệ");
        }
        return name;
    }

    public String checkPassword(){
        String pass = null;
        try {
            pass = edPassword.getEditText().getText().toString();
            if(pass.length()==0)
            {
                edPassword.setError("Mật khẩu không được để trống!");
                return null;
            }
            if(pass.length()<5 || pass.length()>20)
            {
                edPassword.setError("Mật khẩu có độ dài từ 5-20 kí tự!");
                return null;
            }

        }catch (Exception e)
        {
            edPassword.setError("Tên đăng nhập không hợp lệ");
        }
        return pass;
    }

    public boolean checkRePassword(){
        String pass= null;
        String repass= null;
        try {
            pass = edPassword.getEditText().getText().toString();
            repass = edRePassword.getEditText().getText().toString();
            if(repass.length()==0)
            {
                edRePassword.setError("Mật khẩu không trùng khớp!");
                return false;
            }
            if(!repass.equals(pass))
            {
                edRePassword.setError("Mật khẩu không trùng khớp!");
                return false;
            }


        }catch (Exception e)
        {
            edRePassword.setError("Mật khẩu không trùng khớp!");
        }
        return true;
    }
}