package vn.poly.quiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import vn.poly.quiz.activities.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

public class LoadingDialog {
    final Activity activity;
    AlertDialog dialog, dialogNoInternet;
    boolean dialogShow;
    Button btnConfirm;
    TextView tvDialogTitle, tvDialogMessage;
    final CheckInternet checkInternet;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
        checkInternet = new CheckInternet();
    }

    public void showLoadingDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity, android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_loading, null);
        alertDialog.setView(view);
        alertDialog.setCancelable(false);

        dialog = alertDialog.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialogShow = true;

        new Handler().postDelayed(() -> {
            if(dialogShow){
                checkInternet();
            }
        },5000);

    }

    public void hideLoadingDialog(){
        if(dialogShow){
            dialogShow = false;
            dialog.dismiss();
        }
    }

    public void checkInternet(){
        if(!checkInternet.isNetworkAvailable(activity)){
            noInternetDialog();
        }
    }

    private void noInternetDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity, android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_no_internet_connection, null);

        tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        tvDialogMessage = view.findViewById(R.id.tvDialogMessage);
        btnConfirm = view.findViewById(R.id.btnConfirmNoInternet);

        tvDialogTitle.setText(R.string.dialog_no_internet_title);
        tvDialogMessage.setText(R.string.dialog_no_internet_message);

        alertDialog.setView(view);
        alertDialog.setCancelable(false);

        dialogNoInternet = alertDialog.create();
        dialogNoInternet.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogNoInternet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogNoInternet.show();

        btnConfirm.setOnClickListener(view1 -> {
            dialogNoInternet.dismiss();
            dialog.dismiss();
            clearPref();
            Intent i = new Intent(activity, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(i);
            activity.finish();
        });
    }

    public void uniqueLoginDialog(Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity, android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_no_internet_connection, null);

        tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        tvDialogMessage = view.findViewById(R.id.tvDialogMessage);
        btnConfirm = view.findViewById(R.id.btnConfirmNoInternet);

        tvDialogTitle.setText(R.string.dialog_unique_login_title);
        tvDialogMessage.setText(R.string.dialog_unique_login_message);

        alertDialog.setView(view);
        alertDialog.setCancelable(false);

        dialogNoInternet = alertDialog.create();
        dialogNoInternet.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogNoInternet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogNoInternet.show();

        btnConfirm.setOnClickListener(view1 -> {
            dialogNoInternet.dismiss();
            clearPref();
            Intent i = new Intent(activity, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(i);
            activity.finish();
        });
    }

    private void clearPref(){
        SharedPreferences pref = activity.getSharedPreferences("USER_FILE",MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.apply();
    }

}
