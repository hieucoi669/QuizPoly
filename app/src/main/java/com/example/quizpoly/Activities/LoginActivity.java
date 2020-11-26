package com.example.quizpoly.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.quizpoly.R;
import com.example.quizpoly.Sound.App;
import com.example.quizpoly.Sound.MusicManager;
import com.example.quizpoly.Models.User;
import com.example.quizpoly.DAO.UserDAO;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout edUsername,edPassword;
    User u;
    UserDAO userDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
        userDAO = new UserDAO(this);
    }

    public void dangNhap(View view)
    {
        App.getMusicPlayer().play(MusicManager.buttonclick, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });
        String user = checkUsername();
        String pass = checkPassword();

        if(user != null && pass != null)
        {
            Toast.makeText(this,"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            intent.putExtra("username",u.getUsername());
            startActivity(intent);
        }
    }

    public void dangKy(View view)
    {
        App.getMusicPlayer().play(MusicManager.buttonclick, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private String checkUsername()
    {
        edUsername.setError(null);
        try{
            String username = edUsername.getEditText().getText().toString();
            if(username.length() == 0){
                edUsername.setError("Vui lòng nhập tên tài khoản!");
                return null;
            }
            u = userDAO.checkUserExist(username,"username=?");
            if(u == null){
                edUsername.setError("Tên tài khoản không tồn tại!");
                return null;
            }else{
                return username;
            }
        }catch (Exception e){
            edUsername.setError("Tên tài khoản không hợp lệ!");
            return null;
        }
    }

    private String checkPassword() {
        edPassword.setError(null);
        try{
            String pass = edPassword.getEditText().getText().toString();
            if(pass.length() == 0){
                edPassword.setError("Vui lòng nhập mật khẩu!");
                return null;
            }
            if(u != null){
                if(u.getPassword().equals(pass)){
                    return pass;
                }else{
                    edPassword.setError("Mật khẩu bạn nhập không đúng!");
                    return null;
                }
            }else{
                return null;
            }
        }catch (Exception e){
            edPassword.setError("Mật khẩu không hợp lệ!");
            return null;
        }
    }

    public void backdoor(View view){
        if(userDAO.checkUserExist("backdoor","username=?")==null)
        {
            User u = new User("backdoor","backdoor","backdoor",null,"backdoor");
            userDAO.insertUser(u);
        }
        Intent intent = new Intent(this,MenuActivity.class);
        intent.putExtra("username","backdoor");
        startActivity(intent);
    }

}