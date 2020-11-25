package com.example.quizpoly.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quizpoly.R;
import com.example.quizpoly.Models.User;
import com.example.quizpoly.DAO.UserDAO;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {

    CircleImageView ivAvatar, ivEditAvatar;
    TextInputLayout edPassword,edRePassword,edDisplayname;
    TextView tvUsername;
    UserDAO userDAO;
    String username;
    Button btnGallery, btnCamera;
    Bitmap selectedBitmap;
    Uri imageUri;
    String stringUri,password;
    private static final int SELECT_PHOTO_GALLERY = 100, SELECT_PHOTO_CAMERA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivEditAvatar = findViewById(R.id.ivEditAvatar);
        edPassword = findViewById(R.id.edPasswordEdit);
        edRePassword = findViewById(R.id.edRePasswordEdit);
        edDisplayname = findViewById(R.id.edDisplayname);
        tvUsername = findViewById(R.id.tvUsernameEdit);
        userDAO = new UserDAO(this);

        Intent intent =  getIntent();
        username = intent.getStringExtra("username");
        tvUsername.setText("Tên tài khoản: "+username);
        User u = userDAO.checkUserExist(username,"username=?");
        String displayName = u.getDisplayname();

        if(displayName != null) {
            edDisplayname.getEditText().setText(displayName);
        }
        stringUri = u.getStringUri();

        if(stringUri != null) {
            Glide.with(this)
                    .load(stringUri)
                    .into(ivAvatar);
        }
        password = u.getPassword();

        if(password != null) {
            edPassword.getEditText().setText(password);
            edRePassword.getEditText().setText(password);
        }

        ivEditAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        EditActivity.this, android.R.style.Theme_Material_Dialog_Alert);

                LayoutInflater layoutInflater = getLayoutInflater();
                View viewD = layoutInflater.inflate(R.layout.dialog_choose_pic, null);

                btnGallery = viewD.findViewById(R.id.btnGallery);
                btnCamera = viewD.findViewById(R.id.btnCamera);

                alertDialog.setView(viewD);

                final AlertDialog dialog = alertDialog.create();
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                btnCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPhotoFromCam();
                        dialog.dismiss();
                    }
                });
                btnGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPhotoFromGal();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public void suathongtin(View view) {
        String displayname = checkDisplayName();
        String password = checkPassword();
        boolean repass = checkRePassword();
        if(displayname != null && password != null && repass)
        {
            User u = userDAO.checkUserExist(username,"username=?");
            u.setDisplayname(displayname);
            u.setPassword(password);
            if(selectedBitmap != null){
                imageUri = saveImage(selectedBitmap);
                u.setStringUri(String.valueOf(imageUri));
            }else{
                u.setStringUri(null);
            }

            try {
                if(userDAO.updateUser(u)>0)
                {
                    Intent i = new Intent(EditActivity.this, MenuActivity.class);
                    i.putExtra("username",u.getUsername());
                    startActivity(i);
                    finish();
                }

            }catch (Exception e)
            {
                Log.e("Edit",e.getMessage());
            }
        }
    }

    private void getPhotoFromGal(){
        if(ContextCompat.checkSelfPermission(EditActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(EditActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(photoPickerIntent, SELECT_PHOTO_GALLERY);

        }else{
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },888);
        }

    }

    private void getPhotoFromCam(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(i, SELECT_PHOTO_CAMERA);
        }else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 999);
        }
    }

    private Uri saveImage(Bitmap bitmap){
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        File storageDir = wrapper.getDir(Environment.DIRECTORY_PICTURES, MODE_PRIVATE);
        File file = new File(storageDir, UUID.randomUUID() + ".jpg");

        try{
            OutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Uri.parse(file.getAbsolutePath());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO_GALLERY:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this.getContentResolver(),selectedImage);
                        Glide.with(this).load(selectedBitmap).into(ivAvatar);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this,"Error!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case SELECT_PHOTO_CAMERA:
                if(resultCode == RESULT_OK){
                    selectedBitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    Glide.with(this).load(selectedBitmap).into(ivAvatar);
                }
        }
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
            edPassword.setError("Mật khẩu không hợp lệ");
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

    public String checkDisplayName(){
        String name = null;
        try {
            name = edDisplayname.getEditText().getText().toString();
            if(name.length()==0)
            {
                edDisplayname.setError("Tên hiển thị không được để trống!");
                return null;
            }
            if(name.length()<5 || name.length()>20)
            {
                edDisplayname.setError("Tên hiển thị có độ dài từ 5-20 kí tự!");
                return null;
            }

        }catch (Exception e)
        {
            edDisplayname.setError("Tên hiển thị không hợp lệ");
        }
        return name;
    }


}