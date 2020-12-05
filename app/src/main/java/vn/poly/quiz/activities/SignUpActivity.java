package vn.poly.quiz.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import vn.poly.quiz.R;
import vn.poly.quiz.models.User;
import vn.poly.quiz.dao.UserDAO;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        btnDangKy.setOnClickListener(v -> {
            String id;
            String username = checkUsername();
            String password = checkPassword();
            id = UUID.randomUUID().toString();
            boolean rePass = checkRePassword();
            if(username != null && password != null && rePass)
            {
                User u = new User();
                u.setId(id);
                u.setUsername(username);
                u.setPassword(password);

                try {
                    if(userDAO.insertUser(u)>0)
                    {
                        Toast.makeText(SignUpActivity.this,
                                getString(R.string.sign_up_success_message),
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this,
                                OneTimeActivity.class);
                        intent.putExtra("id",id);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(SignUpActivity.this,
                                getString(R.string.sign_up_fail_message),
                                Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.i("AddUser", e.toString());
                }
                finish();
            }
            else{
                Toast.makeText(SignUpActivity.this,
                        getString(R.string.sign_up_fail_message),
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
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
            name = Objects.requireNonNull(edUsername.getEditText()).getText().toString();
            if(name.length()==0)
            {
                edUsername.setError(getString(R.string.ed_username_error_null));
                return null;
            }
            if(name.length()<5 || name.length()>20)
            {
                edUsername.setError(getString(R.string.ed_username_error_length));
                return null;
            }
            for(int i=0; i<nameList.size(); i++){
                if(name.equalsIgnoreCase(nameList.get(i))){
                    edUsername.setError(getString(R.string.ed_username_error_exist));
                    return null;
                }
            }

        }catch (Exception e)
        {
            edUsername.setError(getString(R.string.ed_username_error));
        }
        return name;
    }

    public String checkPassword(){
        String pass = null;
        try {
            pass = Objects.requireNonNull(edPassword.getEditText()).getText().toString();
            if(pass.length()==0)
            {
                edPassword.setError(getString(R.string.ed_password_error_null));
                return null;
            }
            if(pass.length()<5 || pass.length()>20)
            {
                edPassword.setError(getString(R.string.ed_password_error_length));
                return null;
            }

        }catch (Exception e)
        {
            edPassword.setError(getString(R.string.ed_password_error));
        }
        return pass;
    }

    public boolean checkRePassword(){
        String pass;
        String rePass;
        try {
            pass = Objects.requireNonNull(edPassword.getEditText()).getText().toString();
            rePass = Objects.requireNonNull(edRePassword.getEditText()).getText().toString();
            if(rePass.length()==0)
            {
                edRePassword.setError(getString(R.string.ed_password_error_null));
                return false;
            }
            if(!rePass.equals(pass))
            {
                edRePassword.setError(getString(R.string.ed_password_error_match));
                return false;
            }


        }catch (Exception e)
        {
            edRePassword.setError(getString(R.string.ed_password_error));
        }
        return true;
    }
}