package com.calderon.mymoney.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.calderon.mymoney.LoginActivity;
import com.calderon.mymoney.R;
import com.calderon.mymoney.adapters.Adaptador;
import com.calderon.mymoney.models.Registro;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.calderon.mymoney.utils.Util.loadData;
import static com.calderon.mymoney.utils.Util.saveData;
import static com.calderon.mymoney.utils.Util.setDate;

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
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


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
                setDate(c,v.getContext(),fecha);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_chart){
            Intent intent = new Intent(this, ChartActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.menu_cloud){
            Intent intent = new Intent(this, SaveActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.salir){
            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mAuth.signOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
//                    Log.i("$$$$$$$$$$$$$1",mAuth.getCurrentUser().getDisplayName()+" "+mAuth.getCurrentUser().getEmail());
                }
            });

        }

        return super.onOptionsItemSelected(item);
    }
}

