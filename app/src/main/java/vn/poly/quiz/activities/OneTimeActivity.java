package vn.poly.quiz.activities;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import vn.poly.quiz.R;
import vn.poly.quiz.models.User;
import vn.poly.quiz.dao.UserDAO;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class OneTimeActivity extends AppCompatActivity {

    Button btnStart, btnGallery, btnCamera;
    Bitmap selectedBitmap;
    TextInputLayout edName;
    CircleImageView ivAvatar, ivEditAvatar;
    String id;
    Uri imageUri;
    UserDAO userDAO;
    private static final int SELECT_PHOTO_GALLERY = 100, SELECT_PHOTO_CAMERA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_time);

        Intent intent =  getIntent();
        id = intent.getStringExtra("id");

        btnStart = findViewById(R.id.btnStart);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivEditAvatar = findViewById(R.id.ivEditAvatar);
        edName = findViewById(R.id.edName);
        userDAO = new UserDAO(this);

        btnStart.setOnClickListener(view -> {

            String name = checkName();
            if(name != null) {
                User u = userDAO.checkUserExist(id,"id=?");
                u.setDisplayName(name);

                if(selectedBitmap != null){
                    imageUri = saveImage(selectedBitmap);
                    u.setStringUri(String.valueOf(imageUri));
                }else{
                    u.setStringUri(null);
                }

                try {
                    if(userDAO.updateUser(u)>0) {
                        Intent i = new Intent(OneTimeActivity.this,
                                MenuActivity.class);
                        i.putExtra("username",u.getUsername());
                        startActivity(i);
                        finish();
                    }

                }catch (Exception e) {
                    Log.e("Onetime",e.getMessage());
                }
            }
        });

        ivEditAvatar.setOnClickListener(view -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    OneTimeActivity.this, android.R.style.Theme_Material_Dialog_Alert);

            LayoutInflater layoutInflater = getLayoutInflater();
            View viewD = layoutInflater.inflate(R.layout.dialog_choose_pic, null);

            btnGallery = viewD.findViewById(R.id.btnGallery);
            btnCamera = viewD.findViewById(R.id.btnCamera);

            alertDialog.setView(viewD);

            final AlertDialog dialog = alertDialog.create();
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            btnCamera.setOnClickListener(view12 -> {
                getPhotoFromCam();
                dialog.dismiss();
            });
            btnGallery.setOnClickListener(view1 -> {
                getPhotoFromGal();
                dialog.dismiss();
            });
        });
    }

    public String checkName(){
        String name = null;
        try {
            name = Objects.requireNonNull(edName.getEditText()).getText().toString();
            if(name.length()==0)
            {
                edName.setError(getString(R.string.display_name_error_null));
                return null;
            }
            if(name.length()<5 || name.length()>20)
            {
                edName.setError(getString(R.string.display_name_error_length));
                return null;
            }

        }catch (Exception e)
        {
            edName.setError(getString(R.string.display_name_error));
        }
        return name;
    }

    private void getPhotoFromGal(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, SELECT_PHOTO_GALLERY);
    }

    private void getPhotoFromCam(){

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 999){
            if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, SELECT_PHOTO_CAMERA);
            }else{
                Toast.makeText(this, getString(R.string.one_time_camera_message_fail),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}