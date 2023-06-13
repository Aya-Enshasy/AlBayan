package com.ayaenshasy.bayan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// كلاس يحتوي على دوال متنوع تم استخدامها داخل التطبيق من أجل ترتيب الكود
public class Methods {
    // لإزالة ال view
    public static void goneView(View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.GONE);
        }
    }

    public static String getText(EditText editText) {
        return editText.getText().toString();
    }

    // لإظهار ال view
    public static void visibleView(View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.VISIBLE);
        }
    }

    // لإخفاء ال view دون إزالتها
    public static void invisibleView(View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.INVISIBLE);
        }
    }

    // لتعطيل أو تفعيل ال view القمية المنطقية المعطاة هي التي تحدد
    public static void enableView(boolean state, View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setEnabled(state);
        }
    }

    public static boolean isEditTextEmpty(EditText... editTexts) {
        boolean isEmpty = false;

        for (int i = 0; i < editTexts.length; i++) {
            if (TextUtils.isEmpty(editTexts[i].getText().toString())) {
                isEmpty = true;
            }
        }
        return isEmpty;
    }

    public static boolean isTextInputEmpty(TextInputEditText... editTexts) {
        boolean isEmpty = false;

        for (int i = 0; i < editTexts.length; i++) {
            if (TextUtils.isEmpty(editTexts[i].getText().toString())) {
                isEmpty = true;
                editTextEmptyError(editTexts[i]);
            }
        }
        return isEmpty;
    }

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static void setLangArabic() {
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Resources resources = MyApplication.getContext().getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static void editTextEmptyError(EditText... editTexts) {
        for (EditText edittext : editTexts) {

        }
    }

    public static void clearTextInputLayoutError(TextInputEditText... editTexts) {
        for (TextInputEditText editText : editTexts) {
            editText.setError(null);
        }
    }

    public static void clearEditText(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setText("");
        }
    }

    public static String formatDate(Date date) {
        String pattern = "yyyy/MM/dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(date);
    }

    public static String formatJsonDate(Date date) {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(date);
    }

    public static <T> void addToList(List<T> list, T... ts) {
        for (T t : ts) {
            list.add(t);
        }
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static Calendar getCalendarIgnoreTime() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        return calendar;
    }

    public static Calendar getCalendar(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        return calendar;
    }

    public static <T extends RecyclerView.ViewHolder> void  prepareRecycler(RecyclerView recycler , RecyclerView.Adapter<T> adapter , boolean hasFixedSize , RecyclerView.LayoutManager manager){
        recycler.setAdapter(adapter);
        recycler.setHasFixedSize(hasFixedSize);
        recycler.setLayoutManager(manager);
    }
}
