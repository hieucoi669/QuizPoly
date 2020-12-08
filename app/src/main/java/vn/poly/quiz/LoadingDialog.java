package vn.poly.quiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import android.os.Handler;
import android.widget.Button;

public class LoadingDialog {
    final Activity activity;
    AlertDialog dialog, dialogNoInternet;
    boolean show;
    Button btnConfirm;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
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
        show = true;

        new Handler().postDelayed(() -> {
            if(show){
                if(!new CheckInternet().isNetworkAvailable(activity)){
                    noInternetDialog();
                }
            }
        },5000);

    }

    public void hideLoadingDialog(){
        show = false;
        dialog.dismiss();
    }

    private void noInternetDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity, android.R.style.Theme_Material_Dialog_Alert);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_no_internet_connection, null);
        btnConfirm = view.findViewById(R.id.btnConfirmNoInternet);
        alertDialog.setView(view);
        alertDialog.setCancelable(false);

        dialogNoInternet = alertDialog.create();
        dialogNoInternet.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogNoInternet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogNoInternet.show();

        btnConfirm.setOnClickListener(view1 -> {
            dialogNoInternet.dismiss();
            dialog.dismiss();
//            activity.finish();
            activity.finishAffinity();
            System.exit(0);
        });
    }
}
