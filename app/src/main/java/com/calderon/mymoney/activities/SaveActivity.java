package com.calderon.mymoney.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.calderon.mymoney.adapters.SaveAdapter;
import com.calderon.mymoney.models.Registro;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.calderon.mymoney.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.calderon.mymoney.utils.Util.loadData;

public class SaveActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private List<Registro> list;
    private RecyclerView recyclerView;
    private SaveAdapter saveAdapter;

    private SharedPreferences preferences;

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferences = getSharedPreferences("DATA",MODE_PRIVATE);

        sendBind();
        sendRecyclerView();
        setSupportActionBar(toolbar);

        list = loadData(preferences,list);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab){
            addNewItem();
        }
    }

    private void sendRecyclerView(){
        usersRef = db.collection("usuarios")
                .document(mAuth.getCurrentUser().getUid())
                .collection("backup");

        Log.i("$$$$$$$$$$$4",mAuth.getCurrentUser().getUid());
        Query query = usersRef;

        FirestoreRecyclerOptions<Registro> options =
                new FirestoreRecyclerOptions.Builder<Registro>()
                .setQuery(query,Registro.class)
                .build();

        Log.i("$$$$$$$$$$$4",options.getSnapshots().size()+"");

        saveAdapter = new SaveAdapter(options, new SaveAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Registro registro, int position) {
                Toast.makeText(SaveActivity.this,registro.toString(),Toast.LENGTH_SHORT).show();
                Log.i("$$$$$$$$$$$4",registro.toString());
            }
        }, SaveActivity.this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(saveAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        saveAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveAdapter.stopListening();
    }

    private void sendBind(){
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        recyclerView = findViewById(R.id.rv_saved);
    }

    private void addNewItem() {
        int i = list.size()-1;

        String id = list.get(i).getFecha()+"";
        String date[] = id.split("/");
        StringBuilder id_col;
        id_col = new StringBuilder(date[2]);

        int mes_col = Integer.parseInt(date[1]);
        if(mes_col<10)
            id_col.append("0").append(mes_col);
        else
            id_col.append(mes_col);

        int dia_col = Integer.parseInt(date[0]);
        if(dia_col<10)
            id_col.append("0").append(dia_col);
        else
            id_col.append(dia_col);

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

            if(i==0) {
                db.collection("usuarios")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("backup")
                        .document(id_col + "")
                        .set(map);
                Log.i("$$$$$$$$$$$4",registro.toString());
            }

            db.collection("usuarios")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("backup")
                    .document(id_col+"")
                    .collection("registros")
                    .document(id_doc+"")
                    .set(map);
            i--;
        }
        Snackbar.make(fab,"Agregado",Snackbar.LENGTH_LONG).show();
    }
}