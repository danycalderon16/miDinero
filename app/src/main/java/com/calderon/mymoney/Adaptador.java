package com.calderon.mymoney;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Adaptador extends RecyclerView.Adapter<Adaptador.ViewHolder> {

    private List<Registro> list;
    private Activity activity;
    private SharedPreferences preferences;

    public Adaptador(List<Registro> list, Activity activity) {
        this.list = list;
        this.activity = activity;
        preferences = activity.getSharedPreferences("DATA",MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Adaptador.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adaptador.ViewHolder holder, final int position) {
        holder.bind(list.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean expanded = list.get(position).isExpanded();
                list.get(position).setExpanded(!expanded);
                notifyItemChanged(position);
            }

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public  class ViewHolder extends RecyclerView.ViewHolder implements MenuItem.OnMenuItemClickListener, View.OnCreateContextMenuListener {

        public TextView total;
        public TextView fecha;
        public TextView capital;
        public TextView invertido;
        public TextView ahorrado;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            total = itemView.findViewById(R.id.tvTotal);
            fecha = itemView.findViewById(R.id.tvFecha);
            capital = itemView.findViewById(R.id.exCapital);
            ahorrado = itemView.findViewById(R.id.exAhorrado);
            invertido = itemView.findViewById(R.id.exInv);
            linearLayout = itemView.findViewById(R.id.linearLayoutDetails1);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(final Registro registro) {
            boolean expanded = registro.isExpanded();
            total.setText(String.format(Locale.getDefault(),"%s%.2f", "$",registro.getTotal()));
            capital.setText(String.format(Locale.getDefault(),"%s%.2f", "$",registro.getCapital()));
            invertido.setText(String.format(Locale.getDefault(),"%s%.2f", "$",registro.getInvertido()));
            ahorrado.setText(String.format(Locale.getDefault(),"%s%.2f", "$",registro.getAhorrado()));
            String f[] = registro.getFecha().split("/");
            fecha.setText(String.format(Locale.getDefault(),"%s de %s de %s",f[0] , lagerMonth(registro) , f[2]));
            linearLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);

        }


        public void saveData(SharedPreferences preferences, List<Registro> registros) {
            SharedPreferences.Editor editor = preferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(registros);
            editor.putString("data", json);
            editor.apply();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            Registro reg = list.get(this.getAdapterPosition());

            menu.setHeaderTitle(reg.getFecha());
            MenuInflater inflater = activity.getMenuInflater();
            inflater.inflate(R.menu.menu_main,menu);
            for (int i=0;i<menu.size();i++){
                menu.getItem(i).setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle("Borrar");
                    alert.setMessage("¿Esta seguro de eliminar "+list.get(getAdapterPosition()).getFecha()+"?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            list.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            saveData(preferences,list);
                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alert.show();return true;
                default:
                    return false;
            }
        }

    }

    private String lagerMonth(Registro registro) {
        String c;
        String f[] = registro.getFecha().split("/");
        switch (f[1]) {
            case "1":
                c ="Enero";
                break;
            case "2":
                c= "Febrero";
                break;
            case "3":
                c= "Marzo";
                break;
            case "4":
                c= "Abril";
                break;
            case "5":
                c= "Mayo";
                break;
            case "6":
                c= "Junio";
                break;
            case "7":
                c= "Julio";
                break;
            case "8":
                c= "Agosto";
                break;
            case "9":
                c= "Septiembre";
                break;
            case "10":
                c= "Octubre";
                break;
            case "11":
                c= "Noviembre";
                break;
            case "12":
                c= "Diciembre";
                break;
            default:
                return "";
        }
        return  c;
    }

}
