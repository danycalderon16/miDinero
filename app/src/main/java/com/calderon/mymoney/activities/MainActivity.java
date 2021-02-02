package com.calderon.mymoney;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private List<Registro> list;
    private RecyclerView recyclerView;
    private Adaptador adaptador;
    private RecyclerView.LayoutManager manager;

    private Calendar c = Calendar.getInstance();

    private FloatingActionButton fab;
    private TextView tvTotal;

    private SharedPreferences preferences;

    private int click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("DATA",MODE_PRIVATE);
        sendBind();
        sendRecycler();
    }

    private void sendRecycler() {
        list = loadData(preferences,list);

        manager = new LinearLayoutManager(this);
        adaptador = new Adaptador(list, this);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adaptador);

        if(list.isEmpty()) tvTotal.setText("$0");
        else tvTotal.setText(String.format(Locale.getDefault(), "$%.2f",list.get(list.size()-1).getTotal()));
    }

    private void sendBind(){
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.rv);
        tvTotal = findViewById(R.id.textView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForm(v);
            }
        });
        tvTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click++;
                if(click ==3 ) {
                    float numeroNuevo = 0;
                    if (adaptador.getItemCount() != 0)
                        numeroNuevo = list.get(list.size() - 1).getTotal();
                    tvTotal.setText(String.format(Locale.getDefault(), "$%.2f", numeroNuevo));
                    click = 0;
                }
            }
        });

    }

    private void showForm(final View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater  = getLayoutInflater();
        View v = inflater.inflate(R.layout.form,null);

        // Create the alert dialog

        builder.setCancelable(false);
        builder.setView(v);

        final AlertDialog dialog = builder.create();

        final TextView fecha = v.findViewById(R.id.fFecha);
        final EditText capital = v.findViewById(R.id.fCap);
        final EditText ahorrado = v.findViewById(R.id.fAhorrado);
        final EditText invertido = v.findViewById(R.id.fInvertido);
        Button button = v.findViewById(R.id.fAdd);

        fecha.setText(c.get(Calendar.DAY_OF_MONTH)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR));

        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v.getContext(),fecha);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float cap;
                float aho;
                float inv;
                float tot;
                try {
                    cap = Integer.parseInt(capital.getText().toString());
                    aho = Integer.parseInt(ahorrado.getText().toString());
                    inv = Integer.parseInt(invertido.getText().toString());
                    tot = cap + aho + inv;
                }catch (NumberFormatException np){
                    Toast.makeText(MainActivity.this,"LLene todos los campos\n"+np.getMessage(),Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }


                final Registro r = new Registro(tot,fecha.getText().toString(),cap,aho,inv);
                list.add(r);
                saveData(preferences,list);
                Toast.makeText(MainActivity.this, "Agregado", Toast.LENGTH_SHORT).show();
                recyclerView.setScrollingTouchSlop(list.size());
                adaptador.notifyItemInserted(list.size());
                tvTotal.setText(String.format(Locale.getDefault(),"$%.2f",list.get(list.size()-1).getTotal()));
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();

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

    public static void saveData(SharedPreferences preferences, List<Registro> registros) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(registros);
        editor.putString("data", json);
        editor.apply();
    }

    public void setDate(Context context, final TextView textView){if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        int dia = c.get(Calendar.DAY_OF_MONTH);
        int mes = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int motnhOfYear, int dayOfMonth) {
                textView.setText(String.format(Locale.getDefault(),"%d/%d/%d",dayOfMonth, motnhOfYear , year));
            }
        }, year, mes, dia);
        datePickerDialog.show();
    }}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_chart){
            Intent intent = new Intent(this,ChartActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

