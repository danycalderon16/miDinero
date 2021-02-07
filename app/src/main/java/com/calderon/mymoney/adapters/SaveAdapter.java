package com.calderon.mymoney.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.calderon.mymoney.R;
import com.calderon.mymoney.models.Registro;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Locale;

import static com.calderon.mymoney.utils.Util.lagerMonth;

public class SaveAdapter extends FirestoreRecyclerAdapter<Registro,SaveAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private Activity activity;

    public SaveAdapter(@NonNull FirestoreRecyclerOptions<Registro> options, OnItemClickListener listener, Activity activity) {
        super(options);
        this.listener = listener;
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i, @NonNull final Registro registro) {
        String f[] = registro.getFecha().split("/");
        viewHolder.tv_fecha.setText(String.format(Locale.getDefault(),"%s de %s de %s",f[0] , lagerMonth(registro) , f[2]));
        viewHolder.tv_total.setText(String.format(Locale.getDefault(),"$%.2f",registro.getTotal()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(registro,i,getSnapshots().getSnapshot(i).getId());
            }
        });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_fecha;
        TextView tv_total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_fecha = itemView.findViewById(R.id.tv_fecha);
            tv_total = itemView.findViewById(R.id.tv_total);
        }
    }

    public interface  OnItemClickListener{
        void onItemClick(Registro registro, int position,String id);
    }
}
