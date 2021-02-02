package com.calderon.mymoney.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.calderon.mymoney.models.Registro;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.calderon.mymoney.R;

import java.util.List;

import static com.calderon.mymoney.utils.Util.loadData;

public class SaveActivity extends AppCompatActivity {

    private TextView multi;
    private List<Registro> list;
    private SharedPreferences preferences;

    private String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("DATA",MODE_PRIVATE);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        multi = findViewById(R.id.editTextTextMultiLine);
        list = loadData(preferences,list);

        for (Registro registro : list) {
            text += registro.toString()+"\n";
        }
        multi.setText(text);
    }
}