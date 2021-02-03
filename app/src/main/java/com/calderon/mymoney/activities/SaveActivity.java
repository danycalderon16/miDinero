package com.calderon.mymoney.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.calderon.mymoney.models.Registro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.calderon.mymoney.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.calderon.mymoney.utils.Util.loadData;

public class SaveActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView multi;
    private FloatingActionButton fab;
    private List<Registro> list;
    private SharedPreferences preferences;

    private FirebaseFirestore db;

    private String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();

        preferences = getSharedPreferences("DATA",MODE_PRIVATE);
       fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        multi = findViewById(R.id.editTextTextMultiLine);
        list = loadData(preferences,list);

        for (Registro registro : list) {
            text += registro.toString()+"\n";
        }
        multi.setText(text);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab){
            addNewItem();
        }
    }

    private void addNewItem() {
        boolean flag = false;
        int i = list.size()-1;

        String id = list.get(i).getFecha()+"";
        String date[] = id.split("/");
        String id_collection = date[2]+date[1]+date[0];

        for (Registro registro : list) {

            Map<String, Object> map = new HashMap<>();
            map.put("fecha", registro.getFecha());
            map.put("total", registro.getTotal());
            map.put("ahorrado", registro.getAhorrado());
            map.put("capital", registro.getCapital());
            map.put("invertido", registro.getInvertido());

            String doc = registro.getFecha()+"";
            String fecha[] = doc.split("/");
            StringBuilder id_doc;

            id_doc = new StringBuilder(fecha[2]);

            int mes = Integer.parseInt(fecha[1]);
            if(mes<10)
                id_doc.append("0").append(mes);
            else
                id_doc.append(mes);

            int dia = Integer.parseInt(fecha[0]);
            if(dia<10)
                id_doc.append("0").append(dia);
            else
                id_doc.append(dia);


            db.collection("backup")
                    .document(id_collection)
                    .collection("registros")
                    .document(id_doc+"")
                    .set(map);
        }
        Snackbar.make(fab,"Agregado",Snackbar.LENGTH_LONG).show();
    }
}