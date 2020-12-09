package vn.poly.quiz.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import vn.poly.quiz.LoadingDialog;
import vn.poly.quiz.R;
import vn.poly.quiz.models.User;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class OneTimeActivity extends AppCompatActivity {

    Button btnStart, btnGallery, btnCamera;
    Bitmap selectedBitmap;
    TextInputLayout edName;
    CircleImageView ivAvatar, ivEditAvatar;
    String username, password;
    StorageReference mStorageRef;
    DatabaseReference rootRef;
    Uri imageUri;
    LoadingDialog loadingDialog;
    User u;
    private static final int SELECT_PHOTO_GALLERY = 100, SELECT_PHOTO_CAMERA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_time);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        btnStart = findViewById(R.id.btnStart);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivEditAvatar = findViewById(R.id.ivEditAvatar);
        edName = findViewById(R.id.edName);

        loadingDialog = new LoadingDialog(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();

        btnStart.setOnClickListener(view -> start());

        ivEditAvatar.setOnClickListener(view -> changeImage());
    }

    private void start(){
        String displayName = checkDisplayName();
        if(displayName != null){
            loadingDialog.showLoadingDialog();
            if(imageUri != null){
                StorageReference filepath = mStorageRef.child("Images").child(username);
                filepath.putFile(imageUri).addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) -> {
                    Task<Uri> result = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl();
                    result.addOnSuccessListener(uri -> {
                        String photoStringLink = uri.toString();
                        u = new User(username, password, displayName, photoStringLink,
                                username + "_" + password);
                        changeOtherInfo();
                    });
                });
            }else{
                u = new User(username, password, displayName, null,
                        username + "_" + password);
                changeOtherInfo();
            }
        }
    }

    private void changeOtherInfo(){
        DatabaseReference userRef = rootRef.child("Users").child(username);
        userRef.setValue(u)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.hideLoadingDialog();
                    Intent intent = new Intent(OneTimeActivity.this,
                            MenuActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    loadingDialog.hideLoadingDialog();
                    Toast.makeText(OneTimeActivity.this,
                            "Fail 2", Toast.LENGTH_SHORT).show();
                });
    }

    private void changeImage(){
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
    }

    private void getPhotoFromGal(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, SELECT_PHOTO_GALLERY);
    }

    private void getPhotoFromCam(){

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(i, SELECT_PHOTO_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, 999);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 888);
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String checkDisplayName(){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO_GALLERY:
                if(resultCode == RESULT_OK){
                    imageUri = imageReturnedIntent.getData();
                    try {
                        selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this.getContentResolver(),imageUri);
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

                    imageUri = getImageUri(getApplicationContext(), selectedBitmap);
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
        }else if(requestCode == 888){
            if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(i, SELECT_PHOTO_CAMERA);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, 999);
                }
            }else{
            Toast.makeText(this, getString(R.string.one_time_camera_message_fail),
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
}