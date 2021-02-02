package com.calderon.mymoney.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.DatePicker;
import android.widget.TextView;

import com.calderon.mymoney.models.Registro;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Util {

    public static void saveData(SharedPreferences preferences, List<Registro> registros) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(registros);
        editor.putString("data", json);
        editor.apply();
    }

    public static List<Registro> loadData(SharedPreferences preferences, List<Registro> reg) {
        Gson gson = new Gson();
        String json = preferences.getString("data" , null);
        Type type = new TypeToken<ArrayList<Registro>>() {
        }.getType();
        reg = gson.fromJson(json, type);

        if (reg == null)
            reg = new ArrayList<>();
        return  reg;
    }

    public static void setDate(Calendar c,Context context, final TextView textView)
        {if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            int dia = c.get(Calendar.DAY_OF_MONTH);
            int mes = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int motnhOfYear, int dayOfMonth) {
                    textView.setText(String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, motnhOfYear, year));
                }
            }, year, mes, dia);
            datePickerDialog.show();
        }
    }
}
