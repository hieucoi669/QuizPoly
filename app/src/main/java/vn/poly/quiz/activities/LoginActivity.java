package vn.poly.quiz.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import vn.poly.quiz.LoadingDialog;
import vn.poly.quiz.R;
import vn.poly.quiz.models.User;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout edUsername,edPassword;
    User u;
    Button btnLogin, btnRegister;
    DatabaseReference rootRef;
    LoadingDialog loadingDialog;
    CheckBox chkRemember;
    SharedPreferences pref;
    Query query;
    ValueEventListener valueEventListener;
    boolean isListening = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnDangKyLogin);
        chkRemember = findViewById(R.id.chkRemember);

        loadingDialog = new LoadingDialog(this);
        pref = getSharedPreferences("USER_FILE",MODE_PRIVATE);

        rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        autoLogin();
        btnLogin.setOnClickListener(view -> login());
        btnRegister.setOnClickListener(view -> register());
    }

    public void login() {
        String user = checkUsername();
        String pass = checkPassword();
        if(user != null && pass != null){
            checkLogin(user+"_"+pass);
        }
    }

    public void register() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private String checkUsername() {
        edUsername.setError(null);
        try{
            String username = Objects.requireNonNull(
                    edUsername.getEditText()).getText().toString();

            if(username.length() == 0){
                edUsername.setError(getString(R.string.ed_username_error_null));
                return null;
            }
            return username;
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
            return pass;
        }catch (Exception e){
            edPassword.setError(getString(R.string.ed_password_error));
            return null;
        }
    }

    private void setAutoLogin(String auth){
        SharedPreferences.Editor editor = pref.edit();

        if(chkRemember.isChecked()){
            editor.putString("auth", auth);
            editor.putBoolean("remember", true);
        }else{
            editor.clear();
        }
        editor.apply();
    }

    private void autoLogin(){
        boolean chk = pref.getBoolean("remember",false);
        if(chk) {
            String auth = pref.getString("auth", null);
            checkLogin(auth);
        }
    }

    private void checkLogin(String auth){
        loadingDialog.showLoadingDialog();
        query = rootRef.orderByChild("auth").equalTo(auth);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(isListening){
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            u = data.getValue(User.class);
                        }
                        setAutoLogin(auth);
                        loadingDialog.hideLoadingDialog();
                        Intent intent;
                        if(u.getDisplayName() == null){
                            intent = new Intent(LoginActivity.this,
                                    OneTimeActivity.class);
                            intent.putExtra("username", u.getUsername());
                            intent.putExtra("password", u.getPassword());
                        }else {
                            intent = new Intent(LoginActivity.this, MenuActivity.class);
                            intent.putExtra("username", u.getUsername());
                            intent.putExtra("user", u);
                        }
                        startActivity(intent);

                    } else {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.apply();
                        loadingDialog.hideLoadingDialog();
                        Toast.makeText(LoginActivity.this,
                                getString(R.string.ed_password_error_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this,
                        "DOC", Toast.LENGTH_SHORT).show();
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    protected void onDestroy() {
        isListening = false;
        super.onDestroy();
    }
}