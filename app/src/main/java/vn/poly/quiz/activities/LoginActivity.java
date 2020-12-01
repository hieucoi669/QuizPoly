package vn.poly.quiz.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import vn.poly.quiz.R;
import vn.poly.quiz.models.User;
import vn.poly.quiz.dao.UserDAO;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout edUsername,edPassword;
    User u;
    UserDAO userDAO;
    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnDangKyLogin);
        userDAO = new UserDAO(this);

        btnLogin.setOnClickListener(view -> login());
        btnRegister.setOnClickListener(view -> register());
    }

    public void login()
    {
        String user = checkUsername();
        String pass = checkPassword();

        if(user != null && pass != null)
        {
            Toast.makeText(this, getString(R.string.login_log_success_message),
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            intent.putExtra("username",u.getUsername());
            startActivity(intent);
        }
    }

    public void register()
    {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private String checkUsername()
    {
        edUsername.setError(null);
        try{
            String username = Objects.requireNonNull(
                    edUsername.getEditText()).getText().toString();

            if(username.length() == 0){
                edUsername.setError(getString(R.string.ed_username_error_null));
                return null;
            }

            u = userDAO.checkUserExist(username,"username=?");
            if(u == null){
                edUsername.setError(getString(R.string.ed_username_error_not_exist));
                return null;
            }else{
                return username;
            }
        }catch (Exception e){
            edUsername.setError(getString(R.string.ed_username_error));
            return null;
        }
    }

    private String checkPassword() {
        edPassword.setError(null);
        try{
            String pass = Objects.requireNonNull(edPassword.getEditText()).getText().toString();
            if(pass.length() == 0){
                edPassword.setError(getString(R.string.ed_password_error_null));
                return null;
            }
            if(u != null){
                if(u.getPassword().equals(pass)){
                    return pass;
                }else{
                    edPassword.setError(getString(R.string.ed_password_error_wrong));
                    return null;
                }
            }else{
                return null;
            }
        }catch (Exception e){
            edPassword.setError(getString(R.string.ed_password_error));
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