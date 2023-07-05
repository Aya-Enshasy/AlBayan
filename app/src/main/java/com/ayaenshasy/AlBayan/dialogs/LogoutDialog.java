package com.ayaenshasy.AlBayan.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.ayaenshasy.AlBayan.R;

public class LogoutDialog extends DialogFragment {

    public interface LogoutDialogListener {
        void onLogoutConfirmed();
        void onLogoutCancelled();
    }

    private LogoutDialogListener listener;

    public void setListener(LogoutDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("تسجيل الخروج")
                .setMessage("هل انت متاكد من تسجيل الخروج؟").setIcon(R.drawable.ic_quran_icon)
                .setPositiveButton("تسجيل خروج", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onLogoutConfirmed();
                        }
                    }
                })
                .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            dismiss();
                            listener.onLogoutCancelled();
                        }
                    }
                });
        return builder.create();
    }
}

