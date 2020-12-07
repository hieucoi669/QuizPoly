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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import vn.poly.quiz.LoadingDialog;
import vn.poly.quiz.R;
import vn.poly.quiz.models.Quiz;
import vn.poly.quiz.models.User;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.poly.quiz.sound.App;
import vn.poly.quiz.sound.MusicManager;

public class EditActivity extends AppCompatActivity {

    CircleImageView ivAvatar, ivEditAvatar;
    TextInputLayout edPassword,edRePassword, edDisplayName;
    TextView tvUsername;
    String username, displayName, password, imageURL;
    Button btnGallery, btnCamera, btnSua;
    Bitmap selectedBitmap;
    Uri imageUri;
    StorageReference mStorageRef;
    DatabaseReference rootRef;
    Quiz quiz;
    User u;
    LoadingDialog loadingDialog;
    private static final int SELECT_PHOTO_GALLERY = 100, SELECT_PHOTO_CAMERA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ivAvatar = findViewById(R.id.ivAvatar);
        ivEditAvatar = findViewById(R.id.ivEditAvatar);
        edPassword = findViewById(R.id.edPasswordEdit);
        edRePassword = findViewById(R.id.edRePasswordEdit);
        edDisplayName = findViewById(R.id.edDisplayName);
        tvUsername = findViewById(R.id.tvUsernameEdit);
        btnSua = findViewById(R.id.btnSuaThongTin);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();

        loadingDialog = new LoadingDialog(this);

        Intent intent =  getIntent();
        username = intent.getStringExtra("username");
        displayName = intent.getStringExtra("displayName");
        password = intent.getStringExtra("password");
        imageURL = intent.getStringExtra("imageURL");

        tvUsername.setText(getString(R.string.edit_hint_username, username));

        if(imageURL != null) {
            Glide.with(this)
                    .load(imageURL)
                    .into(ivAvatar);
        }

        if(displayName != null) {
            Objects.requireNonNull(edDisplayName.getEditText()).setText(displayName);
        }

        if(password != null) {
            Objects.requireNonNull(edPassword.getEditText()).setText(password);
            Objects.requireNonNull(edRePassword.getEditText()).setText(password);
        }

        ivEditAvatar.setOnClickListener(view -> {
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

            btnCamera.setOnClickListener(view12 -> {
                getPhotoFromCam();
                dialog.dismiss();
            });
            btnGallery.setOnClickListener(view1 -> {
                getPhotoFromGal();
                dialog.dismiss();
            });
        });

        btnSua.setOnClickListener(view -> {
            App.getMusicPlayer().play(EditActivity.this,
                    MusicManager.buttonClick, mediaPlayer -> {

            });
            suaThongTin();
        });
    }

    public void suaThongTin() {
        String displayName = checkDisplayName();
        String password = checkPassword();
        boolean rePass = checkRePassword();

        if(displayName != null && password != null && rePass){
            loadingDialog.showLoadingDialog();
            if(imageUri != null){
                StorageReference filepath = mStorageRef.child("Images").child(username);
                filepath.putFile(imageUri)
                        .addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) -> {

                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(uri -> {
                        String photoStringLink = uri.toString();
                        changeImageURLInQuiz(photoStringLink);

                        u = new User(username, password, displayName, photoStringLink,
                                username + "_" + password);
                        changeOtherInfo(displayName);
                    });
                });
            }else{
                u = new User(username, password, displayName, imageURL,
                        username + "_" + password);
                changeOtherInfo(displayName);
            }
        }
    }

    private void changeOtherInfo(String displayName) {
        DatabaseReference userRef = rootRef.child("Users").child(username);
        userRef.setValue(u)
                .addOnSuccessListener(aVoid -> {
                    changeDisplayNameInQuiz(displayName);
                    loadingDialog.hideLoadingDialog();
                    Intent intent = new Intent(EditActivity.this,
                            MenuActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    loadingDialog.hideLoadingDialog();
                    Toast.makeText(EditActivity.this,
                            "Fail 2", Toast.LENGTH_SHORT).show();
                });
    }

    private void changeDisplayNameInQuiz(String displayName) {
        if(!displayName.equals(this.displayName)){
            Query query = rootRef.child("Quiz").orderByChild("username").equalTo(username);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            quiz = data.getValue(Quiz.class);
                            quiz.setDisplayName(displayName);
                            String key = quiz.getKey();
                            rootRef.child("Quiz").child(key).setValue(quiz);
                        }
                    }
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void changeImageURLInQuiz(String imageURL) {
        Query query = rootRef.child("Quiz").orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        quiz = data.getValue(Quiz.class);
                        quiz.setImageURL(imageURL);
                        String key = quiz.getKey();
                        rootRef.child("Quiz").child(key).setValue(quiz);
                    }
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void getPhotoFromGal(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, SELECT_PHOTO_GALLERY);
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

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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

    public String checkDisplayName(){
        String name = null;
        try {
            name = Objects.requireNonNull(edDisplayName.getEditText()).getText().toString();
            if(name.length()==0)
            {
                edDisplayName.setError(getString(R.string.display_name_error_null));
                return null;
            }
            if(name.length()<5 || name.length()>20)
            {
                edDisplayName.setError(getString(R.string.display_name_error_length));
                return null;
            }

        }catch (Exception e)
        {
            edDisplayName.setError(getString(R.string.display_name_error));
        }
        return name;
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
}