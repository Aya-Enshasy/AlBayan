package com.ayaenshasy.bayan.base;

import static android.content.Context.MODE_PRIVATE;

import static com.ayaenshasy.bayan.utils.AppPreferences.PREF_NAME;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ayaenshasy.bayan.R;
import com.ayaenshasy.bayan.api.ApiInterface;
import com.ayaenshasy.bayan.api.Creator;
import com.ayaenshasy.bayan.utils.AppPreferences;

import java.util.Locale;


public abstract class BaseFragment extends Fragment {

    public Dialog loader_dialog ;
    public ApiInterface api;
       public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
      AppPreferences preferences;
    Context context = getActivity();
     @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loader_dialog = new Dialog(getActivity());

         sp = getActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = sp.edit();
         api =  Creator.getRetrofitInstance();

        preferences = AppPreferences.getInstance(getActivity());


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    protected void loaderDialog() {
        loader_dialog.setContentView(R.layout.loader_dialog);
        loader_dialog.setCancelable(false);
        loader_dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
        loader_dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        loader_dialog.show();
    }

 }
