package vn.poly.quiz.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import vn.poly.quiz.LoadingDialog;
import vn.poly.quiz.R;
import vn.poly.quiz.models.User;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout edUsername,edPassword,edRePassword;
    Button btnDangKy,btnBack;
    DatabaseReference rootRef;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
        edRePassword = findViewById(R.id.edRePassword);
        btnDangKy = findViewById(R.id.btnDangKy);
        btnBack = findViewById(R.id.btnBack);

        loadingDialog = new LoadingDialog(this);
        rootRef = FirebaseDatabase.getInstance().getReference();

        btnDangKy.setOnClickListener(v -> createUser());

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void createUser(){
        String username = checkUsername();
        String password = checkPassword();
        boolean rePass = checkRePassword();

        if(username != null && password != null && rePass){
            loadingDialog.showLoadingDialog();
            DatabaseReference userRef = rootRef.child("Users").child(username);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    if(!snapshot.exists()) {

                        User u = new User(username, password, username + "_" + password);

                        userRef.setValue(u)
                                .addOnSuccessListener(aVoid -> {
                                    loadingDialog.hideLoadingDialog();
                                    Intent intent = new Intent(SignUpActivity.this,
                                            OneTimeActivity.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("password", password);
                                    startActivity(intent);

                                })
                                .addOnFailureListener(e -> {
                                    loadingDialog.hideLoadingDialog();
                                    Toast.makeText(SignUpActivity.this,
                                            "Fail!", Toast.LENGTH_SHORT).show();
                                });

                    }else {
                        loadingDialog.hideLoadingDialog();
                        Toast.makeText(SignUpActivity.this,
                                getString(R.string.ed_username_error_exist),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                    Log.i("Firebase", databaseError.getMessage());
                }

            });
        }
    }

    public String checkUsername(){
        String name = null;

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